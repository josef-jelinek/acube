package acube.transform;

import java.util.Set;
import acube.Edge;
import acube.Turn;
import acube.pack.PackKit;

final class MEdgePos extends MoveToB {
  public MEdgePos(final Set<Edge> edgeMask, final Set<Turn> turnMask) {
    super(PackKit.mEdgePos(edgeMask), turnMask);
  }

  @Override
  public void turn(final Turn turn) {
    cycleEdges(turn);
  }

  @Override
  public boolean isInB() {
    return areUsedIn(PackKit.mEdgeMaskInB);
  }
}
