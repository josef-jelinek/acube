package acube.transform;

import acube.Corner;
import acube.Edge;
import acube.Turn;

public final class MoveKit {

  public static ITableMove cornerTwist(Corner[] mask, Corner[] twistMask, Turn[] turns) {
    return MoveTable.instance(new CornerTwist(mask, twistMask, turns), TurnBase);
  }

  public static ITableMove edgeFlip(Edge[] mask, Edge[] flipMask, Turn[] turns) {
    return MoveTable.instance(new EdgeFlip(mask, flipMask, turns), TurnBase);
  }

  public static ITableMove cornerPosition(Corner[] mask, Turn[] turns) {
    return MoveTable.instance(new CornerPosition(mask, turns), TurnBase);
  }

  public static ITableMove mEdgePositionSet(Edge[] mask, Turn[] turns) {
    return MoveTable.instance(new MEdgePositionSet(mask, turns), TurnBase);
  }

  public static ITableMove mEdgePosition(Edge[] mask, Turn[] turns) {
    return MoveTable.instance(new MEdgePosition(mask, turns), TurnBase);
  }

  public static ITableMove uEdgePosition(Edge[] mask, Turn[] turns) {
    return MoveTable.instance(new UEdgePosition(mask, turns), TurnBase);
  }

  public static ITableMove dEdgePosition(Edge[] mask, Turn[] turns) {
    return MoveTable.instance(new DEdgePosition(mask, turns), TurnBase);
  }

  private static final Turn[][][] TurnBase = {
    {{Turn.U1}, {Turn.U1}},
    {{Turn.D1}, {Turn.D1}},
    {{Turn.F1}, {Turn.F1}},
    {{Turn.B1}, {Turn.B1}},
    {{Turn.L1}, {Turn.L1}},
    {{Turn.R1}, {Turn.R1}},
    // derived
    {{Turn.u1}, {Turn.D1}},
    {{Turn.d1}, {Turn.U1}},
    {{Turn.f1}, {Turn.B1}},
    {{Turn.b1}, {Turn.F1}},
    {{Turn.l1}, {Turn.R1}},
    {{Turn.r1}, {Turn.L1}},
    {{Turn.U2}, {Turn.U1, Turn.U1}},
    {{Turn.D2}, {Turn.D1, Turn.D1}},
    {{Turn.F2}, {Turn.F1, Turn.F1}},
    {{Turn.B2}, {Turn.B1, Turn.B1}},
    {{Turn.L2}, {Turn.L1, Turn.L1}},
    {{Turn.R2}, {Turn.R1, Turn.R1}},
    {{Turn.u2}, {Turn.D1, Turn.D1}},
    {{Turn.d2}, {Turn.U1, Turn.U1}},
    {{Turn.f2}, {Turn.B1, Turn.B1}},
    {{Turn.b2}, {Turn.F1, Turn.F1}},
    {{Turn.l2}, {Turn.R1, Turn.R1}},
    {{Turn.r2}, {Turn.L1, Turn.L1}},
    {{Turn.U3}, {Turn.U1, Turn.U1, Turn.U1}},
    {{Turn.D3}, {Turn.D1, Turn.D1, Turn.D1}},
    {{Turn.F3}, {Turn.F1, Turn.F1, Turn.F1}},
    {{Turn.B3}, {Turn.B1, Turn.B1, Turn.B1}},
    {{Turn.L3}, {Turn.L1, Turn.L1, Turn.L1}},
    {{Turn.R3}, {Turn.R1, Turn.R1, Turn.R1}},
    {{Turn.u3}, {Turn.D1, Turn.D1, Turn.D1}},
    {{Turn.d3}, {Turn.U1, Turn.U1, Turn.U1}},
    {{Turn.f3}, {Turn.B1, Turn.B1, Turn.B1}},
    {{Turn.b3}, {Turn.F1, Turn.F1, Turn.F1}},
    {{Turn.l3}, {Turn.R1, Turn.R1, Turn.R1}},
    {{Turn.r3}, {Turn.L1, Turn.L1, Turn.L1}},
    {{Turn.E1}, {Turn.U1, Turn.D1, Turn.D1, Turn.D1}},
    {{Turn.E2}, {Turn.U1, Turn.U1, Turn.D1, Turn.D1}},
    {{Turn.E3}, {Turn.U1, Turn.U1, Turn.U1, Turn.D1}},
    {{Turn.S1}, {Turn.B1, Turn.F1, Turn.F1, Turn.F1}},
    {{Turn.S2}, {Turn.B1, Turn.B1, Turn.F1, Turn.F1}},
    {{Turn.S3}, {Turn.B1, Turn.B1, Turn.B1, Turn.F1}},
    {{Turn.M1}, {Turn.R1, Turn.L1, Turn.L1, Turn.L1}},
    {{Turn.M2}, {Turn.R1, Turn.R1, Turn.L1, Turn.L1}},
    {{Turn.M3}, {Turn.R1, Turn.R1, Turn.R1, Turn.L1}},
  };

  private MoveKit() { }
}
