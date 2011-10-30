package acube.transform;

import static acube.Edge.BL;
import static acube.Edge.BR;
import static acube.Edge.DB;
import static acube.Edge.DF;
import static acube.Edge.FL;
import static acube.Edge.FR;
import static acube.Edge.UB;
import static acube.Edge.UF;
import static acube.Tools.edgesKey;
import java.util.EnumSet;
import acube.Edge;
import acube.Turn;
import acube.pack.PackKit;
import acube.pack.PackOrientation;

public final class Flip extends Move<Edge> {
  private final PackOrientation<Edge> packOrientation;

  private Flip(final PackOrientation<Edge> packOrientation, final String key) {
    super(packOrientation, key);
    this.packOrientation = packOrientation;
  }

  public Flip(final EnumSet<Edge> edgeMask, final EnumSet<Edge> knownFlipMask, final int unknownFlipped) {
    this(new PackOrientation<Edge>(PackKit.edgeMask(edgeMask), PackKit.edgeMask(knownFlipMask), unknownFlipped,
        Edge.values(), 2), "EF-" + edgesKey(edgeMask) + "-" + edgesKey(knownFlipMask) + "-" + unknownFlipped);
  }

  @Override
  public void turn(final Turn turn) {
    cycleEdges(turn);
    switch (turn) {
    case F1:
      flip(FL, DF, FR, UF);
      break;
    case B1:
      flip(BR, DB, BL, UB);
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
