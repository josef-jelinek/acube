package acube.transform;

import acube.Corner;
import acube.Edge;
import acube.Turn;

public final class TransformB {

  private final Turn[] turns;

  public final ITableMove mEdgePosition;
  public final ITableMove oEdgePosition;
  public final ITableMove cornerPosition;

  public static TransformB instance(Corner[] cornerMask, Edge[] edgeMask, Turn[] turns) {
    return new TransformB(cornerMask, edgeMask, turns);
  }

  private TransformB(Corner[] cornerMask, Edge[] edgeMask, Turn[] turns) {
    this.turns = Turn.getValidB(turns).clone();
    mEdgePosition = MoveKitB.mEdgePosition(edgeMask, this.turns.clone());
    oEdgePosition = MoveKitB.oEdgePosition(edgeMask, this.turns.clone());
    cornerPosition = MoveKit.cornerPosition(cornerMask, this.turns.clone());
  }

  public Turn[] turns() {
    return turns.clone();
  }
}
