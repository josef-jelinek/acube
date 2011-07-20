package acube.transform;

import acube.Edge;
import acube.Turn;
import acube.pack.PackKit;

final class OEdgePositionB extends Move {
  public OEdgePositionB(final Edge[] mask, final Turn[] turns) {
    super(PackKit.oEdgePositionB(mask), turns);
  }

  @Override
  public void turn(final Turn turn) {
    switch (turn.toB()) {
    case U1:
      cycle(Edge.UR, Edge.UB, Edge.UL, Edge.UF);
      break;
    case D1:
      cycle(Edge.DL, Edge.DB, Edge.DR, Edge.DF);
      break;
    case F2:
      swap(Edge.UF, Edge.DF);
      break;
    case B2:
      swap(Edge.UB, Edge.DB);
      break;
    case L2:
      swap(Edge.UL, Edge.DL);
      break;
    case R2:
      swap(Edge.UR, Edge.DR);
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
