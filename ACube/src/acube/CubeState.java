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

  public CubeState(Corner[] corners, Edge[] edges, int[] twists, int[] flips) {
    this.corners = corners;
    this.edges = edges;
    this.twists = twists;
    this.flips = flips;
    checkValidity();
  }

  private void checkValidity() {
    // for fully defined orientation the sum of orientations mod 3 (for corners, mod 2 for edges) must be zero
    // if the orientations are not all specified than we can always solve the remaining orientations
    if (CoderTools.valuesUsed(twists) == twists.length && CoderTools.totalOrientation(twists, 3) != 0)
      throw new RuntimeException("Unsolvable corner twists");
    if (CoderTools.valuesUsed(flips) == flips.length && CoderTools.totalOrientation(flips, 2) != 0)
      throw new RuntimeException("Unsolvable edge flips");
    // if all positions of corners/edges are known (or just one missing -> it is determined anyway)
    // we need the two parities to match (for every piece swap there must be another piece swap)
    int[] c = Corner.ordinals(corners);
    int[] e = Edge.ordinals(edges);
    if (CoderTools.valuesUsed(c) >= c.length - 1 && CoderTools.valuesUsed(e) >= e.length - 1)
      if (CoderTools.permutationParity(c) != CoderTools.permutationParity(e))
        throw new RuntimeException("Unsolvable corner and edge swaps");
  }

  public void solve(Metric metric, EnumSet<Turn> turns, int maxLength, boolean findAll, Reporter r) {
    r.solvingStarted(reidString());
    // extract generic information from this configuration that fully specifies transformation and pruning tables
    // the tables can be reused for many similar configurations (with the same sets ignored etc.)
    EnumSet<Corner> knownCorners = getKnownMask(corners, Corner.class);
    EnumSet<Edge> knownEdges = getKnownMask(edges, Edge.class);
    EnumSet<Corner> knownTwisted = getKnownOrientedMask(corners, twists, Corner.class);
    EnumSet<Edge> knownFlipped = getKnownOrientedMask(edges, flips, Edge.class);
    int unknownTwisted = getUnknownOrientedCount(corners, twists);
    int unknownFlipped = getUnknownOrientedCount(edges, flips);
    // create a solver and configure it with transformation and pruning tables
    TwoPhaseSolver solver = new TwoPhaseSolver(turns, metric, r);
    solver.setFindAll(findAll);
    solver.setMaxSearchLength(maxLength);
    Transform transform = new Transform(knownCorners, knownEdges, knownTwisted, knownFlipped, unknownTwisted, unknownFlipped, r);
    PruneA pruneA = new PruneA(transform, r);
    TransformB transformB = new TransformB(knownCorners, knownEdges, r);
    PruneB pruneB = new PruneB(transformB, r);
    solver.setTables(transform, transformB, pruneA, pruneB);
    // run the solver with this particular configuration - encoded by the tables used to a compact one
    solver.solve(encodeCube(transform));
  }

  public void solveOptimal(Metric metric, EnumSet<Turn> turns, int maxLength, boolean findAll, Reporter r) {
    r.solvingStarted(reidString());
    // optimal solver does not use Phase B, so it creates less tables
    EnumSet<Corner> knownCorners = getKnownMask(corners, Corner.class);
    EnumSet<Edge> knownEdges = getKnownMask(edges, Edge.class);
    EnumSet<Corner> knownTwisted = getKnownOrientedMask(corners, twists, Corner.class);
    EnumSet<Edge> knownFlipped = getKnownOrientedMask(edges, flips, Edge.class);
    int unknownTwisted = getUnknownOrientedCount(corners, twists);
    int unknownFlipped = getUnknownOrientedCount(edges, flips);
    OptimalSolver solver = new OptimalSolver(turns, metric, r);
    solver.setFindAll(findAll);
    solver.setMaxSearchLength(maxLength);
    Transform transform = new Transform(knownCorners, knownEdges, knownTwisted, knownFlipped, unknownTwisted, unknownFlipped, r);
    Prune prune = new Prune(transform, r);
    solver.setTables(transform, prune);
    solver.solve(encodeCube(transform));
  }

  // makes the cube representation compact
  // solver does not use arrays for the configuration (for efficiency reasons)
  // it uses a vecor in the state space
  // each axis of the vector is one number representing e.g. corner permutation, edge orientation, ...
  private EncodedCube encodeCube(Transform transform) {
    return transform.encode(SymTransform.I, corners, edges, twists, flips);
  }

  // string representation in extended 'Reid' notation (used by other programs)
  public String reidString() {
    StringBuilder s = new StringBuilder();
    for (int i = 0; i < edges.length; i++)
      s.append(' ').append(Edge.name(edges[i], flips[i]));
    for (int i = 0; i < corners.length; i++)
      s.append(' ').append(Corner.name(corners[i], twists[i]));
    return s.substring(1);
  }

  // returns string enumerating all pieces that have their positions ignored
  public String ignoredPositionsString() {
    StringBuilder s = new StringBuilder();
    for (Edge e : EnumSet.complementOf(getKnownMask(edges, Edge.class)))
      s.append(' ').append(e.toString());
    for (Corner c : EnumSet.complementOf(getKnownMask(corners, Corner.class)))
      s.append(' ').append(c.toString());
    return s.length() > 0 ? s.substring(1) : "";
  }

  // returs a string enumerating potential pieces that do not have their orientation defined
  public String ignoredOrientationsString() {
    StringBuilder s = new StringBuilder();
    EnumSet<Edge> unknownEdges = EnumSet.complementOf(getKnownMask(edges, Edge.class));
    EnumSet<Corner> unknownCorners = EnumSet.complementOf(getKnownMask(corners, Corner.class));
    if (unknownEdges.size() > 0) {
      s.append(getUnknownUnorientedCount(edges, flips)).append(" of {");
      for (Edge e : unknownEdges)
        s.append(" ").append(e.toString());
      s.append(" }");
    }
    if (unknownCorners.size() > 0) {
      if (s.length() > 0)
        s.append(", ");
      s.append(getUnknownUnorientedCount(corners, twists)).append(" of {");
      for (Corner c : unknownCorners)
        s.append(" ").append(c.toString());
      s.append(" }");
    }
    return s.toString();
  }

  // returns a set of pieces that have their position defined
  private <T extends Enum<T>> EnumSet<T> getKnownMask(T[] a, Class<T> type) {
    EnumSet<T> set = EnumSet.noneOf(type);
    for (T ai : a)
      if (ai != null)
        set.add(ai);
    return set;
  }

  // returns a set of pieces that have both position and orientation defined
  private <T extends Enum<T>> EnumSet<T> getKnownOrientedMask(T[] a, int[] o, Class<T> type) {
    EnumSet<T> set = EnumSet.noneOf(type);
    for (int i = 0; i < a.length; i++)
      if (o[i] >= 0)
        if (a[i] != null)
          set.add(a[i]);
    return set;
  }

  // counts how many unknown pieces have orientation defined
  private <T extends Enum<T>> int getUnknownOrientedCount(T[] a, int[] o) {
    int count = 0;
    for (int i = 0; i < a.length; i++)
      if (o[i] >= 0)
        if (a[i] == null)
          count++;
    return count;
  }

  // counts how many pieces have neither position nor orientation defined
  private <T extends Enum<T>> int getUnknownUnorientedCount(T[] a, int[] o) {
    int count = 0;
    for (int i = 0; i < a.length; i++)
      if (o[i] < 0)
        if (a[i] == null)
          count++;
    return count;
  }
}
