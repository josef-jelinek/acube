package acube;

public final class SymTransform  {
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
  public static final int N = 24;

  private static final int[][] turnD = {
    {Turn.F1, Turn.L1, Turn.B1, Turn.R1}, // in D sym: doing F1 affects cube as L1, L1 as B1, B1 as R1, and R1 as F1
    {Turn.F2, Turn.L2, Turn.B2, Turn.R2},
    {Turn.F3, Turn.L3, Turn.B3, Turn.R3},
    {Turn.f1, Turn.l1, Turn.b1, Turn.r1},
    {Turn.f2, Turn.l2, Turn.b2, Turn.r2},
    {Turn.f3, Turn.l3, Turn.b3, Turn.r3},
    {Turn.S1, Turn.M1, Turn.S3, Turn.M3},
    {Turn.S2, Turn.M2},
  };
  private static final int[][] turnB = {
    {Turn.U1, Turn.R1, Turn.D1, Turn.L1},
    {Turn.U2, Turn.R2, Turn.D2, Turn.L2},
    {Turn.U3, Turn.R3, Turn.D3, Turn.L3},
    {Turn.u1, Turn.r1, Turn.d1, Turn.l1},
    {Turn.u2, Turn.r2, Turn.d2, Turn.l2},
    {Turn.u3, Turn.r3, Turn.d3, Turn.l3},
    {Turn.E1, Turn.M1, Turn.E3, Turn.M3},
    {Turn.E2, Turn.M2},
  };
  private static final int[][] turnL = {
    {Turn.U1, Turn.B1, Turn.D1, Turn.F1},
    {Turn.U2, Turn.B2, Turn.D2, Turn.F2},
    {Turn.U3, Turn.B3, Turn.D3, Turn.F3},
    {Turn.u1, Turn.b1, Turn.d1, Turn.f1},
    {Turn.u2, Turn.b2, Turn.d2, Turn.f2},
    {Turn.u3, Turn.b3, Turn.d3, Turn.f3},
    {Turn.E1, Turn.S1, Turn.E3, Turn.S3},
    {Turn.E2, Turn.S2},
  };

  private static final int[][] symD = {
    {I, D, D2, U},
    {F, DR, BL2, LF},
    {B, DL, BD2, BU},
    {B2, DL2, L2, DB2},
    {R, DB, LB2, UF},
    {L, LD, LD2, BL},
  };
  private static final int[][] symF = {
    {I, F, B2, B},
    {R, DR, LD2, BU},
    {L, LF, LB2, DL},
    {L2, BL2, D2, BD2},
    {U, UF, DL2, BL},
    {D, LD, DB2, DB},
  };
  private static final int[][] symL = {
    {I, L, L2, R},
    {U, LF, DB2, BU},
    {D, DL, DL2, DR},
    {D2, LB2, B2, LD2},
    {F, LD, BD2, UF},
    {B, BL, BL2, DB},
  };

  private final int[][] output = new int[N][Turn.N];
  private final int[][] transition = new int[N][Turn.N];

  public int turn(int turn, int symmetry) {
    return output[symmetry][turn];
  }

  public int sym(int turn, int symmetry) {
    return transition[symmetry][turn];
  }

  public SymTransform() {
    fillOutputTab(output);
    fillTransitionTab(transition);
  }

