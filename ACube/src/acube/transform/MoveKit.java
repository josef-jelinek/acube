package acube.transform;

import acube.Turn;
import acube.pack.PackKit;
import acube.pack.Pack;

public final class MoveKit {
  private MoveKit() {}

  public static ToTabMove cornTwist(int[] cornMask, int[] cornOriMask) {
    return MoveTab.obj(new CornTwist(cornMask, cornOriMask));
  }

  public static ToTabMove edgeFlip(int[] edgeMask, int[] edgeOriMask) {
    return MoveTab.obj(new EdgeFlip(edgeMask, edgeOriMask));
  }

  public static ToTabMove cornPos(int[] cornMask) {
    return MoveTab.obj(new CornPos(cornMask));
  }

  public static ToTabMove midgeLoc(int[] edgeMask) {
    return MoveTab.obj(new MidgeLoc(edgeMask));
  }

  public static ToTabMove midgePos(int[] edgeMask) {
    return MoveTab.obj(new MidgePos(edgeMask));
  }

  public static ToTabMove udgePos(int[] edgeMask) {
    return MoveTab.obj(new UdgePos(edgeMask));
  }

  public static ToTabMove dedgePos(int[] edgeMask) {
    return MoveTab.obj(new DedgePos(edgeMask));
  }

  private static final int[][] turnBase = {
    {Turn.U1}, {Turn.D1}, {Turn.F1}, {Turn.B1}, {Turn.L1}, {Turn.R1},
    {Turn.u1, Turn.D1}, {Turn.d1, Turn.U1}, {Turn.f1, Turn.B1}, {Turn.b1, Turn.F1}, {Turn.l1, Turn.R1}, {Turn.r1, Turn.L1},
    {Turn.U2, Turn.U1, Turn.U1}, {Turn.D2, Turn.D1, Turn.D1},
    {Turn.F2, Turn.F1, Turn.F1}, {Turn.B2, Turn.B1, Turn.B1},
    {Turn.L2, Turn.L1, Turn.L1}, {Turn.R2, Turn.R1, Turn.R1},
    {Turn.u2, Turn.D2}, {Turn.d2, Turn.U2}, {Turn.f2, Turn.B2}, {Turn.b2, Turn.F2}, {Turn.l2, Turn.R2}, {Turn.r2, Turn.L2},
    {Turn.U3, Turn.U2, Turn.U1}, {Turn.D3, Turn.D2, Turn.D1},
    {Turn.F3, Turn.F2, Turn.F1}, {Turn.B3, Turn.B2, Turn.B1},
    {Turn.L3, Turn.L2, Turn.L1}, {Turn.R3, Turn.R2, Turn.R1},
    {Turn.u3, Turn.D3}, {Turn.d3, Turn.U3}, {Turn.f3, Turn.B3}, {Turn.b3, Turn.F3}, {Turn.l3, Turn.R3}, {Turn.r3, Turn.L3},
    {Turn.E1, Turn.U1, Turn.D3}, {Turn.E2, Turn.U2, Turn.D2}, {Turn.E3, Turn.U3, Turn.D1},
    {Turn.S1, Turn.B1, Turn.F3}, {Turn.S2, Turn.B2, Turn.F2}, {Turn.S3, Turn.B3, Turn.F1},
    {Turn.M1, Turn.R1, Turn.L3}, {Turn.M2, Turn.R2, Turn.L2}, {Turn.M3, Turn.R3, Turn.L1},
  };

  private static void cycleEdges(Pack pack, int turn) {
    switch (turn) {
     case Turn.U1: pack.cycle(0, 1, 2, 3); break; // UF UR UB UL
     case Turn.D1: pack.cycle(4, 7, 6, 5); break; // DF DL DB DR
     case Turn.F1: pack.cycle(9, 4, 8, 0); break; // FL DF FR UF
     case Turn.B1: pack.cycle(10, 6, 11, 2); break; // BR DB BL UB
     case Turn.L1: pack.cycle(11, 7, 9, 3); break; // BL DL FL UL
     case Turn.R1: pack.cycle(8, 5, 10, 1); break; // FR DR BR UR
     default: throw new IllegalArgumentException("Unsupported or non primitive turn");
    }
  }

  private static void cycleCorns(Pack pack, int turn) {
    switch (turn) {
     case Turn.U1: pack.cycle(1, 2, 3, 0); break; // URB UBL ULF UFR
     case Turn.D1: pack.cycle(5, 6, 7, 4); break; // DFL DLB DBR DRF
     case Turn.F1: pack.cycle(5, 4, 0, 3); break; // DFL DRF UFR ULF
     case Turn.B1: pack.cycle(7, 6, 2, 1); break; // DBR DLB UBL URB
     case Turn.L1: pack.cycle(6, 5, 3, 2); break; // DLB DFL ULF UBL
     case Turn.R1: pack.cycle(4, 7, 1, 0); break; // DRF DBR URB UFR
     default: throw new IllegalArgumentException("Unsupported or non primitive turn");
    }
  }

  private static final class CornTwist extends MoveTwist {
    CornTwist(int[] mask, int[] oriMask) {
      super(PackKit.cornTwist(mask, oriMask), Turn.N, turnBase);
    }

    public void turn(int t) {
      cycleCorns(pack, t);
      switch (t) {
       case Turn.F1: pack.twist(5, 4, 0, 3); break; // DFL DRF UFR ULF
       case Turn.B1: pack.twist(7, 6, 2, 1); break; // DBR DLB UBL URB
       case Turn.L1: pack.twist(6, 5, 3, 2); break; // DLB DFL ULF UBL
       case Turn.R1: pack.twist(4, 7, 1, 0); break; // DRF DBR URB UFR
      }
    }
  }

  private static final class EdgeFlip extends MoveTwist {
    EdgeFlip(int[] mask, int[] oriMask) {
      super(PackKit.edgeFlip(mask, oriMask), Turn.N, turnBase);
    }

    public void turn(int t) {
      cycleEdges(pack, t);
      switch (t) {
       case Turn.F1: pack.twist(9, 4, 8, 0); break; // FL DF FR UF
       case Turn.B1: pack.twist(10, 6, 11, 2); break; // BR DB BL UB
      }
    }
  }

  private static final class CornPos extends MovePos {
    CornPos(int[] mask) {
      super(PackKit.cornPos(mask), Turn.N, turnBase);
    }

    public void turn(int t) {
      cycleCorns(pack, t);
    }
  }

  private static final class MidgeLoc extends MovePos {
    MidgeLoc(int[] mask) {
      super(PackKit.midgeLoc(mask), Turn.N, turnBase);
    }

    public void turn(int t) {
      cycleEdges(pack, t);
    }
  }

  private static final class MidgePos extends MovePos {
    MidgePos(int[] mask) {
      super(PackKit.midgePos(mask), Turn.N, turnBase);
    }

    public void turn(int t) {
      cycleEdges(pack, t);
    }
  }

  private static final class UdgePos extends MovePos {
    UdgePos(int[] mask) {
      super(PackKit.udgePos(mask), Turn.N, turnBase);
    }

    public void turn(int t) {
      cycleEdges(pack, t);
    }
  }

  private static final class DedgePos extends MovePos {
    DedgePos(int[] mask) {
      super(PackKit.dedgePos(mask), Turn.N, turnBase);
    }

    public void turn(int t) {
      cycleEdges(pack, t);
    }
  }
}
