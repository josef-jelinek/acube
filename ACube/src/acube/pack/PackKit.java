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

  public static int mEdgeIndexB(Edge edge) {
    int i = mEdgeIndicesB[edge.ordinal()];
    if (i >= 0)
      return i;
    throw new IllegalArgumentException("Illegal ring edge");
  }

  public static int oEdgeIndexB(Edge edge) {
    int i = oEdgeIndicesB[edge.ordinal()];
    if (i >= 0)
      return i;
    throw new IllegalArgumentException("Illegal U/D edge");
  }

  public static Pack cornerPosition(Corner[] mask) {
    return setupPack(new PackPositionFull(cornerMask(mask)), cornerOrdinals);
  }

  public static PackOrientation cornerTwist(Corner[] mask, Corner[] twistMask) {
    return setupPackOrientation(PackOrientation.instance(cornerMask(mask), cornerMask(twistMask), 3), cornerOrdinals);
  }

  public static PackOrientation edgeFlip(Edge[] mask, Edge[] flipMask) {
    return setupPackOrientation(PackOrientation.instance(edgeMask(mask), edgeMask(flipMask), 2), edgeOrdinals);
  }

  public static Pack mEdgePositionSet(Edge[] mask) {
    return setupPack(new PackPositionPartUnordered(edgeMask(mask), edgeMask(maskMEdgesB)), edgeOrdinals);
  }

  public static Pack mEdgePosition(Edge[] mask) {
    return setupPack(new PackPositionPartOrdered(edgeMask(mask), edgeMask(maskMEdgesB)), edgeOrdinals);
  }

  public static Pack uEdgePosition(Edge[] mask) {
    return setupPack(new PackPositionPartOrdered(edgeMask(mask), edgeMask(maskUEdges)), edgeOrdinals);
  }

  public static Pack dEdgePosition(Edge[] mask) {
    return setupPack(new PackPositionPartOrdered(edgeMask(mask), edgeMask(maskDEdges)), edgeOrdinals);
  }

  public static Pack mEdgePositionB(Edge[] mask) {
    return setupPack(new PackPositionFull(mEdgeMaskB(mask)), edgeOrdinals(mEdgesB));
  }

  public static Pack oEdgePositionB(Edge[] mask) {
    return setupPack(new PackPositionFull(oEdgeMaskB(mask)), edgeOrdinals(oEdgesB));
  }

  private static Pack setupPack(Pack pack, int[] parts) {
    pack.parts(parts);
    return pack;
  }

  private static PackOrientation setupPackOrientation(PackOrientation pack, int[] parts) {
    pack.parts(parts);
    return pack;
  }

  private static int[] edgeOrdinals(Edge[] edges) {
    int[] ordinals = new int[edges.length];
    for (int i = 0; i < edges.length; i++)
      ordinals[i] = edges[i].ordinal();
    return ordinals;
  }

  private static int[] cornerOrdinals(Corner[] corners) {
    int[] ordinals = new int[corners.length];
    for (int i = 0; i < corners.length; i++)
      ordinals[i] = corners[i].ordinal();
    return ordinals;
  }

  private static int[] edgeMask(Edge[] edges) {
    int[] mask = new int[Edge.size()];
    for (Edge edge : edges)
      mask[edge.ordinal()] = 1;
    return mask;
  }

  private static int[] cornerMask(Corner[] corners) {
    int[] mask = new int[Corner.size()];
    for (Corner corner : corners)
      mask[corner.ordinal()] = 1;
    return mask;
  }

  private static int[] mEdgeMaskB(Edge[] edges) {
    int[] mask = new int[mEdgesB.length];
    for (Edge edge : edges)
      if (mEdgeIndicesB[edge.ordinal()] >= 0)
        mask[mEdgeIndicesB[edge.ordinal()]] = 1;
    return mask;
  }

  private static int[] oEdgeMaskB(Edge[] edges) {
    int[] mask = new int[oEdgesB.length];
    for (Edge edge : edges)
      if (oEdgeIndicesB[edge.ordinal()] >= 0)
        mask[oEdgeIndicesB[edge.ordinal()]] = 1;
    return mask;
  }

  private static int[] edgeIndicesB(Edge[] edges) {
    int[] indices = new int[Edge.size()];
    for (int i = 0; i < indices.length; i++)
      indices[i] = -1;
    for (int i = 0; i < edges.length; i++)
      indices[edges[i].ordinal()] = i;
    return indices;
  }

  private PackKit() { }
}