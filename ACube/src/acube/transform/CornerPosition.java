package acube.transform;

import acube.Corner;
import acube.Turn;
import acube.pack.PackKit;

final class CornerPosition extends Move {
  public CornerPosition(final Corner[] mask, final Turn[] turns) {
    super(PackKit.cornerPosition(mask), turns);
  }

  @Override
  public void turn(final Turn turn) {
    cycleCorners(turn);
  }
}
