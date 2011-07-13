package acube;

public enum Symmetry {

  U4, F2, UFR3, LR2;

  private static int[] sizes = {4, 2, 3, 2};

  public int size() {
    return sizes[ordinal()];
  }

  public static int totalSize() {
    return 4 * 2 * 3 * 2;
  }

  public static int symmetry(int lr2, int ufr3, int f2, int u4) {
    return ((((lr2 * UFR3.size()) + ufr3) * F2.size()) + f2) * U4.size() + u4;
  }


}