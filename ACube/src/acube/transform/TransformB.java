package acube.transform;

import acube.Corner;
import acube.Edge;
import acube.Turn;

public final class TransformB {
  private final Turn[] turns;
  public final TurnTable mEdgePosition;
  public final TurnTable oEdgePosition;
  public final TurnTable cornerPosition;

  public static TransformB instance(final Corner[] cornerMask, final Edge[] edgeMask, final Turn[] turns) {
    return new TransformB(cornerMask, edgeMask, turns);
  }

  private TransformB(final Corner[] cornerMask, final Edge[] edgeMask, final Turn[] turns) {
    this.turns = Turn.getValidB(turns);
    mEdgePosition = MoveKitB.mEdgePosition(edgeMask, this.turns);
    oEdgePosition = MoveKitB.oEdgePosition(edgeMask, this.turns);
    cornerPosition = MoveKit.cornerPosition(cornerMask, this.turns);
  }

  public Turn[] turns() {
    return turns;
  }
}
