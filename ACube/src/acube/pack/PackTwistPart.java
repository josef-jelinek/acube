package acube.pack;

final class PackTwistPart extends PackTwist {
  PackTwistPart(int[] usedmask, int[] orimask, int twists) {
    super(usedmask, orimask, twists);
  }

  public int startLen() {
    return CombPart.comb.len(unknownPos(), unknownPosKnownOri());
  }

  public int start(int x) {
    int[] a = new int[unknownPos()];
    CombPart.comb.unpack(a, unknownPosKnownOri(), x);
    for (int i = 0, j = 0; i < num; i++)
      arr[i] = mask[i] == 0 ? a[j++] : (omask[i] == 0 ? 0 : 1);
    return pack();
  }

  private int unknownPosKnownOri() {
    int n = 0;
    for (int i = 0; i < num; i++)
      if (mask[i] == 0 && omask[i] != 0) n++;
    return n;
  }
}
