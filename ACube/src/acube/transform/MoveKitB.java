package acube.transform;

import acube.TurnB;
import acube.pack.PackKit;

public final class MoveKitB {
  private MoveKitB() {}
  
  public static ToTabMove midgePos(int[] edgeMask) {
    return MoveTab.obj(new MidgePos(new int[] {edgeMask[8], edgeMask[9], edgeMask[10], edgeMask[11]}));
  }

  public static ToTabMove edgePos(int[] edgeMask) {
    return MoveTab.obj(new EdgePos(new int[] {edgeMask[0], edgeMask[1], edgeMask[2], edgeMask[3], edgeMask[4], edgeMask[5], edgeMask[6], edgeMask[7]}));
  }

  private static final int[][] turnBase = {
    {TurnB.U1}, {TurnB.D1}, {TurnB.F2}, {TurnB.B2}, {TurnB.L2}, {TurnB.R2},
    {TurnB.u1, TurnB.D1}, {TurnB.d1, TurnB.U1}, {TurnB.f2, TurnB.B2}, {TurnB.b2, TurnB.F2}, {TurnB.l2, TurnB.R2}, {TurnB.r2, TurnB.L2},
    {TurnB.U2, TurnB.U1, TurnB.U1}, {TurnB.D2, TurnB.D1, TurnB.D1},
    {TurnB.u2, TurnB.D2}, {TurnB.d2, TurnB.U2},
    {TurnB.U3, TurnB.U2, TurnB.U1}, {TurnB.D3, TurnB.D2, TurnB.D1},
    {TurnB.u3, TurnB.D3}, {TurnB.d3, TurnB.U3},
    {TurnB.E1, TurnB.U1, TurnB.D3}, {TurnB.E2, TurnB.U2, TurnB.D2}, {TurnB.E3, TurnB.U3, TurnB.D1},
    {TurnB.S2, TurnB.B2, TurnB.F2},
    {TurnB.M2, TurnB.R2, TurnB.L2},
  };

  private static final class MidgePos extends MovePos {
    MidgePos(int[] mask) {
      super(PackKit.midgePosB(mask), TurnB.N, turnBase);
    }

    public void turn(int t) {
      switch (t) {
       case TurnB.U1: break;
       case TurnB.D1: break;
       case TurnB.F2: pack.swap(0, 1); break; // FR FL
       case TurnB.B2: pack.swap(2, 3); break; // BR BL
       case TurnB.L2: pack.swap(1, 3); break; // FL BL
       case TurnB.R2: pack.swap(0, 2); break; // FR BR
       default: throw new IllegalArgumentException("Unsupported or non primitive turn");
      }
    }
  }

  private static final class EdgePos extends MovePos {
    EdgePos(int[] mask) {
      super(PackKit.edgePosB(mask), TurnB.N, turnBase);
    }

    public void turn(int t) {
      switch (t) {
       case TurnB.U1: pack.cycle(1, 2, 3, 0); break; // UR UB UL UF
       case TurnB.D1: pack.cycle(7, 6, 5, 4); break; // DL DB DR DF
       case TurnB.F2: pack.swap(0, 4); break; // UF DF
       case TurnB.B2: pack.swap(2, 6); break; // UB DB
       case TurnB.L2: pack.swap(3, 7); break; // UL DL
       case TurnB.R2: pack.swap(1, 5); break; // UR DR
       default: throw new IllegalArgumentException("Unsupported or non primitive turn");
      }
    }
  }
}
