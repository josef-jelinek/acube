package acube.pack;

public class PackPositionPartOrdered extends Pack {
  public PackPositionPartOrdered(final boolean[] usedMask, final int[] partIds) {
    this(usedMask, usedMask, partIds);
  }

  public PackPositionPartOrdered(final boolean[] usedMask, final boolean[] careMask, final int[] partIds) {
    super(CoderPart.ordered, CoderTools.maskIntersection(usedMask, careMask), partIds);
  }
}
