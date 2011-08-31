package acube.pack;

public class PackPositionOrdered<T> extends Pack<T> {
  public PackPositionOrdered(final boolean[] usedMask, final T[] partIds) {
    this(usedMask, usedMask, partIds);
  }

  public PackPositionOrdered(final boolean[] usedMask, final boolean[] careMask, final T[] partIds) {
    super(Coder.ordered, CoderTools.maskIntersection(usedMask, careMask), partIds);
  }

  @Override
  public String toString() {
    return coder.toString(values);
  }
}
