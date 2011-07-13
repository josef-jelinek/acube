package acube;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Tools {

  public static final List<String> ReidOrder = joinLists(Edge.names, Corner.names);

  public static <T> String arrayToString(T[] parts) {
    StringBuilder s = new StringBuilder();
    for (int i = 0; i < parts.length; i++)
      s.append(" " + parts[i]);
    return s.substring(1);
  }

  public static String sortCharacters(String s) {
    char[] a = s.toCharArray();
    Arrays.sort(a);
    return new String(a);
  }

  public static String[] splitSortString(String s) {
    String[] a = s.split(" ");
    Arrays.sort(a);
    return a;
  }

  public static <T> T[] sortArrayCopy(T[] items) {
    T[] a = items.clone();
    Arrays.sort(a);
    return a;
  }

  public static <T> List<T> joinLists(List<T> list1, List<T> list2) {
    List<T> list = new ArrayList<T>(list1);
    list.addAll(list2);
    return Collections.unmodifiableList(list);
  }

  public static String intArrayToString(int[] values) {
    if (values.length == 0)
      return "";
    StringBuilder s = new StringBuilder();
    for (int i = 0; i < values.length; i++)
      s.append(" " + values[i]);
    return s.substring(1);
  }

  public static boolean containsSomeNotAll(String where, String[] what) {
    String[] sortedWhere = splitSortString(where);
    return containsSome(sortedWhere, what) && !containsAll(sortedWhere, what);
  }

  public static <T> boolean containsAll(T[] sortedWhere, T[] what) {
    for (int i = 0; i < what.length; i++)
      if (Arrays.binarySearch(sortedWhere, what[i]) < 0)
        return false;
    return true;
  }

  public static <T> boolean containsSome(T[] sortedWhere, T[] what) {
    for (int i = 0; i < what.length; i++)
      if (Arrays.binarySearch(sortedWhere, what[i]) >= 0)
        return true;
    return false;
  }

  public static <T> boolean containsDuplicities(T[] items) {
    for (int i = 0; i < items.length - 1; i++)
      for (int j = i + 1; j < items.length; j++)
        if (items[i].equals(items[j]))
          return true;
    return false;
  }

  public static String rotate(String s, int i) {
    return s.substring(i) + s.substring(0, i);
  }

  public static void swap(int[] a, int i, int j) {
    int t = a[i];
    a[i] = a[j];
    a[j] = t;
  }

  public static <T> void swap(T[] a, int i, int j) {
    T t = a[i];
    a[i] = a[j];
    a[j] = t;
  }

  public static void addMod(int[] a, int i, int d, int m) {
    a[i] = (a[i] + d + m) % m;
  }

  public static int[] identityPermutation(int n) {
    int[] a = new int[n];
    for (int i = 0; i < n; i++)
      a[i] = i;
    return a;
  }

  public static int[] subArray(int[] a, int begin, int end) {
    int[] b = new int[end - begin];
    System.arraycopy(a, begin, b, 0, end - begin);
    return b;
  }

  private Tools() { }
}