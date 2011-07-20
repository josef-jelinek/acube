package acube.pack;

public class PackPositionPartOrdered extends Pack {
  public PackPositionPartOrdered(final int[] usedMask, final int[] careMask, final int[] partIds) {
    super(CoderPart.ordered, CoderTools.maskIntersection(usedMask, careMask), partIds);
  }
}
