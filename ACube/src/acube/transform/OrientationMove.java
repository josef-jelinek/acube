package acube.transform;

import acube.Edge;
import acube.Corner;
import acube.Turn;
import acube.pack.PackOrientation;

abstract class OrientationMove extends Move {

  protected final PackOrientation packOrientation;

  public OrientationMove(PackOrientation pack, Turn[] turns) {
    super(pack, turns);
    this.packOrientation = pack;
  }

  protected void flip(Edge edge1, Edge edge2, Edge edge3, Edge edge4) {
    packOrientation.changeOrientation(edgeIndex(edge1), edgeIndex(edge2), edgeIndex(edge3), edgeIndex(edge4));
  }

  protected void twist(Corner corner1, Corner corner2, Corner corner3, Corner corner4) {
    packOrientation.changeOrientation(cornerIndex(corner1), cornerIndex(corner2), cornerIndex(corner3), cornerIndex(corner4));
  }
}