package acube.transform;

import static acube.Edge.BL;
import static acube.Edge.BR;
import static acube.Edge.FL;
import static acube.Edge.FR;
import java.util.EnumSet;
import acube.Edge;
import acube.Turn;
import acube.pack.PackKit;
import acube.pack.PackPositionOrdered;

final class MEdgePos extends MoveToB<Edge> {
  private static final EnumSet<Edge> RING_EDGES = EnumSet.of(FR, FL, BR, BL);
  private static final boolean[] RING_EDGE_ARRAY = PackKit.getEdgeMaskFor(RING_EDGES);

  public MEdgePos(final EnumSet<Edge> edgeMask) {
    super(new PackPositionOrdered<Edge>(PackKit.edgeMask(edgeMask), PackKit.edgeMask(RING_EDGES), Edge.values()));
  }

  @Override
  public void turn(final Turn turn) {
    cycleEdges(turn);
  }

  @Override
  public boolean isInB() {
    return areUsedIn(RING_EDGE_ARRAY);
  }

  public void setup(final Edge[] edges) {
    pack.setValues(PackKit.fillIndices(edges, RING_EDGES.toArray(new Edge[0])));
  }
}
