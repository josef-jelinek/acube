package acube.pack;

import java.util.Arrays;

public final class PackOrientationFull<T> extends PackOrientation<T> {
  private final boolean[] orientationMask;

  public PackOrientationFull(final boolean[] orientationMask, final T[] partIds, final int order) {
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
  public void setValues(final int[] values) {
    Arrays.fill(this.values, 0);
    for (int i = 0; i < values.length; i++)
      this.values[i] = Math.max(0, values[i]);
  }

  @Override
  public int startSize() {
    int l = 1;
    for (int i = unknownOrientations() - 1; i > 0; i--)
      l *= order;
    return l;
  }

  @Override
  public int start(int index) {
    int total = 0;
    int unknownOrientationsToDo = unknownOrientations() - 1;
    for (int i = 0; i < values.length; i++)
      if (orientationMask[i])
        values[i] = 0;
      else {
        final int o = unknownOrientationsToDo > 0 ? index % order : remainingOrientation(total);
        values[i] = o;
        total += o;
        index /= order;
        unknownOrientationsToDo--;
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
