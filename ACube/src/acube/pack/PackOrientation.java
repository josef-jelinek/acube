package acube.pack;

public abstract class PackOrientation extends Pack {
  protected final boolean[] orientMask;
  protected final int orientationsUsed;
  protected final int order;
  private final int orientationPartSize;

  public static PackOrientation instance(final boolean[] usedMask, final boolean[] orientMask, final int[] partIds,
      final int order) {
    final int valuesUsed = CoderTools.valuesUsed(orientMask);
    final int lengthForPartial = PackOrientation.size(orientMask.length, valuesUsed, order);
    final int lengthForFull = PackOrientationFull.size(orientMask.length, order);
    return lengthForPartial < lengthForFull ? new PackOrientationPart(usedMask, orientMask, partIds, order)
        : new PackOrientationFull(orientMask, partIds, order);
  }

  protected PackOrientation(final boolean[] usedMask, final boolean[] orientMask, final int[] partIds, final int order) {
    super(CoderPart.unordered, usedMask, partIds);
    if (usedMask.length != orientMask.length)
      throw new IllegalArgumentException("Argument sizes do not match");
    this.order = order;
    this.orientMask = orientMask;
    orientationsUsed = CoderTools.valuesUsed(this.orientMask);
    orientationPartSize = orientationPartSize(values.length, orientationsUsed, order);
  }

  private static int orientationPartSize(final int length, final int orientationsUsed, final int order) {
    int size = 1;
    for (int i = 0; i < orientationsUsed; i++)
      size *= order;
    if (orientationsUsed == length)
      size /= order;
    return size;
  }

  protected static int size(final int length, final int orientationsUsed, final int order) {
    return CoderPart.unordered.size(length, orientationsUsed) * orientationPartSize(length, orientationsUsed, order);
  }

  @Override
  public int size() {
    return CoderPart.unordered.size(values.length, orientationsUsed) * orientationPartSize;
  }

  @Override
  public int pack() {
    int t = 0;
    for (final int value : values)
      if (value != 0)
        t = order * t + value - 1;
    return coder.encode(values) * orientationPartSize + (orientationsUsed == values.length ? t / order : t);
  }

  @Override
  public void unpack(final int value) { // TODO: distribute the code to subclasses
    if (orientationsUsed == values.length) {
      int orientationPart = value;
      int total = 0;
      for (int i = 0; i < values.length; i++) {
        total +=
            i < values.length - 1 ? storeOrientation(values.length - i - 2, orientationPart % order)
                : storeOrientation(i, remainingOrientation(total));
        orientationPart /= order;
      }
    } else {
      int orientationPart = value % orientationPartSize;
      coder.decode(values, orientationsUsed, value / orientationPartSize);
      for (int i = values.length - 1; i >= 0; i--)
        if (values[i] != 0) {
          storeOrientation(i, orientationPart % order);
          orientationPart /= order;
        }
    }
  }

  protected int storeOrientation(final int index, final int value) {
    values[index] = value + 1;
    return value;
  }

  protected int remainingOrientation(final int total) {
    return (order - total % order) % order;
  }

  public void changeOrientation(final int i1, final int i2, final int i3, final int i4) {
    if (values[i1] != 0)
      values[i1] = updateOrientation(values[i1], 1);
    if (values[i2] != 0)
      values[i2] = updateOrientation(values[i2], -1);
    if (values[i3] != 0)
      values[i3] = updateOrientation(values[i3], 1);
    if (values[i4] != 0)
      values[i4] = updateOrientation(values[i4], -1);
  }

  public int updateOrientation(final int value, final int difference) {
    return value == 0 ? 0 : (value - 1 + order + difference) % order + 1;
  }
}
