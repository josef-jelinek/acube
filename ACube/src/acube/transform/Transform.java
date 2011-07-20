package acube.transform;

import acube.Corner;
import acube.Edge;
import acube.Turn;

public final class Transform {
  private final Turn[] turns;
  public final TurnTable cornerTwist;
  public final TurnTable edgeFlip;
  public final TurnTable mEdgePositionSet;
  public final TurnTable mEdgePosition;
  public final TurnTable uEdgePosition;
  public final TurnTable dEdgePosition;
  public final TurnTable cornerPosition;

  public Transform(final Corner[] cornerMask, final Edge[] edgeMask, final Corner[] cornerTwistMask,
      final Edge[] edgeFlipMask, final Turn[] turns) {
    this.turns = turns;
    cornerTwist = MoveKit.cornerTwist(cornerMask, cornerTwistMask, turns);
    edgeFlip = MoveKit.edgeFlip(edgeMask, edgeFlipMask, turns);
    mEdgePositionSet = MoveKit.mEdgePositionSet(edgeMask, turns);
    mEdgePosition = MoveKit.mEdgePosition(edgeMask, turns);
    uEdgePosition = MoveKit.uEdgePosition(edgeMask, turns);
    dEdgePosition = MoveKit.dEdgePosition(edgeMask, turns);
    cornerPosition = MoveKit.cornerPosition(cornerMask, turns);
  }

  public Transform(final Turn[] turns) {
    this(Corner.values(), Edge.values(), Corner.values(), Edge.values(), turns);
  }

  public Turn[] turns() {
    return turns;
  }
}
