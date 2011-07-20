package acube.pack;

public final class PackOrientationPart extends PackOrientation {
  public PackOrientationPart(final int[] usedMask, final int[] orientMask, final int[] partIds, final int order) {
    super(usedMask, orientMask, partIds, order);
  }

  @Override
  public int startSize() {
    return CoderPart.unordered.size(unknownPositions(), unknownPositionsKnownOrientations());
  }

  @Override
  public int start(final int index) {
    final int[] a = new int[unknownPositions()];
    CoderPart.unordered.decode(a, unknownPositionsKnownOrientations(), index);
    int unusedFound = 0;
    for (int i = 0; i < values.length; i++)
      values[i] = usedMask[i] == 0 ? a[unusedFound++] : orientMask[i] == 0 ? 0 : 1;
    return pack();
  }

  private int unknownPositionsKnownOrientations() {
    int count = 0;
    for (int i = 0; i < values.length; i++)
      if (usedMask[i] == 0 && orientMask[i] != 0)
        count++;
    return count;
  }
}