  private static void fillOutputTab(int[][] tab) {
    int[] tD = new int[Turn.N];
    int[] tB = new int[Turn.N];
    int[] tL = new int[Turn.N];
    for (int t = 0; t < Turn.N; t++)
      tD[t] = tB[t] = tL[t] = t;
    for (int t = 0; t < turnD.length; t++)
      for (int i = 0; i < turnD[t].length; i++)
        tD[turnD[t][i]] = turnD[t][(i + 1) % turnD[t].length];
    for (int t = 0; t < turnB.length; t++)
      for (int i = 0; i < turnB[t].length; i++)
        tB[turnB[t][i]] = turnB[t][(i + 1) % turnB[t].length];
    for (int t = 0; t < turnL.length; t++)
      for (int i = 0; i < turnL[t].length; i++)
        tL[turnL[t][i]] = turnL[t][(i + 1) % turnL[t].length];
    for (int t = 0; t < Turn.N; t++) {
      tab[I][t] = t;
      tab[D][t] = tD[t];
      tab[B][t] = tB[t];
      tab[L][t] = tL[t];
      tab[D2][t] = tD[tD[t]];
      tab[B2][t] = tB[tB[t]];
      tab[L2][t] = tL[tL[t]];
      tab[U][t] = tD[tD[tD[t]]];
      tab[F][t] = tB[tB[tB[t]]];
      tab[R][t] = tL[tL[tL[t]]];
      tab[DB][t] = tB[tD[t]];
      tab[DL][t] = tL[tD[t]];
      tab[BL][t] = tL[tB[t]];
      tab[LD][t] = tD[tL[t]];
      tab[DB2][t] = tB[tB[tD[t]]];
      tab[DL2][t] = tL[tL[tD[t]]];
      tab[BD2][t] = tD[tD[tB[t]]];
      tab[BL2][t] = tL[tL[tB[t]]];
      tab[LD2][t] = tD[tD[tL[t]]];
      tab[LB2][t] = tB[tB[tL[t]]];
      tab[DR][t] = tL[tL[tL[tD[t]]]];
      tab[BU][t] = tD[tD[tD[tB[t]]]];
      tab[LF][t] = tB[tB[tB[tL[t]]]];
      tab[UF][t] = tB[tB[tB[tD[tD[tD[t]]]]]];
    }
  }

  private static void fillTransitionTab(int[][] tab) {
    for (int s = 0; s < N; s++)
      for (int t = 0; t < Turn.N; t++)
        tab[s][t] = s;
    for (int i = 0; i < symD.length; i++)
      for (int j = 0; j < symD[i].length; j++)
        tab[symD[i][j]][Turn.E1] = symD[i][(j + 1) % symD[i].length];
    for (int i = 0; i < symF.length; i++)
      for (int j = 0; j < symF[i].length; j++)
        tab[symF[i][j]][Turn.S1] = symF[i][(j + 1) % symF[i].length];
    for (int i = 0; i < symL.length; i++)
      for (int j = 0; j < symL[i].length; j++)
        tab[symL[i][j]][Turn.M1] = symL[i][(j + 1) % symL[i].length];
    for (int s = 0; s < N; s++) {
      tab[s][Turn.E2] = tab[tab[s][Turn.E1]][Turn.E1];
      tab[s][Turn.S2] = tab[tab[s][Turn.S1]][Turn.S1];
      tab[s][Turn.M2] = tab[tab[s][Turn.M1]][Turn.M1];
    }
    for (int s = 0; s < N; s++) {
      tab[s][Turn.E3] = tab[tab[s][Turn.E2]][Turn.E1];
      tab[s][Turn.S3] = tab[tab[s][Turn.S2]][Turn.S1];
      tab[s][Turn.M3] = tab[tab[s][Turn.M2]][Turn.M1];
    }
    for (int s = 0; s < N; s++) {
      tab[s][Turn.d1] = tab[s][Turn.u3] = tab[s][Turn.E1];
      tab[s][Turn.f1] = tab[s][Turn.b3] = tab[s][Turn.S1];
      tab[s][Turn.l1] = tab[s][Turn.r3] = tab[s][Turn.M1];
      tab[s][Turn.d2] = tab[s][Turn.u2] = tab[s][Turn.E2];
      tab[s][Turn.f2] = tab[s][Turn.b2] = tab[s][Turn.S2];
      tab[s][Turn.l2] = tab[s][Turn.r2] = tab[s][Turn.M2];
      tab[s][Turn.d3] = tab[s][Turn.u1] = tab[s][Turn.E3];
      tab[s][Turn.f3] = tab[s][Turn.b1] = tab[s][Turn.S3];
      tab[s][Turn.l3] = tab[s][Turn.r1] = tab[s][Turn.M3];
    }
  }
}
