package acube;

public final class SymTransform {
  public static final int I = 0;
  public static final int D = 1;
  public static final int B = 2;
  public static final int L = 3;
  public static final int D2 = 4;
  public static final int B2 = 5;
  public static final int L2 = 6;
  public static final int U = 7;
  public static final int F = 8;
  public static final int R = 9;
  public static final int DB = 10;
  public static final int DL = 11;
  public static final int BL = 12;
  public static final int LD = 13;
  public static final int DB2 = 14;
  public static final int DL2 = 15;
  public static final int BD2 = 16;
  public static final int BL2 = 17;
  public static final int LD2 = 18;
  public static final int LB2 = 19;
  public static final int DR = 20;
  public static final int BU = 21;
  public static final int LF = 22;
  public static final int UF = 23;
  public static final int SymmetryCount = 24;
  private static final Turn[][] turnD = {
      { Turn.F1, Turn.L1, Turn.B1, Turn.R1 }, // in D sym: doing F1 affects cube as L1, L1 as B1, B1 as R1, and R1 as F1
      { Turn.F2, Turn.L2, Turn.B2, Turn.R2 }, { Turn.F3, Turn.L3, Turn.B3, Turn.R3 },
      { Turn.f1, Turn.l1, Turn.b1, Turn.r1 }, { Turn.f2, Turn.l2, Turn.b2, Turn.r2 },
      { Turn.f3, Turn.l3, Turn.b3, Turn.r3 }, { Turn.S1, Turn.M1, Turn.S3, Turn.M3 }, { Turn.S2, Turn.M2 }, };
  private static final Turn[][] turnB = {
      { Turn.U1, Turn.R1, Turn.D1, Turn.L1 }, { Turn.U2, Turn.R2, Turn.D2, Turn.L2 },
      { Turn.U3, Turn.R3, Turn.D3, Turn.L3 }, { Turn.u1, Turn.r1, Turn.d1, Turn.l1 },
      { Turn.u2, Turn.r2, Turn.d2, Turn.l2 }, { Turn.u3, Turn.r3, Turn.d3, Turn.l3 },
      { Turn.E1, Turn.M1, Turn.E3, Turn.M3 }, { Turn.E2, Turn.M2 }, };
  private static final Turn[][] turnL = {
      { Turn.U1, Turn.B1, Turn.D1, Turn.F1 }, { Turn.U2, Turn.B2, Turn.D2, Turn.F2 },
      { Turn.U3, Turn.B3, Turn.D3, Turn.F3 }, { Turn.u1, Turn.b1, Turn.d1, Turn.f1 },
      { Turn.u2, Turn.b2, Turn.d2, Turn.f2 }, { Turn.u3, Turn.b3, Turn.d3, Turn.f3 },
      { Turn.E1, Turn.S1, Turn.E3, Turn.S3 }, { Turn.E2, Turn.S2 }, };
  private static final int[][] symD = { { I, D, D2, U }, // after D, I becomes D, D -> D2, D2 -> U, and U -> I
      { F, DR, BL2, LF }, { B, DL, BD2, BU }, { B2, DL2, L2, DB2 }, { R, DB, LB2, UF }, { L, LD, LD2, BL }, };
  private static final int[][] symF = {
      { I, F, B2, B }, { R, DR, LD2, BU }, { L, LF, LB2, DL }, { L2, BL2, D2, BD2 }, { U, UF, DL2, BL },
      { D, LD, DB2, DB }, };
  private static final int[][] symL = {
      { I, L, L2, R }, { U, LF, DB2, BU }, { D, DL, DL2, DR }, { D2, LB2, B2, LD2 }, { F, LD, BD2, UF },
      { B, BL, BL2, DB }, };
  private static final Turn[][] output = new Turn[SymmetryCount][Turn.size()];
  private static final int[][] transition = new int[SymmetryCount][Turn.size()];
  static {
    fillOutputTab(output);
    fillTransitionTab(transition);
  }

  public static Turn getTurn(final Turn turn, final int symmetry) {
    return output[symmetry][turn.ordinal()];
  }

  public static int getSymmetry(final Turn turn, final int symmetry) {
    return transition[symmetry][turn.ordinal()];
  }

