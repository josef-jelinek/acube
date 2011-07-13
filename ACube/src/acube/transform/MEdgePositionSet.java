package acube.transform;

import acube.Edge;
import acube.Turn;
import acube.pack.PackKit;

final class MEdgePositionSet extends Move {
  public MEdgePositionSet(final Edge[] mask, final Turn[] turns) {
    super(PackKit.mEdgePositionSet(mask), turns);
  }

  @Override
  public void turn(final Turn turn) {
    cycleEdges(turn);
  }
}
