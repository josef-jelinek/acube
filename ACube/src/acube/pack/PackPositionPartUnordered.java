package acube.pack;

public final class PackPositionPartUnordered extends Pack {

  private final int[] careMask;

  public PackPositionPartUnordered(int[] mask, int[] careMask) {
    super(CoderPart.unordered, CoderTools.maskIntersection(mask, careMask));
    this.careMask = careMask.clone();
  }

  @Override
  public int startSize() {
    return coder.size(care(), careNotPos());
  }

  @Override
  public int start(int x) {
    int[] a = new int[care()];
    coder.decode(a, careNotPos(), x);
    for (int i = 0, j = 0; i < values.length; i++)
      values[i] = careMask[i] == 1 ? a[j++] : 0;
    return pack();
  }

  private int care() {
    int n = 0;
    for (int i = 0; i < values.length; i++)
      if (careMask[i] == 1)
        n++;
    return n;
  }

  private int careNotPos() {
    int n = 0;
    for (int i = 0; i < values.length; i++)
      if (careMask[i] == 1 && usedMask[i] == 0)
        n++;
    return n;
  }
}