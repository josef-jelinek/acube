package acube.pack;

public class PackPositionOrdered extends Pack {
  public PackPositionOrdered(final boolean[] usedMask, final int[] partIds) {
    this(usedMask, usedMask, partIds);
  }

  public PackPositionOrdered(final boolean[] usedMask, final boolean[] careMask, final int[] partIds) {
    super(Coder.ordered, CoderTools.maskIntersection(usedMask, careMask), partIds);
  }

  @Override
  public String toString() {
    return CoderOrdered.toString(values);
  }
}
