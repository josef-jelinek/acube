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

final class MEdgePosB extends Move<Edge> {
  private static final EnumSet<Edge> RING_EDGES = EnumSet.of(FR, FL, BR, BL);

  public MEdgePosB(final EnumSet<Edge> edgeMask) {
    super(new PackPositionOrdered<Edge>(maskRingEdges(edgeMask), RING_EDGES.toArray(new Edge[0])));
  }

  public static boolean[] maskRingEdges(final EnumSet<Edge> edgeMask) {
    final boolean[] mask = new boolean[RING_EDGES.size()];
    for (final Edge edge : edgeMask)
      if (PackKit.getEdgeIndicesIn(RING_EDGES)[edge.ordinal()] >= 0)
        mask[PackKit.getEdgeIndicesIn(RING_EDGES)[edge.ordinal()]] = true;
    return mask;
  }

  @Override
  public void turn(final Turn turn) {
    switch (turn) {
    case U1:
      break;
    case D1:
      break;
    case F2:
      swap(FR, FL);
      break;
    case B2:
      swap(BR, BL);
      break;
    case L2:
      swap(FL, BL);
      break;
    case R2:
      swap(FR, BR);
      break;
    default:
      throw new IllegalArgumentException("Unsupported or non primitive turn " + turn);
    }
  }

  @Override
  protected int getIndex(final Edge e) {
    return PackKit.getEdgeIndicesIn(RING_EDGES)[e.ordinal()];
  }
}
