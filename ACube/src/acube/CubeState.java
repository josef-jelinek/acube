package acube;

import java.util.EnumSet;
import acube.pack.CoderTools;
import acube.prune.Prune;
import acube.prune.PruneB;
import acube.transform.Transform;
import acube.transform.TransformB;

public final class CubeState {
  private final Corner[] corners;
  private final Edge[] edges;
  private final int[] twists;
  private final int[] flips;
  public int twist;
  public int flip;
  public int twistFull;
  public int flipFull;
  public int mEdgePosSet;
  public int cornerPos;
  public int mEdgePos;
  public int uEdgePos;
  public int dEdgePos;
  public int symmetry = SymTransform.I;
  public Transform transform;
  public TransformB transformB;
  public Prune prune;
  public PruneB pruneB;
  public TurnList turnList;
  public TurnList turnListB;

  //public TurnList turnListB;
  public CubeState(final Corner[] corners, final Edge[] edges, final int[] twists, final int[] flips) {
    this.corners = corners;
    this.edges = edges;
    this.twists = twists;
    this.flips = flips;
    checkIfValid();
  }

  private void checkIfValid() {
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

  public String reidString() {
    final StringBuilder s = new StringBuilder();
    for (int i = 0; i < edges.length; i++)
      s.append(' ').append(Edge.name(edges[i], flips[i]));
    for (int i = 0; i < corners.length; i++)
      s.append(' ').append(Corner.name(corners[i], twists[i]));
    return s.substring(1);
  }

  public String ignoredPositionsString() {
    final StringBuilder s = new StringBuilder();
    for (final Edge e : EnumSet.complementOf(getKnownMask(edges, Edge.class)))
      s.append(' ').append(e.toString());
    for (final Corner c : EnumSet.complementOf(getKnownMask(corners, Corner.class)))
      s.append(' ').append(c.toString());
    return s.length() > 0 ? s.substring(1) : "";
  }

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

  public void solve(final Metric metric, final EnumSet<Turn> turns, final int maxLength, final boolean findAll,
      final Reporter r) {
    r.solvingStarted(reidString());
    final EnumSet<Corner> cornerMask = getKnownMask(corners, Corner.class);
    final EnumSet<Edge> edgeMask = getKnownMask(edges, Edge.class);
    final EnumSet<Corner> knownTwistMask = getKnownOrientedMask(corners, twists, Corner.class);
    final EnumSet<Edge> knownFlipMask = getKnownOrientedMask(edges, flips, Edge.class);
    final int unknownTwisted = getUnknownOrientedCount(corners, twists);
    final int unknownFlipped = getUnknownOrientedCount(edges, flips);
    r.tableCreationStarted("turn transformation and pruning table");
    turnList = new TurnList(turns, metric, true);
    turnListB = new TurnList(turns, metric, false);
    transform = new Transform(cornerMask, edgeMask, knownTwistMask, knownFlipMask, unknownTwisted, unknownFlipped, r);
    twist = transform.get_twist(twists);
    flip = transform.get_flip(flips);
    cornerPos = transform.get_cornerPos(corners);
    mEdgePos = transform.get_mEdgePos(edges);
    uEdgePos = transform.get_uEdgePos(edges);
    dEdgePos = transform.get_dEdgePos(edges);
    mEdgePosSet = transform.get_mEdgePosSet(edges);
    prune = new Prune(transform, r);
    transformB = new TransformB(cornerMask, edgeMask, r);
    pruneB = new PruneB(transformB, r);
    new Solver(findAll, false, metric, r).solve(this, maxLength);
  }

  private <T extends Enum<T>> EnumSet<T> getKnownMask(final T[] a, final Class<T> type) {
    final EnumSet<T> set = EnumSet.noneOf(type);
    for (final T ai : a)
      if (ai != null)
        set.add(ai);
    return set;
  }

  private <T extends Enum<T>> EnumSet<T> getKnownOrientedMask(final T[] a, final int[] o, final Class<T> type) {
    final EnumSet<T> set = EnumSet.noneOf(type);
    for (int i = 0; i < a.length; i++)
      if (o[i] >= 0)
        if (a[i] != null)
          set.add(a[i]);
    return set;
  }

  private <T extends Enum<T>> int getUnknownOrientedCount(final T[] a, final int[] o) {
    int count = 0;
    for (int i = 0; i < a.length; i++)
      if (o[i] >= 0)
        if (a[i] == null)
          count++;
    return count;
  }

  private <T extends Enum<T>> int getUnknownUnorientedCount(final T[] a, final int[] o) {
    int count = 0;
    for (int i = 0; i < a.length; i++)
      if (o[i] < 0)
        if (a[i] == null)
          count++;
    return count;
  }
}
