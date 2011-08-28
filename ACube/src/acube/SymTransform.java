package acube;

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

public final class SymTransform {
  public static final int I = 0;
  public static final int D = 1;
  public static final int B = 2;
  public static final int L = 3;
  public static final int DD = 4;
  public static final int BB = 5;
  public static final int LL = 6;
  public static final int U = 7;
  public static final int F = 8;
  public static final int R = 9;
  public static final int DB = 10;
  public static final int DL = 11;
  public static final int BL = 12;
  public static final int LD = 13;
  public static final int DBB = 14;
  public static final int DLL = 15;
  public static final int BDD = 16;
  public static final int BLL = 17;
  public static final int LDD = 18;
  public static final int LBB = 19;
  public static final int DR = 20;
  public static final int BU = 21;
  public static final int LF = 22;
  public static final int UF = 23;
  public static final int SymmetryCount = 24;
  private static final Turn[][] turnD = {
      { F1, L1, B1, R1 }, // in D sym: doing F1 affects cube as L1, L1 as B1, B1 as R1, and R1 as F1
      { F2, L2, B2, R2 }, { F3, L3, B3, R3 }, { f1, l1, b1, r1 }, { f2, l2, b2, r2 }, { f3, l3, b3, r3 },
      { S1, M1, S3, M3 }, { S2, M2 }, };
  private static final Turn[][] turnB = {
      { U1, R1, D1, L1 }, { U2, R2, D2, L2 }, { U3, R3, D3, L3 }, { u1, r1, d1, l1 }, { u2, r2, d2, l2 },
      { u3, r3, d3, l3 }, { E1, M1, E3, M3 }, { E2, M2 }, };
  private static final Turn[][] turnL = {
      { U1, B1, D1, F1 }, { U2, B2, D2, F2 }, { U3, B3, D3, F3 }, { u1, b1, d1, f1 }, { u2, b2, d2, f2 },
      { u3, b3, d3, f3 }, { E1, S1, E3, S3 }, { E2, S2 }, };
  private static final int[][] symD = { { I, D, DD, U }, // after D, I becomes D, D -> D2, D2 -> U, and U -> I
      { F, DR, BLL, LF }, { B, DL, BDD, BU }, { BB, DLL, LL, DBB }, { R, DB, LBB, UF }, { L, LD, LDD, BL }, };
  private static final int[][] symF = {
      { I, F, BB, B }, { R, DR, LDD, BU }, { L, LF, LBB, DL }, { LL, BLL, DD, BDD }, { U, UF, DLL, BL },
      { D, LD, DBB, DB }, };
  private static final int[][] symL = {
      { I, L, LL, R }, { U, LF, DBB, BU }, { D, DL, DLL, DR }, { DD, LBB, BB, LDD }, { F, LD, BDD, UF },
      { B, BL, BLL, DB }, };
  private static final Turn[][] output = new Turn[SymmetryCount][Turn.size];
  private static final int[][] transition = new int[SymmetryCount][Turn.size];
  static {
    fillOutputTab(output);
    fillTransitionTab(transition);
  }

  public static Turn getTurn(final Turn turn, final int symmetry) {
    return output[symmetry][turn.ordinal()];
  }

  public static int getSymmetry(final int symmetry, final Turn turn) {
    return transition[symmetry][turn.ordinal()];
  }

