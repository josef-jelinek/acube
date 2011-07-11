package acube.pack;

public abstract class CombPart implements ToPack {
  public static final CombPart comb = new CombComb();
  public static final CombPart var = new CombVar();

  public static int used(int[] arr) {
    int k = 0;
    for (int i = 0; i < arr.length; i++)
      if (arr[i] != 0) k++;
    return k;
  }

  private CombPart() {}
  public abstract int len(int n, int k);
  public abstract void unpack(int[] arr, int k, int x);

  private static final class CombComb extends CombPart {
    public int len(int n, int k) { // n! / ((n - k)! * k!)
      int x = 1, y = 1;
      for (int i = n, j = k; j > 0; i--, j--) {
        x *= i;
        y *= j;
      }
      return x / y;
    }

    public int pack(int[] arr) {
      int x = 0, k = used(arr);
      for (int i = 0; i < arr.length - k; i++)
        if (arr[i] != 0) x += len(arr.length - i - 1, k--);
      return x;
    }

    public void unpack(int[] arr, int k, int x) {
      for (int i = 0, j = k; i < arr.length; i++) {
        int c = len(arr.length - i - 1, j);
        if (c > x) {
          arr[i] = 0;
        } else {
          x -= c;
          arr[i] = 1;
          j--;
        }
      }
    }
  }

  private static final class CombVar extends CombPart {
    public int len(int n, int k) { // n! / (n - k)!
      int x = 1;
      for (int i = n, j = k; j > 0; i--, j--) x *= i;
      return x;
    }

    public int pack(int[] arr) {
      return comb.pack(arr) * CombFull.perm.len(used(arr)) + CombFull.permix.pack(arr);
    }

    public void unpack(int[] arr, int k, int x) {
      comb.unpack(arr, k, x / CombFull.perm.len(k)); // order of comb
      CombFull.permix.unpack(arr, x % CombFull.perm.len(k)); // order of (sub)perm
    }

    /*
    public boolean check() {
      int[] c = new int[n];
      for (int i = 0; i < n; i++) {
        if (a[i] < 0 || a[i] > n) return false;
        if (a[i] > 0)
          c[a[i] - 1]++;
      }
      for (int i = 0; i < n; i++) // each number can appear at most once
        if (c[i] > 1) return false;
      return true;
    }

    public int parity() {
      int p = 0;
      for (int i = 0; i < n; i++) {
        if (a[i] == 0) {
          if (p == -1) return -2;
          p = -1;
        }
      }
      return p < 0 ? p : CombFull.perm.parity();
    }
    */
  }
}
