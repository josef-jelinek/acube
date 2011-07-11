package acube.pack;

public abstract class CombFull implements ToPack {
  public static final CombFull perm = new CombPerm();
  static final CombFull permix = new CombPermix();

  private CombFull() {}
  public abstract int len(int n);
  public abstract void unpack(int[] arr, int x);

  private static final class CombPerm extends CombFull {
    public int len(int n) { // n!
      int x = 1;
      for (int i = n; i > 1; i--) x *= i;
      return x;
    }

    public int pack(int[] arr) {
      int x = 0;
      for (int i = 0; i < arr.length - 1; i++) {
        for (int j = i + 1; j < arr.length; j++)
          if (arr[j] < arr[i]) x++;
        x *= arr.length - i - 1;
      }
      return x;
    }

    public void unpack(int[] arr, int x) {
      arr[arr.length - 1] = 0;
      for (int i = arr.length - 2; i >= 0; i--) {
        arr[i] = x % (arr.length - i);
        x /= arr.length - i;
        for (int j = arr.length - 1; j > i; j--)
          if (arr[j] >= arr[i]) arr[j]++;
      }
    }

    /*
    public boolean check() {
      int[] c = new int[n];
      for (int i = 0; i < n; i++) {
        if (a[i] < 0 || a[i] >= n) return false; // the numbers must be in this range
        c[a[i]]++;
      }
      for (int i = 0; i < n; i++) // each number can appear exactly once
        if (c[i] != 1) return false;
      return true;
    }

    public int parity() {
      int p = 0;
      for (int i = 0; i < n - 1; i++) // how many pairs are not in order mod 2
        for (int j = i + 1; j < n; j++)
          if (a[i] > a[j])
            p = 1 - p;
      return p;
    }
    */
  }

  private static final class CombPermix extends CombFull {
    public int len(int n) {
      throw new UnsupportedOperationException("Not implemented");
    }

    public int pack(int[] arr) {
      int x = 0;
      for (int i = 0; i < arr.length - 1; i++) {
        if (arr[i] == 0) continue;
        int m = 0;
        for (int j = i + 1; j < arr.length; j++) {
          if (arr[j] == 0) continue;
          m++;
          if (arr[j] < arr[i]) x++;
        }
        if (m <= 1) return x;
        x *= m;
      }
      return 0;
    }

    public void unpack(int[] arr, int x) {
      int m = 0;
      for (int i = arr.length - 1; i >= 0; i--) {
        if (arr[i] == 0) continue;
        m++;
        arr[i] = x % m + 1;
        x /= m;
        for (int j = arr.length - 1; j > i; j--)
          if (arr[j] != 0 && arr[j] >= arr[i]) arr[j]++;
      }
    }
  }
}
