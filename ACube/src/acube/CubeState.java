package acube;

import java.util.EnumSet;
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

  public CubeState(final Corner[] corners, final Edge[] edges, final int[] cornerTwists, final int[] edgeFlips) {
    this.corners = corners;
    this.edges = edges;
    twists = cornerTwists;
    flips = edgeFlips;
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
    for (final Edge e : EnumSet.complementOf(getMask(edges, Edge.class)))
      s.append(' ').append(e.toString());
    for (final Corner c : EnumSet.complementOf(getMask(corners, Corner.class)))
      s.append(' ').append(c.toString());
    return s.length() > 0 ? s.substring(1) : "";
  }

  public String ignoredOrientationsString() {
    final StringBuilder s = new StringBuilder();
    for (final Edge e : EnumSet.complementOf(getMask(edges, flips, Edge.class)))
      s.append(' ').append(e.toString());
    for (final Corner c : EnumSet.complementOf(getMask(corners, twists, Corner.class)))
      s.append(' ').append(c.toString());
    return s.length() > 0 ? s.substring(1) : "";
  }

  public void solve(final Metric metric, final EnumSet<Turn> turns, final int maxLength, final boolean findAll,
      final Reporter r) {
    r.solvingStarted(reidString());
    final EnumSet<Corner> cornerMask = getMask(corners, Corner.class);
    final EnumSet<Edge> edgeMask = getMask(edges, Edge.class);
    final EnumSet<Corner> twistMask = getMask(corners, twists, Corner.class);
    final EnumSet<Edge> flipMask = getMask(edges, flips, Edge.class);
    transform = new Transform(cornerMask, edgeMask, twistMask, flipMask, r);
    twist = transform.get_twist(twists);
    flip = transform.get_flip(flips);
    cornerPos = transform.get_cornerPos(corners);
    mEdgePos = transform.get_mEdgePos(edges);
    uEdgePos = transform.get_uEdgePos(edges);
    dEdgePos = transform.get_dEdgePos(edges);
    mEdgePosSet = transform.get_mEdgePosSet(edges);
    prune = new Prune(transform, metric, r);
    turnList = new TurnList(transform, turns, r);
    transformB = new TransformB(cornerMask, edgeMask, r);
    pruneB = new PruneB(transformB, metric, r);
    new Solver(findAll, false, metric, r).solve(this, maxLength);
  }

  private <T extends Enum<T>> EnumSet<T> getMask(final T[] a, final Class<T> type) {
    final EnumSet<T> set = EnumSet.noneOf(type);
    for (final T x : a)
      if (x != null)
        set.add(x);
    return set;
  }

  private <T extends Enum<T>> EnumSet<T> getMask(final T[] a, final int[] o, final Class<T> type) {
    final EnumSet<T> set = EnumSet.noneOf(type);
    final EnumSet<T> undef = EnumSet.complementOf(getMask(a, type));
    for (int i = 0; i < a.length; i++)
      if (o[i] >= 0)
        if (a[i] != null)
          set.add(a[i]);
        else
          for (final T t : undef) {
            set.add(t);
            undef.remove(t);
            break;
          }
    return set;
  }
}
