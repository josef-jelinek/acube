package acube.transform;

import acube.Edge;
import acube.Turn;

public final class MoveKitB {
  private static final Turn[][][] TurnBase = {
      { { Turn.U1 }, { Turn.U1 } },
      { { Turn.D1 }, { Turn.D1 } },
      { { Turn.F2 }, { Turn.F2 } },
      { { Turn.B2 }, { Turn.B2 } },
      { { Turn.L2 }, { Turn.L2 } },
      { { Turn.R2 }, { Turn.R2 } },
      // derived
      { { Turn.u1 }, { Turn.D1 } }, { { Turn.d1 }, { Turn.U1 } }, { { Turn.f2 }, { Turn.B2 } },
      { { Turn.b2 }, { Turn.F2 } }, { { Turn.l2 }, { Turn.R2 } }, { { Turn.r2 }, { Turn.L2 } },
      { { Turn.U2 }, { Turn.U1, Turn.U1 } }, { { Turn.D2 }, { Turn.D1, Turn.D1 } },
      { { Turn.u2 }, { Turn.D1, Turn.D1 } }, { { Turn.d2 }, { Turn.U1, Turn.U1 } },
      { { Turn.U3 }, { Turn.U1, Turn.U1, Turn.U1 } },
      { { Turn.D3 }, { Turn.D1, Turn.D1, Turn.D1 } },
      { { Turn.u3 }, { Turn.D1, Turn.D1, Turn.D1 } },
      { { Turn.d3 }, { Turn.U1, Turn.U1, Turn.U1 } },
      { { Turn.E1 }, { Turn.U1, Turn.D1, Turn.D1, Turn.D1 } },
      { { Turn.E2 }, { Turn.U1, Turn.U1, Turn.D1, Turn.D1 } },
      { { Turn.E3 }, { Turn.U1, Turn.U1, Turn.U1, Turn.D1 } },
      { { Turn.S2 }, { Turn.B2, Turn.F2 } }, { { Turn.M2 }, { Turn.R2, Turn.L2 } }, };

  public static TurnTable mEdgePosition(final Edge[] edgeMask, final Turn[] turns) {
    checkForTurnB(turns);
    return MoveTable.instance(new MEdgePositionB(edgeMask, turns), TurnBase);
  }

  public static TurnTable oEdgePosition(final Edge[] edgeMask, final Turn[] turns) {
    checkForTurnB(turns);
    return MoveTable.instance(new OEdgePositionB(edgeMask, turns), TurnBase);
  }

  private static void checkForTurnB(final Turn[] turns) {
    for (final Turn turn : turns)
      assert turn.isB();
  }

  private MoveKitB() {}
}
