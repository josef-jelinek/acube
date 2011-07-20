package acube.transform;

import acube.Edge;
import acube.Turn;
import acube.pack.PackKit;

final class MEdgePositionB extends Move {
  public MEdgePositionB(final Edge[] mask, final Turn[] turns) {
    super(PackKit.mEdgePositionB(mask), turns);
  }

  @Override
  public void turn(final Turn turn) {
    switch (turn.toB()) {
    case U1:
      break;
    case D1:
      break;
    case F2:
      swap(Edge.FR, Edge.FL);
      break;
    case B2:
      swap(Edge.BR, Edge.BL);
      break;
    case L2:
      swap(Edge.FL, Edge.BL);
      break;
    case R2:
      swap(Edge.FR, Edge.BR);
      break;
    default:
      throw new IllegalArgumentException("Unsupported or non primitive turn " + turn);
    }
  }

  @Override
  protected int getIndex(final Edge e) {
    return PackKit.mEdgeIndexB(e);
  }
}
