package acube.transform;

import acube.Edge;
import acube.Turn;
import acube.pack.PackKit;

final class EdgeFlip extends OrientationMove {
  public EdgeFlip(final Edge[] mask, final Edge[] flipMask, final Turn[] turns) {
    super(PackKit.edgeFlip(mask, flipMask), turns);
  }

  @Override
  public void turn(final Turn turn) {
    cycleEdges(turn);
    switch (turn) {
    case F1:
      flip(Edge.FL, Edge.DF, Edge.FR, Edge.UF);
      break;
    case B1:
      flip(Edge.BR, Edge.DB, Edge.BL, Edge.UB);
      break;
    default:
    }
  }
}
