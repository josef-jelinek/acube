package acube.transform;

import acube.Corner;
import acube.Turn;
import acube.pack.PackKit;
import acube.pack.PackOrientation;

final class CornerTwist extends Move {
  private final PackOrientation packOrientation;

  private CornerTwist(final PackOrientation packOrientation, final Turn[] turns) {
    super(packOrientation, turns);
    this.packOrientation = packOrientation;
  }

  public CornerTwist(final Corner[] mask, final Corner[] twistMask, final Turn[] turns) {
    this(PackKit.cornerTwist(mask, twistMask), turns);
  }

  @Override
  public void turn(final Turn turn) {
    cycleCorners(turn);
    switch (turn) {
    case F1:
      twist(Corner.DFL, Corner.DRF, Corner.UFR, Corner.ULF);
      break;
    case B1:
      twist(Corner.DBR, Corner.DLB, Corner.UBL, Corner.URB);
      break;
    case L1:
      twist(Corner.DLB, Corner.DFL, Corner.ULF, Corner.UBL);
      break;
    case R1:
      twist(Corner.DRF, Corner.DBR, Corner.URB, Corner.UFR);
      break;
    default:
    }
  }

  protected void twist(final Corner c1, final Corner c2, final Corner c3, final Corner c4) {
    packOrientation.changeOrientation(getIndex(c1), getIndex(c2), getIndex(c3), getIndex(c4));
  }
}
