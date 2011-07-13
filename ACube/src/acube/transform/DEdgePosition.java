package acube.transform;

import acube.Edge;
import acube.Turn;
import acube.pack.PackKit;

final class DEdgePosition extends Move {
  public DEdgePosition(final Edge[] mask, final Turn[] turns) {
    super(PackKit.dEdgePosition(mask), turns);
  }

  @Override
  public void turn(final Turn turn) {
    cycleEdges(turn);
  }
}
