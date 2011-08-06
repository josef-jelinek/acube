package acube.transform;

import acube.Corner;
import acube.Edge;
import acube.Turn;

public final class TransformB {
  private final Turn[] turns;
  public final TurnTable mEdgePosition;
  public final TurnTable oEdgePosition;
  public final TurnTable cornerPosition;
  private final boolean[] mEdgePositionInB;
  private final boolean[] uEdgePositionInB;
  private final boolean[] dEdgePositionInB;
  private final int[] uEdgePositionToB;
  private final int[] dEdgePositionToB;
  private final int[] mEdgePositionToB;
  private final int[][] udEdgePositionToB;

  public TransformB(final Corner[] cornerMask, final Edge[] edgeMask, final Turn[] turns) {
    this.turns = Turn.getValidB(turns);
    mEdgePosition = MoveKit.mEdgePositionB(edgeMask, this.turns);
    oEdgePosition = MoveKit.oEdgePositionB(edgeMask, this.turns);
    cornerPosition = MoveKit.cornerPosition(cornerMask, this.turns);
    mEdgePositionInB = MoveKit.getIsMEdgePositionInB(edgeMask, turns);
    uEdgePositionInB = MoveKit.getIsUEdgePositionInB(edgeMask, turns);
    dEdgePositionInB = MoveKit.getIsDEdgePositionInB(edgeMask, turns);
    mEdgePositionToB = MoveKit.getMEdgePositionToB(edgeMask, turns);
    uEdgePositionToB = MoveKit.getUEdgePositionToB(edgeMask, turns);
    dEdgePositionToB = MoveKit.getDEdgePositionToB(edgeMask, turns);
    udEdgePositionToB = MoveKit.getUDEdgePositionBToB(edgeMask, turns);
  }

  public Turn[] turns() {
    return turns;
  }

  public boolean isMEdgePositionInB(final int state) {
    return mEdgePositionInB[state];
  }

  public boolean isUEdgePositionInB(final int state) {
    return uEdgePositionInB[state];
  }

  public boolean isDEdgePositionInB(final int state) {
    return dEdgePositionInB[state];
  }

  public int convertToMEdgePosition(final int state) {
    return mEdgePositionToB[state];
  }

  public int convertToOEdgePosition(final int uState, final int dState) {
    return udEdgePositionToB[uEdgePositionToB[uState]][dEdgePositionToB[dState]];
  }
}
