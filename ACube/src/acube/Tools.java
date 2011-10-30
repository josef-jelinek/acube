package acube;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

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

  public static boolean containsCharacters(final String s, final String chars) {
    for (final char c : chars.toCharArray())
      if (s.indexOf(c) < 0)
        return false;
    return true;
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

  public static String cornersKey(final EnumSet<Corner> set) {
    return Integer.toHexString(cornersToInt(set));
  }

  private static int cornersToInt(final EnumSet<Corner> set) {
    int x = 0;
    for (int i = 0; i < Corner.size; i++)
      x = x << 1 | (set.contains(Corner.value(i)) ? 1 : 0);
    return x;
  }

  public static String edgesKey(final EnumSet<Edge> set) {
    return Integer.toHexString(edgesToInt(set));
  }

  private static int edgesToInt(final EnumSet<Edge> set) {
    int x = 0;
    for (int i = 0; i < Edge.size; i++)
      x = x << 1 | (set.contains(Edge.value(i)) ? 1 : 0);
    return x;
  }

  private static final String CACHE_DIR = "cache";

  public static int[][] loadTable(final String prefix, final Turn[] turns) {
    BufferedReader r = null;
    try {
      if (new File(fileName(prefix, turns)).exists()) {
        r = new BufferedReader(new FileReader(fileName(prefix, turns)));
        final List<int[]> list = new ArrayList<int[]>();
        String line = r.readLine(); // header
        while ((line = r.readLine()) != null && line.length() > 0) {
          final String[] items = line.split("\\s+");
          final int[] row = new int[turns.length];
          for (int i = 0; i < turns.length; i++)
            row[i] = items[i].equals("-") ? -1 : Integer.parseInt(items[i]);
          list.add(row);
        }
        return list.toArray(new int[0][]);
      }
    } catch (final Exception e) {} finally {
      try {
        if (r != null)
          r.close();
      } catch (final IOException ee) {}
    }
    return null;
  }

  public static void saveTable(final String prefix, final int[][] table, final Turn[] turns) {
    Writer w = null;
    try {
      new File(CACHE_DIR).mkdir();
      w = new FileWriter(fileName(prefix, turns));
      String header = "";
      for (final Turn t : turns)
        header += t + "\t";
      w.append(header.trim() + "\r\n");
      for (final int[] element : table) {
        String line = "";
        for (final int element2 : element)
          line += element2 < 0 ? "-\t" : element2 + "\t";
        w.append(line.trim() + "\r\n");
      }
    } catch (final Exception e) {} finally {
      try {
        if (w != null)
          w.close();
      } catch (final Exception ee) {}
    }
  }

  private static String fileName(final String prefix, final Turn[] turns) {
    String name = "";
    for (final Turn t : turns)
      name += t.name();
    return CACHE_DIR + File.separator + prefix + name + ".txt";
  }

  public static byte[] loadTable(final String key, final int[] sizes) {
    InputStream in = null;
    try {
      final String fileName = CACHE_DIR + File.separator + "prune-" + key + ".acube";
      final File file = new File(fileName);
      if (file.exists() && file.isFile() && file.length() > 8) {
        int size = (int)file.length() - 8;
        in = new BufferedInputStream(new FileInputStream(file));
        final byte[] tab = new byte[size];
        Arrays.fill(sizes, 0);
        for (int i = 0; i < 2; i++)
          for (int j = 0; j < 4; j++)
            sizes[i] = sizes[i] << 8 | in.read();
        while (size > 0) {
          final int read = in.read(tab, tab.length - size, size);
          if (read == -1)
            return null;
          size -= read;
        }
        return tab;
      }
    } catch (final Exception e) {} finally {
      try {
        if (in != null)
          in.close();
      } catch (final IOException ee) {}
    }
    return null;
  }

  public static void saveTable(final String key, final int[] sizes, final byte[] table) {
    OutputStream out = null;
    try {
      new File(CACHE_DIR).mkdir();
      final String fileName = CACHE_DIR + File.separator + "prune-" + key + ".acube";
      out = new BufferedOutputStream(new FileOutputStream(fileName));
      for (int i = 0; i < 2; i++)
        for (int j = 24; j >= 0; j -= 8)
          out.write(sizes[i] >> j);
      out.write(table);
    } catch (final Exception e) {} finally {
      try {
        if (out != null)
          out.close();
      } catch (final Exception ee) {}
    }
  }

  public static boolean loadTable(final short[][] table, final String key) {
    InputStream in = null;
    try {
      final String fileName = CACHE_DIR + File.separator + "turns-" + key + ".acube";
      final File file = new File(fileName);
      if (file.exists() && file.isFile() && file.length() > table.length * 4) {
        Arrays.fill(table, null);
        in = new BufferedInputStream(new FileInputStream(file));
        for (int i = 0; i < table.length; i++) {
          int size = 0;
          for (int j = 0; j < 4; j++)
            size = size << 8 | in.read();
          if (size > 0) {
            table[i] = new short[size];
            for (int ii = 0; ii < size; ii++) {
              int val = 0;
              for (int j = 0; j < 2; j++) {
                final int c = in.read();
                if (c < 0)
                  return false;
                val = val << 8 | c & 255;
              }
              table[i][ii] = (short)val;
            }
          }
        }
        return true;
      }
    } catch (final Exception e) {} finally {
      try {
        if (in != null)
          in.close();
      } catch (final IOException ee) {}
    }
    return false;
  }

  public static boolean loadTable(final int[][] table, final String key) {
    InputStream in = null;
    try {
      final String fileName = CACHE_DIR + File.separator + "turns-" + key + ".acube";
      final File file = new File(fileName);
      if (file.exists() && file.isFile() && file.length() > table.length * 4) {
        Arrays.fill(table, null);
        in = new BufferedInputStream(new FileInputStream(file));
        for (int i = 0; i < table.length; i++) {
          int size = 0;
          for (int j = 0; j < 4; j++)
            size = size << 8 | in.read();
          if (size > 0) {
            table[i] = new int[size];
            for (int ii = 0; ii < size; ii++) {
              int val = 0;
              for (int j = 0; j < 4; j++) {
                final int c = in.read();
                if (c < 0)
                  return false;
                val = val << 8 | c & 255;
              }
              table[i][ii] = val;
            }
          }
        }
        return true;
      }
    } catch (final Exception e) {} finally {
      try {
        if (in != null)
          in.close();
      } catch (final IOException ee) {}
    }
    return false;
  }

  public static void saveTable(final short[][] table, final String key) {
    OutputStream out = null;
    try {
      new File(CACHE_DIR).mkdir();
      final String fileName = CACHE_DIR + File.separator + "turns-" + key + ".acube";
      out = new BufferedOutputStream(new FileOutputStream(fileName));
      for (final short[] tab : table) {
        final int size = tab == null ? 0 : tab.length;
        for (int j = 24; j >= 0; j -= 8)
          out.write(size >> j);
        if (tab != null)
          for (int ii = 0; ii < size; ii++)
            for (int j = 8; j >= 0; j -= 8)
              out.write(tab[ii] >> j);
      }
    } catch (final Exception e) {} finally {
      try {
        if (out != null)
          out.close();
      } catch (final Exception ee) {}
    }
  }

  public static void saveTable(final int[][] table, final String key) {
    OutputStream out = null;
    try {
      new File(CACHE_DIR).mkdir();
      final String fileName = CACHE_DIR + File.separator + "turns-" + key + ".acube";
      out = new BufferedOutputStream(new FileOutputStream(fileName));
      for (final int[] tab : table) {
        final int size = tab == null ? 0 : tab.length;
        for (int j = 24; j >= 0; j -= 8)
          out.write(size >> j);
        if (tab != null)
          for (int ii = 0; ii < size; ii++)
            for (int j = 24; j >= 0; j -= 8)
              out.write(tab[ii] >> j);
      }
    } catch (final Exception e) {} finally {
      try {
        if (out != null)
          out.close();
      } catch (final Exception ee) {}
    }
  }
}
