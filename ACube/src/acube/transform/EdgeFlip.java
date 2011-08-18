package acube.transform;

import java.util.Set;
import acube.Edge;
import acube.Turn;
import acube.pack.PackKit;
import acube.pack.PackOrientation;

final class EdgeFlip extends Move {
  private final PackOrientation packOrientation;

  private EdgeFlip(final PackOrientation packOrientation, final Set<Turn> turnMask) {
    super(packOrientation, turnMask);
    this.packOrientation = packOrientation;
  }

  public EdgeFlip(final Set<Edge> edgeMask, final Set<Edge> edgeFlipMask, final Set<Turn> turnMask) {
    this(PackKit.edgeFlip(edgeMask, edgeFlipMask), turnMask);
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

  protected void flip(final Edge e1, final Edge e2, final Edge e3, final Edge e4) {
    packOrientation.changeOrientation(e1.ordinal(), e2.ordinal(), e3.ordinal(), e4.ordinal());
  }
}
