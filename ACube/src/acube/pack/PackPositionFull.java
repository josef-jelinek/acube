package acube.pack;

public final class PackPositionFull extends Pack {
  public PackPositionFull(final boolean[] usedMask, final int[] partIds) {
    super(CoderPart.ordered, usedMask, partIds);
  }
}