  private static void fillOutputTab(final Turn[][] tab) {
    final Turn[] tD = new Turn[Turn.size];
    final Turn[] tB = new Turn[Turn.size];
    final Turn[] tL = new Turn[Turn.size];
    for (final Turn turn : Turn.values)
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
    for (final Turn turn : Turn.values) {
      final int ti = turn.ordinal();
      final int tDi = tD[ti].ordinal();
      final int tBi = tB[ti].ordinal();
      final int tLi = tL[ti].ordinal();
      tab[I][ti] = turn;
      tab[D][ti] = tD[ti];
      tab[B][ti] = tB[ti];
      tab[L][ti] = tL[ti];
      tab[DD][ti] = tD[tDi];
      tab[BB][ti] = tB[tBi];
      tab[LL][ti] = tL[tLi];
      tab[U][ti] = tD[tD[tDi].ordinal()];
      tab[F][ti] = tB[tB[tBi].ordinal()];
      tab[R][ti] = tL[tL[tLi].ordinal()];
      tab[DB][ti] = tD[tBi];
      tab[DL][ti] = tD[tLi];
      tab[BL][ti] = tB[tLi];
      tab[LD][ti] = tL[tDi];
      tab[DBB][ti] = tD[tB[tBi].ordinal()];
      tab[DLL][ti] = tD[tL[tLi].ordinal()];
      tab[BDD][ti] = tB[tD[tDi].ordinal()];
      tab[BLL][ti] = tB[tL[tLi].ordinal()];
      tab[LDD][ti] = tL[tD[tDi].ordinal()];
      tab[LBB][ti] = tL[tB[tBi].ordinal()];
      tab[DR][ti] = tD[tL[tL[tLi].ordinal()].ordinal()];
      tab[BU][ti] = tB[tD[tD[tDi].ordinal()].ordinal()];
      tab[LF][ti] = tL[tB[tB[tBi].ordinal()].ordinal()];
      tab[UF][ti] = tD[tD[tD[tB[tB[tBi].ordinal()].ordinal()].ordinal()].ordinal()];
    }
  }

  private static void fillTransitionTab(final int[][] tab) {
    for (int symmetry = 0; symmetry < SymmetryCount; symmetry++)
      for (final Turn turn : Turn.values)
        tab[symmetry][turn.ordinal()] = symmetry;
    for (int i = 0; i < symD.length; i++)
      for (int j = 0; j < symD[i].length; j++)
        tab[symD[i][j]][E1.ordinal()] = symD[i][(j + 1) % symD[i].length];
    for (int i = 0; i < symF.length; i++)
      for (int j = 0; j < symF[i].length; j++)
        tab[symF[i][j]][S1.ordinal()] = symF[i][(j + 1) % symF[i].length];
    for (int i = 0; i < symL.length; i++)
      for (int j = 0; j < symL[i].length; j++)
        tab[symL[i][j]][M1.ordinal()] = symL[i][(j + 1) % symL[i].length];
    for (int s = 0; s < SymmetryCount; s++) {
      tab[s][E2.ordinal()] = tab[tab[s][E1.ordinal()]][E1.ordinal()];
      tab[s][S2.ordinal()] = tab[tab[s][S1.ordinal()]][S1.ordinal()];
      tab[s][M2.ordinal()] = tab[tab[s][M1.ordinal()]][M1.ordinal()];
    }
    for (int s = 0; s < SymmetryCount; s++) {
      tab[s][E3.ordinal()] = tab[tab[s][E2.ordinal()]][E1.ordinal()];
      tab[s][S3.ordinal()] = tab[tab[s][S2.ordinal()]][S1.ordinal()];
      tab[s][M3.ordinal()] = tab[tab[s][M2.ordinal()]][M1.ordinal()];
    }
    for (int s = 0; s < SymmetryCount; s++) {
      tab[s][d1.ordinal()] = tab[s][u3.ordinal()] = tab[s][E1.ordinal()];
      tab[s][f1.ordinal()] = tab[s][b3.ordinal()] = tab[s][S1.ordinal()];
      tab[s][l1.ordinal()] = tab[s][r3.ordinal()] = tab[s][M1.ordinal()];
      tab[s][d2.ordinal()] = tab[s][u2.ordinal()] = tab[s][E2.ordinal()];
      tab[s][f2.ordinal()] = tab[s][b2.ordinal()] = tab[s][S2.ordinal()];
      tab[s][l2.ordinal()] = tab[s][r2.ordinal()] = tab[s][M2.ordinal()];
      tab[s][d3.ordinal()] = tab[s][u1.ordinal()] = tab[s][E3.ordinal()];
      tab[s][f3.ordinal()] = tab[s][b1.ordinal()] = tab[s][S3.ordinal()];
      tab[s][l3.ordinal()] = tab[s][r1.ordinal()] = tab[s][M3.ordinal()];
    }
  }
}
