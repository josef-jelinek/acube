package acube.transform;

import java.util.EnumSet;
import acube.Edge;
import acube.pack.PackKit;

final class UEdgePosB extends UDEdgePosB {
  public UEdgePosB(final EnumSet<Edge> edgeMask) {
    super(PackKit.getUEdges(edgeMask));
  }
}
