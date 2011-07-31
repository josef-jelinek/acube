package acube.transform;

import static acube.Corner.DBR;
import static acube.Corner.DFL;
import static acube.Corner.DLB;
import static acube.Corner.DRF;
import static acube.Corner.UBL;
import static acube.Corner.UFR;
import static acube.Corner.ULF;
import static acube.Corner.URB;
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
      twist(DFL, DRF, UFR, ULF);
      break;
    case B1:
      twist(DBR, DLB, UBL, URB);
      break;
    case L1:
      twist(DLB, DFL, ULF, UBL);
      break;
    case R1:
      twist(DRF, DBR, URB, UFR);
      break;
    }
  }

  protected void twist(final Corner c1, final Corner c2, final Corner c3, final Corner c4) {
    packOrientation.changeOrientation(getIndex(c1), getIndex(c2), getIndex(c3), getIndex(c4));
  }
}
