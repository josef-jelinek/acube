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
import acube.Corner;
import acube.Edge;

public final class PackKit {
  private static final int[] cornerOrdinals = getCornerOrdinals(Corner.values());
  private static final int[] edgeOrdinals = getEdgeOrdinals(Edge.values());
  private static final Edge[] mEdgesB = { FR, FL, BR, BL };
  private static final Edge[] oEdgesB = { UF, UR, UB, UL, DF, DR, DB, DL };
  private static final Edge[] maskMEdgesB = mEdgesB;
  private static final Edge[] maskUEdges = { UF, UR, UB, UL };
  private static final Edge[] maskDEdges = { DF, DR, DB, DL };
  private static final int[] mEdgeIndicesB = getEdgeIndicesIn(mEdgesB);
  private static final int[] oEdgeIndicesB = getEdgeIndicesIn(oEdgesB);
  public static final boolean[] mEdgeMaskInB = getEdgeMaskFor(mEdgesB);
  public static final boolean[] oEdgeMaskInB = getEdgeMaskFor(oEdgesB);

  public static int mEdgeIndexB(final Edge edge) {
    assert mEdgeIndicesB[edge.ordinal()] > 0;
    return mEdgeIndicesB[edge.ordinal()];
  }

  public static int oEdgeIndexB(final Edge edge) {
    assert oEdgeIndicesB[edge.ordinal()] > 0;
    return oEdgeIndicesB[edge.ordinal()];
  }

  public static Pack cornerPosition(final Corner[] mask) {
    return new PackPositionFull(cornerMask(mask), cornerOrdinals);
  }

  public static PackOrientation cornerTwist(final Corner[] mask, final Corner[] twistMask) {
    return PackOrientation.instance(cornerMask(mask), cornerMask(twistMask), cornerOrdinals, 3);
  }

  public static PackOrientation edgeFlip(final Edge[] mask, final Edge[] flipMask) {
    return PackOrientation.instance(edgeMask(mask), edgeMask(flipMask), edgeOrdinals, 2);
  }

  public static Pack mEdgePositionSet(final Edge[] mask) {
    return new PackPositionPartUnordered(edgeMask(mask), edgeMask(maskMEdgesB), edgeOrdinals);
  }

  public static Pack mEdgePosition(final Edge[] mask) {
    return new PackPositionPartOrdered(edgeMask(mask), edgeMask(maskMEdgesB), edgeOrdinals);
  }

  public static Pack uEdgePosition(final Edge[] mask) {
    return new PackPositionPartOrdered(edgeMask(mask), edgeMask(maskUEdges), edgeOrdinals);
  }

  public static Pack dEdgePosition(final Edge[] mask) {
    return new PackPositionPartOrdered(edgeMask(mask), edgeMask(maskDEdges), edgeOrdinals);
  }

  public static Pack mEdgePositionB(final Edge[] mask) {
    return new PackPositionFull(mEdgeMaskB(mask), getEdgeOrdinals(mEdgesB));
  }

  public static Pack oEdgePositionB(final Edge[] mask) {
    return new PackPositionFull(oEdgeMaskB(mask), getEdgeOrdinals(oEdgesB));
  }

  private static int[] getEdgeOrdinals(final Edge[] edges) {
    final int[] ordinals = new int[edges.length];
    for (int i = 0; i < edges.length; i++)
      ordinals[i] = edges[i].ordinal();
    return ordinals;
  }

  private static int[] getCornerOrdinals(final Corner[] corners) {
    final int[] ordinals = new int[corners.length];
    for (int i = 0; i < corners.length; i++)
      ordinals[i] = corners[i].ordinal();
    return ordinals;
  }

  private static boolean[] edgeMask(final Edge[] edges) {
    final boolean[] mask = new boolean[Edge.size()];
    for (final Edge edge : edges)
      mask[edge.ordinal()] = true;
    return mask;
  }

  private static boolean[] cornerMask(final Corner[] corners) {
    final boolean[] mask = new boolean[Corner.size()];
    for (final Corner corner : corners)
      mask[corner.ordinal()] = true;
    return mask;
  }

  private static boolean[] getEdgeMaskFor(final Edge[] edges) {
    final boolean[] mask = new boolean[Edge.size()];
    for (final Edge edge : edges)
      mask[edge.ordinal()] = true;
    return mask;
  }

  private static boolean[] mEdgeMaskB(final Edge[] edges) {
    final boolean[] mask = new boolean[mEdgesB.length];
    for (final Edge edge : edges)
      if (mEdgeIndicesB[edge.ordinal()] >= 0)
        mask[mEdgeIndicesB[edge.ordinal()]] = true;
    return mask;
  }

  private static boolean[] oEdgeMaskB(final Edge[] edges) {
    final boolean[] mask = new boolean[oEdgesB.length];
    for (final Edge edge : edges)
      if (oEdgeIndicesB[edge.ordinal()] >= 0)
        mask[oEdgeIndicesB[edge.ordinal()]] = true;
    return mask;
  }

  private static int[] getEdgeIndicesIn(final Edge[] edges) {
    final int[] indices = new int[Edge.size()];
    for (int i = 0; i < indices.length; i++)
      indices[i] = -1;
    for (int i = 0; i < edges.length; i++)
      indices[edges[i].ordinal()] = i;
    return indices;
  }
}
