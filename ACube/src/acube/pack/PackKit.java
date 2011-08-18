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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import acube.Corner;
import acube.Edge;

public final class PackKit {
  private static final int[] cornerOrdinals = getCornerOrdinals(Corner.valueSet);
  private static final int[] edgeOrdinals = getEdgeOrdinals(Edge.valueSet);
  private static final SortedSet<Edge> maskOEdgesB = asSortedSet(new Edge[] { UF, UR, UB, UL, DF, DR, DB, DL });
  private static final SortedSet<Edge> maskMEdgesB = asSortedSet(new Edge[] { FR, FL, BR, BL });
  private static final SortedSet<Edge> maskUEdges = asSortedSet(new Edge[] { UF, UR, UB, UL });
  private static final SortedSet<Edge> maskDEdges = asSortedSet(new Edge[] { DF, DR, DB, DL });
  private static final int[] mEdgeIndicesB = getEdgeIndicesIn(maskMEdgesB);
  private static final int[] oEdgeIndicesB = getEdgeIndicesIn(maskOEdgesB);
  public static final boolean[] mEdgeMaskInB = getEdgeMaskFor(maskMEdgesB);
  public static final boolean[] oEdgeMaskInB = getEdgeMaskFor(maskOEdgesB);

  private static <T> SortedSet<T> asSortedSet(final T[] a) {
    return Collections.unmodifiableSortedSet(new TreeSet<T>(Arrays.asList(a)));
  }

  public static int mEdgeIndexB(final Edge edge) {
    assert mEdgeIndicesB[edge.ordinal()] > 0;
    return mEdgeIndicesB[edge.ordinal()];
  }

  public static int oEdgeIndexB(final Edge edge) {
    assert oEdgeIndicesB[edge.ordinal()] > 0;
    return oEdgeIndicesB[edge.ordinal()];
  }

  public static Pack cornerPos(final Set<Corner> cornerMask) {
    return new PackPositionFull(cornerMask(cornerMask), cornerOrdinals);
  }

  public static PackOrientation cornerTwist(final Set<Corner> cornerMask, final Set<Corner> cornerTwistMask) {
    return PackOrientation.instance(cornerMask(cornerMask), cornerMask(cornerTwistMask), cornerOrdinals, 3);
  }

  public static PackOrientation edgeFlip(final Set<Edge> edgeMask, final Set<Edge> edgeFlipMask) {
    return PackOrientation.instance(edgeMask(edgeMask), edgeMask(edgeFlipMask), edgeOrdinals, 2);
  }

  public static Pack mEdgePosSet(final Set<Edge> edgeMask) {
    return new PackPositionPartUnordered(edgeMask(edgeMask), edgeMask(maskMEdgesB), edgeOrdinals);
  }

  public static Pack mEdgePos(final Set<Edge> edgeMask) {
    return new PackPositionPartOrdered(edgeMask(edgeMask), edgeMask(maskMEdgesB), edgeOrdinals);
  }

  public static Pack uEdgePos(final Set<Edge> edgeMask) {
    return new PackPositionPartOrdered(edgeMask(edgeMask), edgeMask(maskUEdges), edgeOrdinals);
  }

  public static Pack dEdgePos(final Set<Edge> edgeMask) {
    return new PackPositionPartOrdered(edgeMask(edgeMask), edgeMask(maskDEdges), edgeOrdinals);
  }

  public static Pack mEdgePos_B(final Set<Edge> edgeMask) {
    return new PackPositionFull(mEdgeMaskB(edgeMask), getEdgeOrdinals(maskMEdgesB));
  }

  public static Pack oEdgePos_B(final Set<Edge> edgeMask) {
    return new PackPositionFull(oEdgeMaskB(edgeMask), getEdgeOrdinals(maskOEdgesB));
  }

  private static int[] getEdgeOrdinals(final SortedSet<Edge> edges) {
    final int[] ordinals = new int[edges.size()];
    int i = 0;
    for (final Edge e : edges)
      ordinals[i++] = e.ordinal();
    return ordinals;
  }

  private static int[] getCornerOrdinals(final SortedSet<Corner> corners) {
    final int[] ordinals = new int[corners.size()];
    int i = 0;
    for (final Corner c : corners)
      ordinals[i++] = c.ordinal();
    return ordinals;
  }

  private static boolean[] edgeMask(final Set<Edge> edgeMask) {
    final boolean[] mask = new boolean[Edge.size];
    for (final Edge edge : edgeMask)
      mask[edge.ordinal()] = true;
    return mask;
  }

  private static boolean[] cornerMask(final Set<Corner> cornerMask) {
    final boolean[] mask = new boolean[Corner.size];
    for (final Corner corner : cornerMask)
      mask[corner.ordinal()] = true;
    return mask;
  }

  private static boolean[] getEdgeMaskFor(final Set<Edge> edgeMask) {
    final boolean[] mask = new boolean[Edge.size];
    for (final Edge edge : edgeMask)
      mask[edge.ordinal()] = true;
    return mask;
  }

  private static boolean[] mEdgeMaskB(final Set<Edge> edgeMask) {
    final boolean[] mask = new boolean[maskMEdgesB.size()];
    for (final Edge edge : edgeMask)
      if (mEdgeIndicesB[edge.ordinal()] >= 0)
        mask[mEdgeIndicesB[edge.ordinal()]] = true;
    return mask;
  }

  private static boolean[] oEdgeMaskB(final Set<Edge> edgeMask) {
    final boolean[] mask = new boolean[maskOEdgesB.size()];
    for (final Edge edge : edgeMask)
      if (oEdgeIndicesB[edge.ordinal()] >= 0)
        mask[oEdgeIndicesB[edge.ordinal()]] = true;
    return mask;
  }

  private static int[] getEdgeIndicesIn(final Set<Edge> edgeMask) {
    final int[] indices = new int[Edge.size];
    for (int i = 0; i < indices.length; i++)
      indices[i] = -1;
    final Edge[] edges = edgeMask.toArray(new Edge[edgeMask.size()]);
    for (int i = 0; i < edges.length; i++)
      indices[edges[i].ordinal()] = i;
    return indices;
  }

  public static Set<Edge> getUEdges(final Set<Edge> edgeMask) {
    return getIntersection(edgeMask, maskUEdges);
  }

  public static Set<Edge> getDEdges(final Set<Edge> edgeMask) {
    return getIntersection(edgeMask, maskDEdges);
  }

  private static Set<Edge> getIntersection(final Set<Edge> edgeMask1, final Set<Edge> edgeMask2) {
    final Set<Edge> edgeMask = new HashSet<Edge>();
    for (final Edge e : edgeMask1)
      if (edgeMask2.contains(e))
        edgeMask.add(e);
    return edgeMask;
  }
}
