package acube.transform;

import java.util.EnumSet;
import acube.Edge;
import acube.Turn;
import acube.pack.PackKit;

final class DEdgePos extends MoveToB<Edge> {
  public DEdgePos(final EnumSet<Edge> edgeMask) {
    super(PackKit.dEdgePos(edgeMask));
  }

  @Override
  public void turn(final Turn turn) {
    cycleEdges(turn);
  }

  @Override
  public boolean isInB() {
    return areUsedIn(PackKit.udEdgeMaskInB);
  }

  public void setup(final Edge[] edges) {
    pack.setValues(PackKit.dEdgePosOrdinals(edges));
  }
}
