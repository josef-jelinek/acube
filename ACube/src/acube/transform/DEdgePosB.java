package acube.transform;

import static acube.Edge.DB;
import static acube.Edge.DF;
import static acube.Edge.DL;
import static acube.Edge.DR;
import java.util.EnumSet;
import acube.Edge;

final class DEdgePosB extends UDEdgePosB {
  public DEdgePosB(final EnumSet<Edge> edgeMask) {
    super(getDEdges(edgeMask));
  }

  public static EnumSet<Edge> getDEdges(final EnumSet<Edge> edgeMask) {
    final EnumSet<Edge> set = EnumSet.copyOf(edgeMask);
    set.retainAll(EnumSet.of(DF, DR, DB, DL));
    return set;
  }
}
