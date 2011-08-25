package acube;

import java.util.EnumSet;
import acube.prune.Prune;
import acube.prune.PruneB;
import acube.transform.Transform;
import acube.transform.TransformB;

public final class CubeState {
  private final Corner[] corners;
  private final Edge[] edges;
  private final int[] cornerTwists;
  private final int[] edgeFlips;
  private final EnumSet<Corner> cornerMask = Corner.valueSet;
  private final EnumSet<Edge> edgeMask = Edge.valueSet;
  private final EnumSet<Corner> twistMask = Corner.valueSet;
  private final EnumSet<Edge> flipMask = Edge.valueSet;
  private final EnumSet<Turn> turnMask = Turn.valueSet;
  public int twist;
  public int flip;
  public int mEdgePosSet;
  public int cornerPos;
  public int mEdgePos;
  public int uEdgePos;
  public int dEdgePos;
  public Metric metric = Metric.SLICE;
  public int symmetry = SymTransform.I;
  public Transform transform;
  public TransformB transformB;
  public Prune prune;
  public PruneB pruneB;
  public TurnList turnList;

  public CubeState(final Corner[] corners, final Edge[] edges, final int[] cornerTwists, final int[] edgeFlips) {
    this.corners = corners;
    this.edges = edges;
    this.cornerTwists = cornerTwists;
    this.edgeFlips = edgeFlips;
  }

  public String toReid() {
    final StringBuilder s = new StringBuilder();
    for (int i = 0; i < edges.length; i++)
      s.append(' ').append(edges[i].name(edgeFlips[i]));
    for (int i = 0; i < corners.length; i++)
      s.append(' ').append(corners[i].name(cornerTwists[i]));
    return s.substring(1);
  }

  public void prepareTables(final Options options) {
    transform = new Transform(cornerMask, edgeMask, twistMask, flipMask, turnMask);
    prune = new Prune(transform);
    turnList = new TurnList(transform);
    if (!options.findOptimal) {
      transformB = new TransformB(cornerMask, edgeMask, turnMask);
      pruneB = new PruneB(transformB);
    }
  }

  public void setCornerTwist(final int ct) {
    twist = ct;
  }

  public void setCornerPos(final int cp) {
    cornerPos = cp;
  }

  public void setEdgeFlip(final int ef) {
    flip = ef;
  }

  public void setEdgePos(final int mep, final int uep, final int dep) {
    mEdgePos = mep;
    uEdgePos = uep;
    dEdgePos = dep;
    mEdgePosSet = transform.convert_mEdgePos_to_mEdgePosSet(mep);
  }
}
