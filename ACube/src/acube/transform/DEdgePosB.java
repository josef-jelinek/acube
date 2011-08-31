package acube.transform;

import java.util.EnumSet;
import acube.Edge;
import acube.pack.PackKit;

final class DEdgePosB extends UDEdgePosB {
  public DEdgePosB(final EnumSet<Edge> edgeMask) {
    super(PackKit.getDEdges(edgeMask));
  }
}
