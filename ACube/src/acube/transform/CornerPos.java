package acube.transform;

import static acube.Tools.cornersKey;
import java.util.EnumSet;
import acube.Corner;
import acube.Turn;
import acube.pack.PackKit;
import acube.pack.PackPositionOrdered;

final class CornerPos extends Move<Corner> {
  public CornerPos(final EnumSet<Corner> cornerMask) {
    super(new PackPositionOrdered<Corner>(PackKit.cornerMask(cornerMask), Corner.values()), "CP-" +
        cornersKey(cornerMask));
  }

  @Override
  public void turn(final Turn turn) {
    cycleCorners(turn);
  }

  public void setup(final Corner[] corners) {
    pack.setValues(Corner.ordinals(corners));
  }
}
