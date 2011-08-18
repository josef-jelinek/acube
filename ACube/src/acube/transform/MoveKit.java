package acube.transform;

import static acube.Turn.B1;
import static acube.Turn.B2;
import static acube.Turn.B3;
import static acube.Turn.D1;
import static acube.Turn.D2;
import static acube.Turn.D3;
import static acube.Turn.E1;
import static acube.Turn.E2;
import static acube.Turn.E3;
import static acube.Turn.F1;
import static acube.Turn.F2;
import static acube.Turn.F3;
import static acube.Turn.L1;
import static acube.Turn.L2;
import static acube.Turn.L3;
import static acube.Turn.M1;
import static acube.Turn.M2;
import static acube.Turn.M3;
import static acube.Turn.R1;
import static acube.Turn.R2;
import static acube.Turn.R3;
import static acube.Turn.S1;
import static acube.Turn.S2;
import static acube.Turn.S3;
import static acube.Turn.U1;
import static acube.Turn.U2;
import static acube.Turn.U3;
import static acube.Turn.b1;
import static acube.Turn.b2;
import static acube.Turn.b3;
import static acube.Turn.d1;
import static acube.Turn.d2;
import static acube.Turn.d3;
import static acube.Turn.f1;
import static acube.Turn.f2;
import static acube.Turn.f3;
import static acube.Turn.l1;
import static acube.Turn.l2;
import static acube.Turn.l3;
import static acube.Turn.r1;
import static acube.Turn.r2;
import static acube.Turn.r3;
import static acube.Turn.u1;
import static acube.Turn.u2;
import static acube.Turn.u3;
import java.util.Arrays;
import java.util.Set;
import acube.Corner;
import acube.Edge;
import acube.Turn;
import acube.pack.PackKit;

public final class MoveKit {
  private static final Turn[][][] TurnBase = {
      { { U1 }, { U1 } }, { { D1 }, { D1 } }, { { F1 }, { F1 } }, { { B1 }, { B1 } }, { { L1 }, { L1 } },
      { { R1 }, { R1 } }, { { u1 }, { D1 } }, { { d1 }, { U1 } }, { { f1 }, { B1 } }, { { b1 }, { F1 } },
      { { l1 }, { R1 } }, { { r1 }, { L1 } }, { { U2 }, { U1, U1 } }, { { D2 }, { D1, D1 } }, { { F2 }, { F1, F1 } },
      { { B2 }, { B1, B1 } }, { { L2 }, { L1, L1 } }, { { R2 }, { R1, R1 } }, { { u2 }, { D1, D1 } },
      { { d2 }, { U1, U1 } }, { { f2 }, { B1, B1 } }, { { b2 }, { F1, F1 } }, { { l2 }, { R1, R1 } },
      { { r2 }, { L1, L1 } }, { { U3 }, { U1, U1, U1 } }, { { D3 }, { D1, D1, D1 } }, { { F3 }, { F1, F1, F1 } },
      { { B3 }, { B1, B1, B1 } }, { { L3 }, { L1, L1, L1 } }, { { R3 }, { R1, R1, R1 } }, { { u3 }, { D1, D1, D1 } },
      { { d3 }, { U1, U1, U1 } }, { { f3 }, { B1, B1, B1 } }, { { b3 }, { F1, F1, F1 } }, { { l3 }, { R1, R1, R1 } },
      { { r3 }, { L1, L1, L1 } }, { { E1 }, { U1, D1, D1, D1 } }, { { E2 }, { U1, U1, D1, D1 } },
      { { E3 }, { U1, U1, U1, D1 } }, { { S1 }, { B1, F1, F1, F1 } }, { { S2 }, { B1, B1, F1, F1 } },
      { { S3 }, { B1, B1, B1, F1 } }, { { M1 }, { R1, L1, L1, L1 } }, { { M2 }, { R1, R1, L1, L1 } },
      { { M3 }, { R1, R1, R1, L1 } } };
  private static final Turn[][][] TurnBaseB = {
      { { U1 }, { U1 } }, { { D1 }, { D1 } }, { { F2 }, { F2 } }, { { B2 }, { B2 } }, { { L2 }, { L2 } },
      { { R2 }, { R2 } }, { { u1 }, { D1 } }, { { d1 }, { U1 } }, { { f2 }, { B2 } }, { { b2 }, { F2 } },
      { { l2 }, { R2 } }, { { r2 }, { L2 } }, { { U2 }, { U1, U1 } }, { { D2 }, { D1, D1 } }, { { u2 }, { D1, D1 } },
      { { d2 }, { U1, U1 } }, { { U3 }, { U1, U1, U1 } }, { { D3 }, { D1, D1, D1 } }, { { u3 }, { D1, D1, D1 } },
      { { d3 }, { U1, U1, U1 } }, { { E1 }, { U1, D1, D1, D1 } }, { { E2 }, { U1, U1, D1, D1 } },
      { { E3 }, { U1, U1, U1, D1 } }, { { S2 }, { B2, F2 } }, { { M2 }, { R2, L2 } }, };

  public static TurnTable cornerTwist(final Set<Corner> cornerMask, final Set<Corner> cornerTwistMask,
      final Set<Turn> turnMask) {
    return MoveTable.instance(new CornerTwist(cornerMask, cornerTwistMask, turnMask), TurnBase);
  }

