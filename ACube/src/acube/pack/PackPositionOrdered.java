package acube.pack;

public class PackPositionOrdered<T> extends Pack<T> {
  public PackPositionOrdered(final boolean[] usedMask, final T[] partIds) {
    this(usedMask, usedMask, partIds);
  }

  public PackPositionOrdered(final boolean[] usedMask, final boolean[] careMask, final T[] partIds) {
    super(Coder.ordered, CoderTools.maskIntersection(usedMask, careMask), partIds);
  }

  @Override
  public int startSize() {
    return 1;
  }

  @Override
  public int start(final int startIndex) {
    int r = 1;
    for (int i = 0; i < values.length; i++)
      values[i] = !usedMask[i] ? -1 : r++;
    return pack();
  }

  @Override
  public String toString() {
    return coder.toString(values);
  }
}
