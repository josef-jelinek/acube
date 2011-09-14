package acube.transform;

import static acube.Edge.UB;
import static acube.Edge.UF;
import static acube.Edge.UL;
import static acube.Edge.UR;
import java.util.EnumSet;
import acube.Edge;

final class UEdgePosB extends UDEdgePosB {
  public UEdgePosB(final EnumSet<Edge> edgeMask) {
    super(getUEdges(edgeMask));
  }

  public static EnumSet<Edge> getUEdges(final EnumSet<Edge> edgeMask) {
    final EnumSet<Edge> set = EnumSet.copyOf(edgeMask);
    set.retainAll(EnumSet.of(UF, UR, UB, UL));
    return set;
  }
}
