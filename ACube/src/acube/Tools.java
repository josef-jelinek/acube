package acube;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Tools {

  public static final List<String> ReidOrder = joinLists(Edge.names, Corner.names);

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

  public static String[] splitSortString(final String s) {
    final String[] a = s.split(" ");
    Arrays.sort(a);
    return a;
  }

  public static <T> T[] sortArrayCopy(final T[] items) {
    final T[] a = items.clone();
    Arrays.sort(a);
    return a;
  }

  public static <T> List<T> joinLists(final List<T> list1, final List<T> list2) {
    final List<T> list = new ArrayList<T>(list1);
    list.addAll(list2);
    return Collections.unmodifiableList(list);
  }

  public static String intArrayToString(final int[] values) {
    if (values.length == 0)
      return "";
    final StringBuilder s = new StringBuilder();
    for (final int value : values)
      s.append(" " + value);
    return s.substring(1);
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

  private Tools() { }
}