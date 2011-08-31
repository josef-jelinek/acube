package acube.transform;

import java.util.EnumSet;
import acube.Edge;
import acube.Turn;
import acube.pack.PackKit;

final class UEdgePos extends MoveToB<Edge> {
  public UEdgePos(final EnumSet<Edge> edgeMask) {
    super(PackKit.uEdgePos(edgeMask));
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
    pack.setValues(PackKit.uEdgePosOrdinals(edges));
  }
}
