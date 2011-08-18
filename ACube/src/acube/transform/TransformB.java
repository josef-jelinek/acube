package acube.transform;

import java.util.Set;
import acube.Corner;
import acube.Edge;
import acube.Turn;

public final class TransformB {
  private final Set<Turn> turnMask;
  public final TurnTable mEdgePos;
  public final TurnTable oEdgePos;
  public final TurnTable cornerPos;
  private final boolean[] mEdgePos_inB;
  private final boolean[] uEdgePos_inB;
  private final boolean[] dEdgePos_inB;
  private final int[] uEdgePos_toB;
  private final int[] dEdgePos_toB;
  private final int[] mEdgePos_toB;
  private final int[][] uEdgePos_dEdgePos_ToB;

  public TransformB(final Set<Corner> cornerMask, final Set<Edge> edgeMask, final Set<Turn> turnMask) {
    this.turnMask = Turn.getValidB(turnMask);
    mEdgePos = MoveKit.mEdgePos_B(edgeMask, this.turnMask);
    oEdgePos = MoveKit.oEdgePos_B(edgeMask, this.turnMask);
    cornerPos = MoveKit.cornerPos(cornerMask, this.turnMask);
    mEdgePos_inB = MoveKit.get_mEdgePos_inB(edgeMask, turnMask);
    uEdgePos_inB = MoveKit.get_uEdgePos_inB(edgeMask, turnMask);
    dEdgePos_inB = MoveKit.get_dEdgePos_inB(edgeMask, turnMask);
    mEdgePos_toB = MoveKit.get_mEdgePos_toB(edgeMask, turnMask);
    uEdgePos_toB = MoveKit.get_uEdgePos_toB(edgeMask, turnMask);
    dEdgePos_toB = MoveKit.get_dEdgePos_toB(edgeMask, turnMask);
    uEdgePos_dEdgePos_ToB = MoveKit.get_uEdgePos_B_dEdgePos_B_toB(edgeMask, turnMask);
  }

  public Set<Turn> turnMask() {
    return turnMask;
  }

  public boolean is_mEdgePos_inB(final int mep) {
    return mEdgePos_inB[mep];
  }

  public boolean is_uEdgePos_inB(final int uep) {
    return uEdgePos_inB[uep];
  }

  public boolean is_dEdgePos_inB(final int dep) {
    return dEdgePos_inB[dep];
  }

  public int convertTo_mEdgePos(final int mep) {
    return mEdgePos_toB[mep];
  }

  public int convertTo_oEdgePos(final int uep, final int dep) {
    return uEdgePos_dEdgePos_ToB[uEdgePos_toB[uep]][dEdgePos_toB[dep]];
  }
}
