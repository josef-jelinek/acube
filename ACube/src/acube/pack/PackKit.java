package acube.pack;

import acube.Corner;
import acube.Edge;

public final class PackKit {
  private static final int[] cornerOrdinals = cornerOrdinals(Corner.values());
  private static final int[] edgeOrdinals = edgeOrdinals(Edge.values());
  private static final Edge[] mEdgesB = { Edge.FR, Edge.FL, Edge.BR, Edge.BL };
  private static final Edge[] oEdgesB = { Edge.UF, Edge.UR, Edge.UB, Edge.UL, Edge.DF, Edge.DR, Edge.DB, Edge.DL };
  private static final Edge[] maskMEdgesB = mEdgesB;
  private static final Edge[] maskUEdges = { Edge.UF, Edge.UR, Edge.UB, Edge.UL };
  private static final Edge[] maskDEdges = { Edge.DF, Edge.DR, Edge.DB, Edge.DL };
  private static final int[] mEdgeIndicesB = edgeIndicesB(mEdgesB);
  private static final int[] oEdgeIndicesB = edgeIndicesB(oEdgesB);

  public static int mEdgeIndexB(final Edge edge) {
    final int i = mEdgeIndicesB[edge.ordinal()];
    assert i >= 0;
    return i;
  }

  public static int oEdgeIndexB(final Edge edge) {
    final int i = oEdgeIndicesB[edge.ordinal()];
    assert i >= 0;
    return i;
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
    return new PackPositionFull(mEdgeMaskB(mask), edgeOrdinals(mEdgesB));
  }

  public static Pack oEdgePositionB(final Edge[] mask) {
    return new PackPositionFull(oEdgeMaskB(mask), edgeOrdinals(oEdgesB));
  }

  private static int[] edgeOrdinals(final Edge[] edges) {
    final int[] ordinals = new int[edges.length];
    for (int i = 0; i < edges.length; i++)
      ordinals[i] = edges[i].ordinal();
    return ordinals;
  }

  private static int[] cornerOrdinals(final Corner[] corners) {
    final int[] ordinals = new int[corners.length];
    for (int i = 0; i < corners.length; i++)
      ordinals[i] = corners[i].ordinal();
    return ordinals;
  }

  private static int[] edgeMask(final Edge[] edges) {
    final int[] mask = new int[Edge.size()];
    for (final Edge edge : edges)
      mask[edge.ordinal()] = 1;
    return mask;
  }

  private static int[] cornerMask(final Corner[] corners) {
    final int[] mask = new int[Corner.size()];
    for (final Corner corner : corners)
      mask[corner.ordinal()] = 1;
    return mask;
  }

  private static int[] mEdgeMaskB(final Edge[] edges) {
    final int[] mask = new int[mEdgesB.length];
    for (final Edge edge : edges)
      if (mEdgeIndicesB[edge.ordinal()] >= 0)
        mask[mEdgeIndicesB[edge.ordinal()]] = 1;
    return mask;
  }

  private static int[] oEdgeMaskB(final Edge[] edges) {
    final int[] mask = new int[oEdgesB.length];
    for (final Edge edge : edges)
      if (oEdgeIndicesB[edge.ordinal()] >= 0)
        mask[oEdgeIndicesB[edge.ordinal()]] = 1;
    return mask;
  }

  private static int[] edgeIndicesB(final Edge[] edges) {
    final int[] indices = new int[Edge.size()];
    for (int i = 0; i < indices.length; i++)
      indices[i] = -1;
    for (int i = 0; i < edges.length; i++)
      indices[edges[i].ordinal()] = i;
    return indices;
  }
}
