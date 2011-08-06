package acube.transform;

import acube.Edge;
import acube.Turn;
import acube.pack.PackKit;

final class UEdgePosition extends MoveToB {
  public UEdgePosition(final Edge[] mask, final Turn[] turns) {
    super(PackKit.uEdgePosition(mask), turns);
  }

  @Override
  public void turn(final Turn turn) {
    cycleEdges(turn);
  }

  @Override
  public boolean isInB() {
    return areUsedIn(PackKit.oEdgeMaskInB);
  }
}