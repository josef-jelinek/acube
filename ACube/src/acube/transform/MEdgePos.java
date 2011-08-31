package acube.transform;

import java.util.EnumSet;
import acube.Edge;
import acube.Turn;
import acube.pack.PackKit;

final class MEdgePos extends MoveToB<Edge> {
  public MEdgePos(final EnumSet<Edge> edgeMask) {
    super(PackKit.mEdgePos(edgeMask));
  }

  @Override
  public void turn(final Turn turn) {
    cycleEdges(turn);
  }

  @Override
  public boolean isInB() {
    return areUsedIn(PackKit.mEdgeMaskInB);
  }

  public void setup(final Edge[] edges) {
    pack.setValues(PackKit.mEdgePosOrdinals(edges));
  }
}
