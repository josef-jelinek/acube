package acube.pack;

import static acube.Edge.DB;
import static acube.Edge.DF;
import static acube.Edge.DL;
import static acube.Edge.DR;
import static acube.Edge.UB;
import static acube.Edge.UF;
import static acube.Edge.UL;
import static acube.Edge.UR;
import java.util.EnumSet;
import acube.Corner;
import acube.Edge;

public final class PackKit {
  public static boolean[] cornerMask(final EnumSet<Corner> cornerMask) {
    final boolean[] mask = new boolean[Corner.size];
    for (final Corner corner : cornerMask)
      mask[corner.ordinal()] = true;
    return mask;
  }

  public static boolean[] edgeMask(final EnumSet<Edge> edgeMask) {
    final boolean[] mask = new boolean[Edge.size];
    for (final Edge edge : edgeMask)
      mask[edge.ordinal()] = true;
    return mask;
  }

  public static boolean[] getEdgeMaskFor(final EnumSet<Edge> edgeMask) {
    final boolean[] mask = new boolean[Edge.size];
    for (final Edge edge : edgeMask)
      mask[edge.ordinal()] = true;
    return mask;
  }

  public static boolean[] udEdgeMaskB(final EnumSet<Edge> edgeMask) {
    final boolean[] mask = new boolean[EnumSet.of(UF, UR, UB, UL, DF, DR, DB, DL).size()];
    for (final Edge edge : edgeMask)
      if (getEdgeIndicesIn(EnumSet.of(UF, UR, UB, UL, DF, DR, DB, DL))[edge.ordinal()] >= 0)
        mask[getEdgeIndicesIn(EnumSet.of(UF, UR, UB, UL, DF, DR, DB, DL))[edge.ordinal()]] = true;
    return mask;
  }

  public static int[] getEdgeIndicesIn(final EnumSet<Edge> edgeMask) {
    final int[] indices = new int[Edge.size];
    for (int i = 0; i < indices.length; i++)
      indices[i] = -1;
    final Edge[] edges = edgeMask.toArray(new Edge[0]);
    for (int i = 0; i < edges.length; i++)
      indices[edges[i].ordinal()] = i;
    return indices;
  }

  public static <T> int[] fillIndices(final T[] pieces, final T[] piecesToSelect) {
    final int[] a = new int[pieces.length];
    for (int i = 0; i < pieces.length; i++) {
      final int ei = getIndex(piecesToSelect, pieces[i]);
      a[i] = ei < 0 ? -1 : ei;
    }
    return a;
  }

  private static <T> int getIndex(final T[] a, final T o) {
    for (int i = 0; i < a.length; i++)
      if (a[i] == o)
        return i;
    return -1;
  }
}