  public static TurnTable edgeFlip(final Set<Edge> edgeMask, final Set<Edge> edgeFlipMask, final Set<Turn> turnMask) {
    return MoveTable.instance(new EdgeFlip(edgeMask, edgeFlipMask, turnMask), TurnBase);
  }

  public static TurnTable cornerPos(final Set<Corner> cornerMask, final Set<Turn> turnMask) {
    return MoveTable.instance(new CornerPos(cornerMask, turnMask), TurnBase);
  }

  public static TurnTable mEdgePosSet(final Set<Edge> edgeMask, final Set<Turn> turnMask) {
    return MoveTable.instance(new MEdgePosSet(edgeMask, turnMask), TurnBase);
  }

  public static TurnTable mEdgePos(final Set<Edge> edgeMask, final Set<Turn> turnMask) {
    return MoveTable.instance(new MEdgePos(edgeMask, turnMask), TurnBase);
  }

  public static TurnTable uEdgePos(final Set<Edge> edgeMask, final Set<Turn> turnMask) {
    return MoveTable.instance(new UEdgePos(edgeMask, turnMask), TurnBase);
  }

  public static TurnTable dEdgePos(final Set<Edge> edgeMask, final Set<Turn> turnMask) {
    return MoveTable.instance(new DEdgePos(edgeMask, turnMask), TurnBase);
  }

  public static TurnTable mEdgePos_B(final Set<Edge> edgeMask, final Set<Turn> turnMask) {
    assertTurnsAreB(turnMask);
    return MoveTable.instance(new MEdgePosB(edgeMask, turnMask), TurnBaseB);
  }

  public static TurnTable oEdgePos_B(final Set<Edge> edgeMask, final Set<Turn> turnMask) {
    assertTurnsAreB(turnMask);
    return MoveTable.instance(new OEdgePosB(edgeMask, turnMask), TurnBaseB);
  }

  private static void assertTurnsAreB(final Set<Turn> turnMask) {
    for (final Turn turn : turnMask)
      assert turn.isB();
  }

  public static boolean[] get_mEdgePos_inB(final Set<Edge> edgeMask, final Set<Turn> turnMask) {
    return get_edgePos_inB(new MEdgePos(edgeMask, turnMask));
  }

  public static boolean[] get_uEdgePos_inB(final Set<Edge> edgeMask, final Set<Turn> turnMask) {
    return get_edgePos_inB(new UEdgePos(edgeMask, turnMask));
  }

  public static boolean[] get_dEdgePos_inB(final Set<Edge> edgeMask, final Set<Turn> turnMask) {
    return get_edgePos_inB(new DEdgePos(edgeMask, turnMask));
  }

  private static boolean[] get_edgePos_inB(final MoveToB pack) {
    final boolean[] t = new boolean[pack.stateSize()];
    for (int i = 0; i < t.length; i++) {
      pack.unpack(i);
      t[i] = pack.isInB();
    }
    return t;
  }

  public static int[] get_mEdgePos_toB(final Set<Edge> edgeMask, final Set<Turn> turnMask) {
    return get_edgePos_toB(new MEdgePos(edgeMask, turnMask), new MEdgePosB(edgeMask, turnMask));
  }

  public static int[] get_uEdgePos_toB(final Set<Edge> edgeMask, final Set<Turn> turnMask) {
    return get_edgePos_toB(new UEdgePos(edgeMask, turnMask), new OEdgePosB(edgeMask, turnMask));
  }

  public static int[] get_dEdgePos_toB(final Set<Edge> edgeMask, final Set<Turn> turnMask) {
    return get_edgePos_toB(new DEdgePos(edgeMask, turnMask), new OEdgePosB(edgeMask, turnMask));
  }

  private static int[] get_edgePos_toB(final MoveToB pack, final Move packB) {
    final int[] t = new int[pack.stateSize()];
    Arrays.fill(t, -1);
    for (int i = 0; i < t.length; i++) {
      pack.unpack(i);
      if (pack.isInB())
        t[i] = packB.convertFrom(pack);
    }
    return t;
  }

  public static int[][] get_uEdgePos_B_dEdgePos_B_toB(final Set<Edge> edgeMask, final Set<Turn> turnMask) {
    final OEdgePosB uPackB = new OEdgePosB(PackKit.getUEdges(edgeMask), turnMask);
    final OEdgePosB dPackB = new OEdgePosB(PackKit.getDEdges(edgeMask), turnMask);
    final OEdgePosB packB = new OEdgePosB(edgeMask, turnMask);
    final int[][] t = new int[uPackB.stateSize()][dPackB.stateSize()];
    for (int i = 0; i < t.length; i++) {
      Arrays.fill(t[i], (short)-1);
      uPackB.unpack(i);
      for (int j = 0; j < t[i].length; j++) {
        dPackB.unpack(j);
        t[i][j] = packB.convertFrom(uPackB, dPackB);
      }
    }
    return t;
  }

  public static int[] get_mEdgePos_to_mEdgePosSet(final Set<Edge> edgeMask, final Set<Turn> turnMask) {
    final MEdgePos mEdgePos = new MEdgePos(edgeMask, turnMask);
    final MEdgePosSet mEdgePosSet = new MEdgePosSet(edgeMask, turnMask);
    final int[] t = new int[mEdgePos.stateSize()];
    for (int i = 0; i < t.length; i++) {
      mEdgePos.unpack(i);
      t[i] = mEdgePosSet.convertFrom(mEdgePos);
    }
    return t;
  }
}
