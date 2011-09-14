package acube.transform;

import static acube.Edge.DB;
import static acube.Edge.DF;
import static acube.Edge.DL;
import static acube.Edge.DR;
import static acube.Edge.UB;
import static acube.Edge.UF;
import static acube.Edge.UL;
import static acube.Edge.UR;
import java.util.EnumSet;
import acube.Edge;
import acube.Turn;
import acube.pack.PackKit;
import acube.pack.PackPositionOrdered;

class UDEdgePosB extends Move<Edge> {
  private static final EnumSet<Edge> UD_EDGES = EnumSet.of(UF, UR, UB, UL, DF, DR, DB, DL);

  public UDEdgePosB(final EnumSet<Edge> edgeMask) {
    super(new PackPositionOrdered<Edge>(PackKit.udEdgeMaskB(edgeMask), UD_EDGES.toArray(new Edge[0])));
  }

  @Override
  public void turn(final Turn turn) {
    switch (turn) {
    case U1:
      cycle(UR, UB, UL, UF);
      break;
    case D1:
      cycle(DL, DB, DR, DF);
      break;
    case F2:
      swap(UF, DF);
      break;
    case B2:
      swap(UB, DB);
      break;
    case L2:
      swap(UL, DL);
      break;
    case R2:
      swap(UR, DR);
      break;
    default:
      throw new IllegalArgumentException("Unsupported or non primitive turn");
    }
  }

  @Override
  protected int getIndex(final Edge e) {
    return PackKit.getEdgeIndicesIn(UD_EDGES)[e.ordinal()];
  }
}
