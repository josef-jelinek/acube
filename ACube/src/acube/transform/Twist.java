package acube.transform;

import static acube.Corner.DBR;
import static acube.Corner.DFL;
import static acube.Corner.DLB;
import static acube.Corner.DRF;
import static acube.Corner.UBL;
import static acube.Corner.UFR;
import static acube.Corner.ULF;
import static acube.Corner.URB;
import java.util.EnumSet;
import acube.Corner;
import acube.Turn;
import acube.pack.PackKit;
import acube.pack.PackOrientation;

public final class Twist extends Move<Corner> {
  private final PackOrientation<Corner> packOrientation;

  private Twist(final PackOrientation<Corner> packOrientation) {
    super(packOrientation);
    this.packOrientation = packOrientation;
  }

  public Twist(final EnumSet<Corner> cornerMask, final EnumSet<Corner> knownTwistMask, final int unknownTwisted) {
    this(new PackOrientation<Corner>(PackKit.cornerMask(cornerMask), PackKit.cornerMask(knownTwistMask),
        unknownTwisted, Corner.values(), 3));
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

  public void setup(final int[] twists) {
    packOrientation.setValues(twists);
  }
}
