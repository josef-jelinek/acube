package acube;

public final class Metric {
  public static final int QTM = 0;
  public static final int FTM = 1;
  public static final int STM = 2;
  public static final int SQTM = 3;
  public static final int N = 4;

  public static final String[] names = {
    "QTM", "FTM", "STM", "SQTM",
  };

  private static Metric[] metrics = new Metric[N]; // singletons

  public static Metric obj(int metric) {
    return metrics[metric] == null ? metrics[metric] = new Metric(metric) : metrics[metric];
  }

  private final int metric;
  private final int[][] len = new int[N][Turn.N];

  public int length(int turn) {
    return length(turn, metric);
  }

  public int length(int turn, int metric) {
    return len[metric][turn];
  }

  public int metric() {
    return metric;
  }

  private static final int[] face = {
    Turn.U2, Turn.D2, Turn.F2, Turn.B2, Turn.L2, Turn.R2,
    Turn.u2, Turn.d2, Turn.f2, Turn.b2, Turn.l2, Turn.r2,
  };

  private static final int[] quarterslice = {
    Turn.E1, Turn.E3, Turn.S1, Turn.S3, Turn.M1, Turn.M3,
  };

  private static final int[] slice = {
    Turn.E2, Turn.S2, Turn.M2,
  };

  private Metric(int metric) {
    this.metric = metric;
    for (int m = 0; m < len.length; m++)
      for (int t = 0; t < len[m].length; t++)
        len[m][t] = 1;
    setLens(len[QTM], face, 2);
    setLens(len[SQTM], face, 2);
    setLens(len[QTM], quarterslice, 2);
    setLens(len[FTM], quarterslice, 2);
    setLens(len[QTM], slice, 4);
    setLens(len[FTM], slice, 2);
    setLens(len[SQTM], slice, 2);
  }

  private void setLens(int[] lens, int[] turns, int len) {
    for (int i = 0; i < turns.length; i++)
      lens[turns[i]] = len;
  }
}
