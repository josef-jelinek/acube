package acube.transform;

import acube.Corner;
import acube.Turn;
import acube.pack.PackKit;

final class CornerTwist extends OrientationMove {
  public CornerTwist(final Corner[] mask, final Corner[] twistMask, final Turn[] turns) {
    super(PackKit.cornerTwist(mask, twistMask), turns);
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
}
