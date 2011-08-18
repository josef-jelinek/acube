package acube.transform;

import java.util.Set;
import acube.Edge;
import acube.Turn;
import acube.pack.PackKit;

final class UEdgePos extends MoveToB {
  public UEdgePos(final Set<Edge> edgeMask, final Set<Turn> turnMask) {
    super(PackKit.uEdgePos(edgeMask), turnMask);
  }

  @Override
  public void turn(final Turn turn) {
    cycleEdges(turn);
  }

  @Override
  public boolean isInB() {
    return areUsedIn(PackKit.oEdgeMaskInB);
  }
}
