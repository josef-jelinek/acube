package acube.transform;

import java.util.Set;
import acube.Corner;
import acube.Turn;
import acube.pack.PackKit;

final class CornerPos extends Move {
  public CornerPos(final Set<Corner> cornerMask, final Set<Turn> turnMask) {
    super(PackKit.cornerPos(cornerMask), turnMask);
  }

  @Override
  public void turn(final Turn turn) {
    cycleCorners(turn);
  }
}
