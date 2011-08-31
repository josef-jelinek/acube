package acube.transform;

import java.util.EnumSet;
import acube.Corner;
import acube.Turn;
import acube.pack.PackKit;

final class CornerPos extends Move<Corner> {
  public CornerPos(final EnumSet<Corner> cornerMask) {
    super(PackKit.cornerPos(cornerMask));
  }

  @Override
  public void turn(final Turn turn) {
    cycleCorners(turn);
  }

  public void setup(final Corner[] corners) {
    pack.setValues(Corner.ordinals(corners));
  }
}
