package acube.transform;

import java.util.EnumSet;
import acube.Edge;
import acube.Turn;
import acube.pack.PackKit;
import acube.pack.PackOrientation;

final class Flip extends Move<Edge> {
  private final PackOrientation<Edge> packOrientation;

  private Flip(final PackOrientation<Edge> packOrientation) {
    super(packOrientation);
    this.packOrientation = packOrientation;
  }

  public Flip(final EnumSet<Edge> edgeMask, final EnumSet<Edge> edgeFlipMask) {
    this(PackKit.edgeFlip(edgeMask, edgeFlipMask));
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

  public void setup(final int[] flips) {
    packOrientation.setValues(flips);
  }
}
