package acube.pack;

public final class PackOrientationFull extends PackOrientation {

  private final int[] orientationMask;

  public PackOrientationFull(int[] orientationMask, int order) {
    super(fullmask(orientationMask.length), fullmask(orientationMask.length), order);
    this.orientationMask = orientationMask.clone();
  }

  private static int[] fullmask(int length) {
    int[] fullMask = new int[length];
    for (int i = 0; i < length; i++)
      fullMask[i] = 1;
    return fullMask;
  }

  public static int size(int length, int order) {
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
  public int start(int index) {
    int t = index;
    int total = 0;
    int remainingUnknownOrientations = unknownOrientations() - 1;
    for (int i = 0; i < values.length; i++) {
      if (orientationMask[i] == 0) {
        total += storeOrientation(i, remainingUnknownOrientations > 0 ? t % order : remainingOrientation(total));
        t /= order;
        remainingUnknownOrientations--;
      } else {
        storeOrientation(i, 0);
      }
    }
    return pack();
  }

  private int unknownOrientations() {
    int count = 0;
    for (int i = 0; i < values.length; i++)
      if (orientationMask[i] == 0)
        count++;
    return count;
  }
}