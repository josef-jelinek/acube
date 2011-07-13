package acube.pack;

public final class PackOrientationPart extends PackOrientation {

  public PackOrientationPart(int[] usedMask, int[] orientMask, int order) {
    super(usedMask, orientMask, order);
  }

  @Override
  public int startSize() {
    return CoderPart.unordered.size(unknownPositions(), unknownPositionsKnownOrientations());
  }

  @Override
  public int start(int index) {
    int[] a = new int[unknownPositions()];
    CoderPart.unordered.decode(a, unknownPositionsKnownOrientations(), index);
    int unusedFound = 0;
    for (int i = 0; i < values.length; i++)
      values[i] = usedMask[i] == 0
        ? a[unusedFound++]
        : (orientMask[i] == 0 ? 0 : 1);
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
