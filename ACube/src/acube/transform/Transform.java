package acube.transform;

import acube.Corner;
import acube.Edge;
import acube.Turn;

public final class Transform {

  private final Turn[] turns;

  public final ITableMove cornerTwist;
  public final ITableMove edgeFlip;
  public final ITableMove mEdgePositionSet;
  public final ITableMove mEdgePosition;
  public final ITableMove uEdgePosition;
  public final ITableMove dEdgePosition;
  public final ITableMove cornerPosition;

  public static Transform instance(Corner[] cornerMask, Edge[] edgeMask, Corner[] cornerTwistMask, Edge[] edgeFlipMask, Turn[] turns) {
    return new Transform(cornerMask, edgeMask, cornerTwistMask, edgeFlipMask, turns);
  }

  private Transform(Corner[] cornerMask, Edge[] edgeMask, Corner[] cornerTwistMask, Edge[] edgeFlipMask, Turn[] turns) {
    this.turns = turns.clone();
    cornerTwist = MoveKit.cornerTwist(cornerMask, cornerTwistMask, turns.clone());
    edgeFlip = MoveKit.edgeFlip(edgeMask, edgeFlipMask, turns.clone());
    mEdgePositionSet = MoveKit.mEdgePositionSet(edgeMask, turns.clone());
    mEdgePosition = MoveKit.mEdgePosition(edgeMask, turns.clone());
    uEdgePosition = MoveKit.uEdgePosition(edgeMask, turns.clone());
    dEdgePosition = MoveKit.dEdgePosition(edgeMask, turns.clone());
    cornerPosition = MoveKit.cornerPosition(cornerMask, turns.clone());
  }

  public Turn[] turns() {
    return turns.clone();
  }
}
