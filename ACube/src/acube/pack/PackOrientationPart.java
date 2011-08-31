package acube.pack;


public final class PackOrientationPart<T> extends PackOrientation<T> {
  public PackOrientationPart(final boolean[] usedMask, final boolean[] orientMask, final T[] partIds, final int order) {
    super(usedMask, orientMask, partIds, order);
  }

  @Override
  public int startSize() {
    return Coder.unordered.size(unknownPositions(), unknownPositionsKnownOrientations());
  }

  @Override
  public int start(final int index) {
    final int[] a = new int[unknownPositions()];
    Coder.unordered.decode(a, unknownPositionsKnownOrientations(), index);
    int unusedFound = 0;
    for (int i = 0; i < values.length; i++)
      values[i] = (!usedMask[i] ? a[unusedFound++] >= 0 : orientMask[i]) ? 0 : -1;
    return pack();
  }

  private int unknownPositionsKnownOrientations() {
    int count = 0;
    for (int i = 0; i < values.length; i++)
      if (!usedMask[i] && orientMask[i])
        count++;
    return count;
  }
}
