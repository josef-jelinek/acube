package acube.transform;

import java.util.Set;
import acube.Edge;
import acube.Turn;
import acube.pack.PackKit;

final class MEdgePosSet extends Move {
  public MEdgePosSet(final Set<Edge> edgeMask, final Set<Turn> turnMask) {
    super(PackKit.mEdgePosSet(edgeMask), turnMask);
  }

  @Override
  public void turn(final Turn turn) {
    cycleEdges(turn);
  }
}
