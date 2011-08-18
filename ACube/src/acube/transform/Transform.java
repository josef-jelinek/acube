package acube.transform;

import java.util.Set;
import acube.Corner;
import acube.Edge;
import acube.Turn;

public final class Transform {
  private final Set<Turn> turnMask;
  public final TurnTable cornerTwist;
  public final TurnTable edgeFlip;
  public final TurnTable mEdgePosSet;
  public final TurnTable mEdgePos;
  public final TurnTable uEdgePos;
  public final TurnTable dEdgePos;
  public final TurnTable cornerPos;
  private final int[] mEdgePos_to_mEdgePosSet;

  public Transform(final Set<Corner> cornerMask, final Set<Edge> edgeMask, final Set<Corner> cornerTwistMask,
      final Set<Edge> edgeFlipMask, final Set<Turn> turnMask) {
    this.turnMask = turnMask;
    cornerTwist = MoveKit.cornerTwist(cornerMask, cornerTwistMask, turnMask);
    edgeFlip = MoveKit.edgeFlip(edgeMask, edgeFlipMask, turnMask);
    mEdgePosSet = MoveKit.mEdgePosSet(edgeMask, turnMask);
    mEdgePos = MoveKit.mEdgePos(edgeMask, turnMask);
    uEdgePos = MoveKit.uEdgePos(edgeMask, turnMask);
    dEdgePos = MoveKit.dEdgePos(edgeMask, turnMask);
    cornerPos = MoveKit.cornerPos(cornerMask, turnMask);
    mEdgePos_to_mEdgePosSet = MoveKit.get_mEdgePos_to_mEdgePosSet(edgeMask, turnMask);
  }

  public Transform(final Set<Turn> turnMask) {
    this(Corner.valueSet, Edge.valueSet, Corner.valueSet, Edge.valueSet, turnMask);
  }

  public Set<Turn> turnMask() {
    return turnMask;
  }

  public int convert_mEdgePos_to_mEdgePosSet(final int mep) {
    return mEdgePos_to_mEdgePosSet[mep];
  }
}
