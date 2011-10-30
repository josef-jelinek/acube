package acube.transform;

import static acube.Edge.BL;
import static acube.Edge.BR;
import static acube.Edge.FL;
import static acube.Edge.FR;
import static acube.Tools.edgesKey;
import java.util.EnumSet;
import acube.Edge;
import acube.Turn;
import acube.pack.PackKit;
import acube.pack.PackPositionUnordered;

final class MEdgePosSet extends Move<Edge> {
  private static final EnumSet<Edge> RING_EDGES = EnumSet.of(FR, FL, BR, BL);

  public MEdgePosSet(final EnumSet<Edge> edgeMask) {
    super(new PackPositionUnordered<Edge>(PackKit.edgeMask(edgeMask), PackKit.edgeMask(RING_EDGES), Edge.values()),
        "MEPS-" + edgesKey(edgeMask));
  }

  @Override
  public void turn(final Turn turn) {
    cycleEdges(turn);
  }
}
