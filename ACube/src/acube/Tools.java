package acube;

import java.util.Arrays;

public final class Tools {

  public static <T> String arrayToString(final T[] parts) {
    final StringBuilder s = new StringBuilder();
    for (final T part : parts)
      s.append(" " + part);
    return s.substring(1);
  }

  public static String sortCharacters(final String s) {
    final char[] a = s.toCharArray();
    Arrays.sort(a);
    return new String(a);
  }

  public static String rotate(final String s, final int i) {
    return s.substring(i) + s.substring(0, i);
  }

  public static void swap(final int[] a, final int i, final int j) {
    final int t = a[i];
    a[i] = a[j];
    a[j] = t;
  }

  public static <T> void swap(final T[] a, final int i, final int j) {
    final T t = a[i];
    a[i] = a[j];
    a[j] = t;
  }

  public static void addMod(final int[] a, final int i, final int d, final int m) {
    a[i] = (a[i] + d + m) % m;
  }

  public static int[] identityPermutation(final int n) {
    final int[] a = new int[n];
    for (int i = 0; i < n; i++)
      a[i] = i;
    return a;
  }

  public static int[] subArray(final int[] a, final int begin, final int end) {
    final int[] b = new int[end - begin];
    System.arraycopy(a, begin, b, 0, end - begin);
    return b;
  }

  public static String substringAfter(final String s, final String start) {
    return s.startsWith(start) ? s.substring(start.length()) : "";
  }
}