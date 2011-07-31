package acube.pack;

import java.util.Arrays;

public final class PackOrientationFull extends PackOrientation {
  private final boolean[] orientationMask;

  public PackOrientationFull(final boolean[] orientationMask, final int[] partIds, final int order) {
    super(fullmask(orientationMask.length), fullmask(orientationMask.length), partIds, order);
    this.orientationMask = orientationMask;
  }

  private static boolean[] fullmask(final int length) {
    final boolean[] mask = new boolean[length];
    Arrays.fill(mask, true);
    return mask;
  }

  public static int size(final int length, final int order) {
    return size(length, length, order);
  }

  @Override
  public int startSize() {
    int l = 1;
    for (int i = unknownOrientations() - 1; i > 0; i--)
      l *= order;
    return l;
  }

  @Override
  public int start(final int index) {
    int t = index;
    int total = 0;
    int remainingUnknownOrientations = unknownOrientations() - 1;
    for (int i = 0; i < values.length; i++)
      if (orientationMask[i])
        storeOrientation(i, 0);
      else {
        total += storeOrientation(i, remainingUnknownOrientations > 0 ? t % order : remainingOrientation(total));
        t /= order;
        remainingUnknownOrientations--;
      }
    return pack();
  }

  private int unknownOrientations() {
    int count = 0;
    for (final boolean f : orientationMask)
      if (!f)
        count++;
    return count;
  }
}
