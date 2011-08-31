package acube.pack;

import static acube.Edge.BL;
import static acube.Edge.BR;
import static acube.Edge.DB;
import static acube.Edge.DF;
import static acube.Edge.DL;
import static acube.Edge.DR;
import static acube.Edge.FL;
import static acube.Edge.FR;
import static acube.Edge.UB;
import static acube.Edge.UF;
import static acube.Edge.UL;
import static acube.Edge.UR;
import java.util.EnumSet;
import acube.Corner;
import acube.Edge;

public final class PackKit {
  private static final EnumSet<Edge> maskUDEdgesB = EnumSet.of(UF, UR, UB, UL, DF, DR, DB, DL);
  private static final EnumSet<Edge> maskMEdges = EnumSet.of(FR, FL, BR, BL);
  private static final EnumSet<Edge> maskUEdges = EnumSet.of(UF, UR, UB, UL);
  private static final EnumSet<Edge> maskDEdges = EnumSet.of(DF, DR, DB, DL);
  private static final int[] mEdgeIndicesB = getEdgeIndicesIn(maskMEdges);
  private static final int[] udEdgeIndicesB = getEdgeIndicesIn(maskUDEdgesB);
  public static final boolean[] mEdgeMaskInB = getEdgeMaskFor(maskMEdges);
  public static final boolean[] udEdgeMaskInB = getEdgeMaskFor(maskUDEdgesB);

  public static int mEdgeIndexB(final Edge edge) {
    assert mEdgeIndicesB[edge.ordinal()] > 0;
    return mEdgeIndicesB[edge.ordinal()];
  }

  public static int oEdgeIndexB(final Edge edge) {
    assert udEdgeIndicesB[edge.ordinal()] > 0;
    return udEdgeIndicesB[edge.ordinal()];
  }

  public static Pack<Corner> cornerPos(final EnumSet<Corner> cornerMask) {
    return new PackPositionOrdered<Corner>(cornerMask(cornerMask), Corner.values());
  }

  public static PackOrientation<Corner> cornerTwist(final EnumSet<Corner> cornerMask, final EnumSet<Corner> twistMask) {
    return PackOrientation.instance(cornerMask(cornerMask), cornerMask(twistMask), Corner.values(), 3);
  }

  public static PackOrientation<Edge> edgeFlip(final EnumSet<Edge> edgeMask, final EnumSet<Edge> flipMask) {
    return PackOrientation.instance(edgeMask(edgeMask), edgeMask(flipMask), Edge.values(), 2);
  }

  public static Pack<Edge> mEdgePosSet(final EnumSet<Edge> edgeMask) {
    return new PackPositionUnordered<Edge>(edgeMask(edgeMask), edgeMask(maskMEdges), Edge.values());
  }

  public static Pack<Edge> mEdgePos(final EnumSet<Edge> edgeMask) {
    return new PackPositionOrdered<Edge>(edgeMask(edgeMask), edgeMask(maskMEdges), Edge.values());
  }

  public static Pack<Edge> uEdgePos(final EnumSet<Edge> edgeMask) {
    return new PackPositionOrdered<Edge>(edgeMask(edgeMask), edgeMask(maskUEdges), Edge.values());
  }

  public static Pack<Edge> dEdgePos(final EnumSet<Edge> edgeMask) {
    return new PackPositionOrdered<Edge>(edgeMask(edgeMask), edgeMask(maskDEdges), Edge.values());
  }

  public static Pack<Edge> mEdgePosB(final EnumSet<Edge> edgeMask) {
    return new PackPositionOrdered<Edge>(mEdgeMaskB(edgeMask), maskMEdges.toArray(new Edge[0]));
  }

  public static Pack<Edge> udEdgePosB(final EnumSet<Edge> edgeMask) {
    return new PackPositionOrdered<Edge>(udEdgeMaskB(edgeMask), maskUDEdgesB.toArray(new Edge[0]));
  }

  private static boolean[] edgeMask(final EnumSet<Edge> edgeMask) {
    final boolean[] mask = new boolean[Edge.size];
    for (final Edge edge : edgeMask)
      mask[edge.ordinal()] = true;
    return mask;
  }

  private static boolean[] cornerMask(final EnumSet<Corner> cornerMask) {
    final boolean[] mask = new boolean[Corner.size];
    for (final Corner corner : cornerMask)
      mask[corner.ordinal()] = true;
    return mask;
  }

  private static boolean[] getEdgeMaskFor(final EnumSet<Edge> edgeMask) {
    final boolean[] mask = new boolean[Edge.size];
    for (final Edge edge : edgeMask)
      mask[edge.ordinal()] = true;
    return mask;
  }

  private static boolean[] mEdgeMaskB(final EnumSet<Edge> edgeMask) {
    final boolean[] mask = new boolean[maskMEdges.size()];
    for (final Edge edge : edgeMask)
      if (mEdgeIndicesB[edge.ordinal()] >= 0)
        mask[mEdgeIndicesB[edge.ordinal()]] = true;
    return mask;
  }

  private static boolean[] udEdgeMaskB(final EnumSet<Edge> edgeMask) {
    final boolean[] mask = new boolean[maskUDEdgesB.size()];
    for (final Edge edge : edgeMask)
      if (udEdgeIndicesB[edge.ordinal()] >= 0)
        mask[udEdgeIndicesB[edge.ordinal()]] = true;
    return mask;
  }

  private static int[] getEdgeIndicesIn(final EnumSet<Edge> edgeMask) {
    final int[] indices = new int[Edge.size];
    for (int i = 0; i < indices.length; i++)
      indices[i] = -1;
    final Edge[] edges = edgeMask.toArray(new Edge[0]);
    for (int i = 0; i < edges.length; i++)
      indices[edges[i].ordinal()] = i;
    return indices;
  }

  public static EnumSet<Edge> getUEdges(final EnumSet<Edge> edgeMask) {
    final EnumSet<Edge> set = EnumSet.copyOf(edgeMask);
    set.retainAll(maskUEdges);
    return set;
  }

  public static EnumSet<Edge> getDEdges(final EnumSet<Edge> edgeMask) {
    final EnumSet<Edge> set = EnumSet.copyOf(edgeMask);
    set.retainAll(maskDEdges);
    return set;
  }

  public static int[] mEdgePosOrdinals(final Edge[] edges) {
    return fillIndices(edges, maskMEdges.toArray(new Edge[0]));
  }

  public static int[] uEdgePosOrdinals(final Edge[] edges) {
    return fillIndices(edges, maskUEdges.toArray(new Edge[0]));
  }

  public static int[] dEdgePosOrdinals(final Edge[] edges) {
    return fillIndices(edges, maskDEdges.toArray(new Edge[0]));
  }

  private static <T> int[] fillIndices(final T[] pieces, final T[] piecesToSelect) {
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
