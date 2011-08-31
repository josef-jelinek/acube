package acube.transform;

import java.util.EnumSet;
import acube.Edge;
import acube.Turn;
import acube.pack.PackKit;

final class MEdgePosSet extends Move<Edge> {
  public MEdgePosSet(final EnumSet<Edge> edgeMask) {
    super(PackKit.mEdgePosSet(edgeMask));
  }

  @Override
  public void turn(final Turn turn) {
    cycleEdges(turn);
  }
}
