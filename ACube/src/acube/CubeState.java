package acube;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import acube.prune.Prune;
import acube.prune.PruneB;
import acube.transform.Transform;
import acube.transform.TransformB;

public final class CubeState {
  private final Corner[] corners;
  private final Edge[] edges;
  private final int[] twists;
  private final int[] flips;
  private final EnumSet<Corner> twistMask = Corner.valueSet;
  private final EnumSet<Edge> flipMask = Edge.valueSet;
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

  public void solve(final Metric metric, final EnumSet<Turn> turns, final int maxLength, final boolean findAll,
      final Reporter reporter) {
    reporter.solvingStarted(reidString());
    transform = new Transform(getMask(corners), getMask(edges), twistMask, flipMask, reporter);
    twist = transform.get_twist(twists);
    flip = transform.get_flip(flips);
    cornerPos = transform.get_cornerPos(corners);
    mEdgePos = transform.get_mEdgePos(edges);
    uEdgePos = transform.get_uEdgePos(edges);
    dEdgePos = transform.get_dEdgePos(edges);
    mEdgePosSet = transform.get_mEdgePosSet(edges);
    prune = new Prune(transform, metric, reporter);
    turnList = new TurnList(transform, turns, reporter);
    transformB = new TransformB(getMask(corners), getMask(edges), reporter);
    pruneB = new PruneB(transformB, metric, reporter);
    new Solver(findAll, false, metric, reporter).solve(this, maxLength);
  }

  private <T extends Enum<T>> EnumSet<T> getMask(final T[] a) {
    final List<T> list = new ArrayList<T>();
    for (final T x : a)
      if (x != null)
        list.add(x);
    return EnumSet.copyOf(list);
  }
}
