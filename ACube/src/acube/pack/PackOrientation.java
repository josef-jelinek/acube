package acube.pack;

public final class PackOrientation<T> extends Pack<T> {
  private final boolean[] knownOrientedMask;
  private final int order;
  private final int orientationPartSize;
  private final int known;
  private final int unknown;
  private final int oriented;
  private final int knownOriented;
  private final int unknownOriented;

  public PackOrientation(final boolean[] usedMask, final boolean[] knownOrientedMask, final int unknownOriented,
      final T[] partIds, final int order) {
    super(Coder.unordered, usedMask, partIds);
    this.order = order;
    this.knownOrientedMask = knownOrientedMask;
    known = CoderTools.valuesUsed(this.usedMask);
    unknown = usedMask.length - known;
    knownOriented = CoderTools.valuesUsed(this.knownOrientedMask);
    this.unknownOriented = unknownOriented;
    oriented = knownOriented + unknownOriented;
    orientationPartSize = orientationPartSize(values.length, oriented, order);
  }

  private static int orientationPartSize(final int length, final int oriented, final int order) {
    int size = 1;
    for (int i = 0; i < oriented; i++)
      size *= order;
    if (oriented == length)
      size /= order;
    return size;
  }

  @Override
  public int size() {
    return Coder.unordered.size(values.length, oriented) * orientationPartSize;
  }

  @Override
  public int pack() {
    int t = 0;
    for (final int value : values)
      if (value >= 0)
        t = order * t + value;
    return coder.encode(values) * orientationPartSize + (oriented == values.length ? t / order : t);
  }

  @Override
  public void unpack(int value) {
    if (oriented == values.length) {
      int total = 0;
      for (int i = 0; i < values.length - 1; i++) {
        values[values.length - i - 2] = value % order;
        total += value % order;
        value /= order;
      }
      values[values.length - 1] = remainingOrientation(total);
    } else {
      coder.decode(values, oriented, value / orientationPartSize);
      value %= orientationPartSize;
      for (int i = values.length - 1; i >= 0; i--)
        if (values[i] >= 0) {
          values[i] = value % order;
          value /= order;
        }
    }
  }

  private int remainingOrientation(final int total) {
    return (order - total % order) % order;
  }

  @Override
  public int startSize() {
    return Coder.unordered.size(unknown, unknownOriented);
  }

  @Override
  public int start(final int startIndex) {
    final int[] unknowns = new int[unknown];
    Coder.unordered.decode(unknowns, unknownOriented, startIndex);
    int unknownIndex = 0;
    for (int i = 0; i < values.length; i++)
      values[i] = !usedMask[i] && unknowns[unknownIndex++] >= 0 || knownOrientedMask[i] ? 0 : -1;
    return pack();
  }

  public void changeOrientation(final int i1, final int i2, final int i3, final int i4) {
    if (values[i1] >= 0)
      values[i1] = updateOrientation(values[i1], 1);
    if (values[i2] >= 0)
      values[i2] = updateOrientation(values[i2], -1);
    if (values[i3] >= 0)
      values[i3] = updateOrientation(values[i3], 1);
    if (values[i4] >= 0)
      values[i4] = updateOrientation(values[i4], -1);
  }

  private int updateOrientation(final int value, final int difference) {
    return value < 0 ? -1 : (value + order + difference) % order;
  }

  @Override
  public String toString() {
    return Coder.ordered.toString(values);
  }
}