  private static void fillOutputTab(final Turn[][] tab) {
    final Turn[] tD = new Turn[Turn.size()];
    final Turn[] tB = new Turn[Turn.size()];
    final Turn[] tL = new Turn[Turn.size()];
    for (final Turn turn : Turn.values())
      tD[turn.ordinal()] = tB[turn.ordinal()] = tL[turn.ordinal()] = turn;
    for (int t = 0; t < turnD.length; t++)
      for (int i = 0; i < turnD[t].length; i++)
        tD[turnD[t][i].ordinal()] = turnD[t][(i + 1) % turnD[t].length];
    for (int t = 0; t < turnB.length; t++)
      for (int i = 0; i < turnB[t].length; i++)
        tB[turnB[t][i].ordinal()] = turnB[t][(i + 1) % turnB[t].length];
    for (int t = 0; t < turnL.length; t++)
      for (int i = 0; i < turnL[t].length; i++)
        tL[turnL[t][i].ordinal()] = turnL[t][(i + 1) % turnL[t].length];
    for (final Turn turn : Turn.values()) {
      tab[I][turn.ordinal()] = turn;
      tab[D][turn.ordinal()] = tD[turn.ordinal()];
      tab[B][turn.ordinal()] = tB[turn.ordinal()];
      tab[L][turn.ordinal()] = tL[turn.ordinal()];
      tab[D2][turn.ordinal()] = tD[tD[turn.ordinal()].ordinal()];
      tab[B2][turn.ordinal()] = tB[tB[turn.ordinal()].ordinal()];
      tab[L2][turn.ordinal()] = tL[tL[turn.ordinal()].ordinal()];
      tab[U][turn.ordinal()] = tD[tD[tD[turn.ordinal()].ordinal()].ordinal()];
      tab[F][turn.ordinal()] = tB[tB[tB[turn.ordinal()].ordinal()].ordinal()];
      tab[R][turn.ordinal()] = tL[tL[tL[turn.ordinal()].ordinal()].ordinal()];
      tab[DB][turn.ordinal()] = tB[tD[turn.ordinal()].ordinal()];
      tab[DL][turn.ordinal()] = tL[tD[turn.ordinal()].ordinal()];
      tab[BL][turn.ordinal()] = tL[tB[turn.ordinal()].ordinal()];
      tab[LD][turn.ordinal()] = tD[tL[turn.ordinal()].ordinal()];
      tab[DB2][turn.ordinal()] = tB[tB[tD[turn.ordinal()].ordinal()].ordinal()];
      tab[DL2][turn.ordinal()] = tL[tL[tD[turn.ordinal()].ordinal()].ordinal()];
      tab[BD2][turn.ordinal()] = tD[tD[tB[turn.ordinal()].ordinal()].ordinal()];
      tab[BL2][turn.ordinal()] = tL[tL[tB[turn.ordinal()].ordinal()].ordinal()];
      tab[LD2][turn.ordinal()] = tD[tD[tL[turn.ordinal()].ordinal()].ordinal()];
      tab[LB2][turn.ordinal()] = tB[tB[tL[turn.ordinal()].ordinal()].ordinal()];
      tab[DR][turn.ordinal()] = tL[tL[tL[tD[turn.ordinal()].ordinal()].ordinal()].ordinal()];
      tab[BU][turn.ordinal()] = tD[tD[tD[tB[turn.ordinal()].ordinal()].ordinal()].ordinal()];
      tab[LF][turn.ordinal()] = tB[tB[tB[tL[turn.ordinal()].ordinal()].ordinal()].ordinal()];
      tab[UF][turn.ordinal()] =
          tB[tB[tB[tD[tD[tD[turn.ordinal()].ordinal()].ordinal()].ordinal()].ordinal()].ordinal()];
    }
  }

  private static void fillTransitionTab(final int[][] tab) {
    for (int symmetry = 0; symmetry < SymmetryCount; symmetry++)
      for (final Turn turn : Turn.values())
        tab[symmetry][turn.ordinal()] = symmetry;
    for (int i = 0; i < symD.length; i++)
      for (int j = 0; j < symD[i].length; j++)
        tab[symD[i][j]][Turn.E1.ordinal()] = symD[i][(j + 1) % symD[i].length];
    for (int i = 0; i < symF.length; i++)
      for (int j = 0; j < symF[i].length; j++)
        tab[symF[i][j]][Turn.S1.ordinal()] = symF[i][(j + 1) % symF[i].length];
    for (int i = 0; i < symL.length; i++)
      for (int j = 0; j < symL[i].length; j++)
        tab[symL[i][j]][Turn.M1.ordinal()] = symL[i][(j + 1) % symL[i].length];
    for (int s = 0; s < SymmetryCount; s++) {
      tab[s][Turn.E2.ordinal()] = tab[tab[s][Turn.E1.ordinal()]][Turn.E1.ordinal()];
      tab[s][Turn.S2.ordinal()] = tab[tab[s][Turn.S1.ordinal()]][Turn.S1.ordinal()];
      tab[s][Turn.M2.ordinal()] = tab[tab[s][Turn.M1.ordinal()]][Turn.M1.ordinal()];
    }
    for (int s = 0; s < SymmetryCount; s++) {
      tab[s][Turn.E3.ordinal()] = tab[tab[s][Turn.E2.ordinal()]][Turn.E1.ordinal()];
      tab[s][Turn.S3.ordinal()] = tab[tab[s][Turn.S2.ordinal()]][Turn.S1.ordinal()];
      tab[s][Turn.M3.ordinal()] = tab[tab[s][Turn.M2.ordinal()]][Turn.M1.ordinal()];
    }
    for (int s = 0; s < SymmetryCount; s++) {
      tab[s][Turn.d1.ordinal()] = tab[s][Turn.u3.ordinal()] = tab[s][Turn.E1.ordinal()];
      tab[s][Turn.f1.ordinal()] = tab[s][Turn.b3.ordinal()] = tab[s][Turn.S1.ordinal()];
      tab[s][Turn.l1.ordinal()] = tab[s][Turn.r3.ordinal()] = tab[s][Turn.M1.ordinal()];
      tab[s][Turn.d2.ordinal()] = tab[s][Turn.u2.ordinal()] = tab[s][Turn.E2.ordinal()];
      tab[s][Turn.f2.ordinal()] = tab[s][Turn.b2.ordinal()] = tab[s][Turn.S2.ordinal()];
      tab[s][Turn.l2.ordinal()] = tab[s][Turn.r2.ordinal()] = tab[s][Turn.M2.ordinal()];
      tab[s][Turn.d3.ordinal()] = tab[s][Turn.u1.ordinal()] = tab[s][Turn.E3.ordinal()];
      tab[s][Turn.f3.ordinal()] = tab[s][Turn.b1.ordinal()] = tab[s][Turn.S3.ordinal()];
      tab[s][Turn.l3.ordinal()] = tab[s][Turn.r1.ordinal()] = tab[s][Turn.M3.ordinal()];
    }
  }

  private SymTransform() {}
}
