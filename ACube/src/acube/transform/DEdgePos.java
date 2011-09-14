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

final class DEdgePos extends MoveToB<Edge> {
  private static final boolean[] UD_EDGES = PackKit.getEdgeMaskFor(EnumSet.of(UF, UR, UB, UL, DF, DR, DB, DL));
  private static final EnumSet<Edge> D_EDGES = EnumSet.of(DF, DR, DB, DL);

  public DEdgePos(final EnumSet<Edge> edgeMask) {
    super(new PackPositionOrdered<Edge>(PackKit.edgeMask(edgeMask), PackKit.edgeMask(D_EDGES), Edge.values()));
  }

  @Override
  public void turn(final Turn turn) {
    cycleEdges(turn);
  }

  @Override
  public boolean isInB() {
    return areUsedIn(UD_EDGES);
  }

  public void setup(final Edge[] edges) {
    pack.setValues(PackKit.fillIndices(edges, D_EDGES.toArray(new Edge[0])));
  }
}
