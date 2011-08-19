package acube;

import java.util.Set;
import acube.prune.Prune;
import acube.prune.PruneB;
import acube.transform.Transform;
import acube.transform.TransformB;

public final class CubeState {
  public Set<Turn> turnMask = Turn.valueSet;
  public Set<Corner> cornerTwistMask = Corner.valueSet;
  public Set<Edge> edgeFlipMask = Edge.valueSet;
  public Set<Corner> cornerMask = Corner.valueSet;
  public Set<Edge> edgeMask = Edge.valueSet;
  public int cornerTwist;
  public int edgeFlip;
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

  public void prepareTables(final Options options) {
    transform = new Transform(cornerMask, edgeMask, cornerTwistMask, edgeFlipMask, turnMask);
    prune = new Prune(transform);
    turnList = new TurnList(transform);
    if (!options.findOptimal) {
      transformB = new TransformB(cornerMask, edgeMask, turnMask);
      pruneB = new PruneB(transformB);
    }
  }

  public void setCornerTwist(final int ct) {
    cornerTwist = ct;
  }

  public void setCornerPos(final int cp) {
    cornerPos = cp;
  }

  public void setEdgeFlip(final int ef) {
    edgeFlip = ef;
  }

  public void setEdgePos(final int mep, final int uep, final int dep) {
    mEdgePos = mep;
    uEdgePos = uep;
    dEdgePos = dep;
    mEdgePosSet = transform.convert_mEdgePos_to_mEdgePosSet(mep);
  }
}
