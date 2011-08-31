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

class UDEdgePosB extends Move<Edge> {
  public UDEdgePosB(final EnumSet<Edge> edgeMask) {
    super(PackKit.udEdgePosB(edgeMask));
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
    return PackKit.oEdgeIndexB(e);
  }
}
