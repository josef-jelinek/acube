package acube;

import java.util.EnumSet;
import acube.pack.CoderTools;
import acube.prune.Prune;
import acube.prune.PruneA;
import acube.prune.PruneB;
import acube.transform.EncodedCube;
import acube.transform.Transform;
import acube.transform.TransformB;


public final class CubeState {

  // corners and edges are used to store permutations regardless of orientations of the pieces
  private final Corner[] corners; // null if the corner at the position is unknown
  private final Edge[] edges; // null if the edge at the position is unknown
  // twists (for corners) and flips (for edges) store orientations of the pieces at the corresponding positions
  private final int[] twists; // values mod 3: 0, 1, 3 (-1 for ignored)
  private final int[] flips;  // values mod 2: 0, 1 (-1 for ignored)

  public CubeState(final Corner[] corners, final Edge[] edges, final int[] twists, final int[] flips) {
    this.corners = corners;
    this.edges = edges;
    this.twists = twists;
    this.flips = flips;
    checkValidity();
  }

  private void checkValidity() {
    if (CoderTools.valuesUsed(twists) == twists.length && CoderTools.totalOrientation(twists, 3) != 0)
      throw new RuntimeException("Unsolvable corner twists");
    if (CoderTools.valuesUsed(flips) == flips.length && CoderTools.totalOrientation(flips, 2) != 0)
      throw new RuntimeException("Unsolvable edge flips");
    final int[] c = Corner.ordinals(corners);
    final int[] e = Edge.ordinals(edges);
    if (CoderTools.valuesUsed(c) >= c.length - 1 && CoderTools.valuesUsed(e) >= e.length - 1)
      if (CoderTools.permutationParity(c) != CoderTools.permutationParity(e))
        throw new RuntimeException("Unsolvable corner and edge swaps");
  }

  public void solve(final Metric metric, final EnumSet<Turn> turns, final int maxLength, final boolean findAll,
      final Reporter r) {
    r.solvingStarted(reidString());
    final EnumSet<Corner> knownCorners = getKnownMask(corners, Corner.class);
    final EnumSet<Edge> knownEdges = getKnownMask(edges, Edge.class);
    final EnumSet<Corner> knownTwisted = getKnownOrientedMask(corners, twists, Corner.class);
    final EnumSet<Edge> knownFlipped = getKnownOrientedMask(edges, flips, Edge.class);
    final int unknownTwisted = getUnknownOrientedCount(corners, twists);
    final int unknownFlipped = getUnknownOrientedCount(edges, flips);
    final TwoPhaseSolver solver = new TwoPhaseSolver(turns, metric, r);
    solver.setFindAll(findAll);
    solver.setMaxSearchLength(maxLength);
    final Transform transform =
        new Transform(knownCorners, knownEdges, knownTwisted, knownFlipped, unknownTwisted, unknownFlipped, r);
    final PruneA pruneA = new PruneA(transform, r);
    final TransformB transformB = new TransformB(knownCorners, knownEdges, r);
    final PruneB pruneB = new PruneB(transformB, r);
    solver.setTables(transform, transformB, pruneA, pruneB);
    solver.solve(encodeCube(transform));
  }

  public void solveOptimal(final Metric metric, final EnumSet<Turn> turns, final int maxLength, final boolean findAll,
      final Reporter r) {
    r.solvingStarted(reidString());
    final EnumSet<Corner> knownCorners = getKnownMask(corners, Corner.class);
    final EnumSet<Edge> knownEdges = getKnownMask(edges, Edge.class);
    final EnumSet<Corner> knownTwisted = getKnownOrientedMask(corners, twists, Corner.class);
    final EnumSet<Edge> knownFlipped = getKnownOrientedMask(edges, flips, Edge.class);
    final int unknownTwisted = getUnknownOrientedCount(corners, twists);
    final int unknownFlipped = getUnknownOrientedCount(edges, flips);
    final OptimalSolver solver = new OptimalSolver(turns, metric, r);
    solver.setFindAll(findAll);
    solver.setMaxSearchLength(maxLength);
    final Transform transform =
        new Transform(knownCorners, knownEdges, knownTwisted, knownFlipped, unknownTwisted, unknownFlipped, r);
    final Prune prune = new Prune(transform, r);
    solver.setTables(transform, prune);
    solver.solve(encodeCube(transform));
  }

  private EncodedCube encodeCube(final Transform transform) {
    return transform.encode(SymTransform.I, corners, edges, twists, flips);
  }

  // string representation in extended 'Reid' notation (used by other programs)
  public String reidString() {
    final StringBuilder s = new StringBuilder();
    for (int i = 0; i < edges.length; i++)
      s.append(' ').append(Edge.name(edges[i], flips[i]));
    for (int i = 0; i < corners.length; i++)
      s.append(' ').append(Corner.name(corners[i], twists[i]));
    return s.substring(1);
  }

  // returns string enumerating all pieces that have their positions ignored
  public String ignoredPositionsString() {
    final StringBuilder s = new StringBuilder();
    for (final Edge e : EnumSet.complementOf(getKnownMask(edges, Edge.class)))
      s.append(' ').append(e.toString());
    for (final Corner c : EnumSet.complementOf(getKnownMask(corners, Corner.class)))
      s.append(' ').append(c.toString());
    return s.length() > 0 ? s.substring(1) : "";
  }

  // returs a string enumerating potential pieces that do not have their orientation defined
  public String ignoredOrientationsString() {
    final StringBuilder s = new StringBuilder();
    final EnumSet<Edge> unknownEdges = EnumSet.complementOf(getKnownMask(edges, Edge.class));
    final EnumSet<Corner> unknownCorners = EnumSet.complementOf(getKnownMask(corners, Corner.class));
    if (unknownEdges.size() > 0) {
      s.append(getUnknownUnorientedCount(edges, flips)).append(" of {");
      for (final Edge e : unknownEdges)
        s.append(" ").append(e.toString());
      s.append(" }");
    }
    if (unknownCorners.size() > 0) {
      if (s.length() > 0)
        s.append(", ");
      s.append(getUnknownUnorientedCount(corners, twists)).append(" of {");
      for (final Corner c : unknownCorners)
        s.append(" ").append(c.toString());
      s.append(" }");
    }
    return s.toString();
  }

  // returns a set of pieces that have their position defined
  private <T extends Enum<T>> EnumSet<T> getKnownMask(final T[] a, final Class<T> type) {
    final EnumSet<T> set = EnumSet.noneOf(type);
    for (final T ai : a)
      if (ai != null)
        set.add(ai);
    return set;
  }

  // returns a set of pieces that have both position and orientation defined
  private <T extends Enum<T>> EnumSet<T> getKnownOrientedMask(final T[] a, final int[] o, final Class<T> type) {
    final EnumSet<T> set = EnumSet.noneOf(type);
    for (int i = 0; i < a.length; i++)
      if (o[i] >= 0)
        if (a[i] != null)
          set.add(a[i]);
    return set;
  }

  // counts how many unknown pieces have orientation defined
  private <T extends Enum<T>> int getUnknownOrientedCount(final T[] a, final int[] o) {
    int count = 0;
    for (int i = 0; i < a.length; i++)
      if (o[i] >= 0)
        if (a[i] == null)
          count++;
    return count;
  }

  // counts how many pieces have neither position nor orientation defined
  private <T extends Enum<T>> int getUnknownUnorientedCount(final T[] a, final int[] o) {
    int count = 0;
    for (int i = 0; i < a.length; i++)
      if (o[i] < 0)
        if (a[i] == null)
          count++;
    return count;
  }
}
