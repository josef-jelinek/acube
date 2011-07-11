package acube;

public final class TurnB {
  public static final int U1 = 0;
  public static final int U2 = 1;
  public static final int U3 = 2;
  public static final int D1 = 3;
  public static final int D2 = 4;
  public static final int D3 = 5;
  public static final int F2 = 6;
  public static final int B2 = 7;
  public static final int L2 = 8;
  public static final int R2 = 9;
  public static final int E1 = 10;
  public static final int E2 = 11;
  public static final int E3 = 12;
  public static final int S2 = 13;
  public static final int M2 = 14;
  public static final int u1 = 15;
  public static final int u2 = 16;
  public static final int u3 = 17;
  public static final int d1 = 18;
  public static final int d2 = 19;
  public static final int d3 = 20;
  public static final int f2 = 21;
  public static final int b2 = 22;
  public static final int l2 = 23;
  public static final int r2 = 24;
  public static final int N = 25;

  public static int toA(int turn) {
    return b2a[turn];
  }

  public static int fromA(int turn) {
    return a2b[turn];
  }

  private static final int[][] ab = {
    {Turn.U1, U1}, {Turn.U2, U2}, {Turn.U3, U3},
    {Turn.D1, D1}, {Turn.D2, D2}, {Turn.D3, D3},
    {Turn.u1, u1}, {Turn.u2, u2}, {Turn.u3, u3},
    {Turn.d1, d1}, {Turn.d2, d2}, {Turn.d3, d3},
    {Turn.E1, E1}, {Turn.E2, E2}, {Turn.E3, E3},
    {Turn.F2, F2}, {Turn.B2, B2},
    {Turn.f2, f2}, {Turn.b2, b2},
    {Turn.L2, L2}, {Turn.R2, R2},
    {Turn.l2, l2}, {Turn.r2, r2},
    {Turn.S2, S2}, {Turn.M2, M2},
  };

  private static final int[] b2a = new int[N];
  private static final int[] a2b = new int[Turn.N];

  static {
    for (int i = 0; i < b2a.length; i++)
      b2a[i] = -1;
    for (int i = 0; i < a2b.length; i++)
      a2b[i] = -1;
    for (int i = 0; i < ab.length; i++) {
      b2a[ab[i][1]] = ab[i][0];
      a2b[ab[i][0]] = ab[i][1];
    }
  }

  private TurnB() {}
}
