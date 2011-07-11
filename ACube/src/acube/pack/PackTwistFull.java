package acube.pack;

final class PackTwistFull extends PackTwist {
  private final int[] omask;

  PackTwistFull(int[] orimask, int twists) {
    super(fullmask(orimask.length), fullmask(orimask.length), twists);
    omask = (int[])orimask.clone();
  }

  private static int[] fullmask(int num) {
    int[] fullmask = new int[num];
    for (int i = 0; i < num; i++) fullmask[i] = 1;
    return fullmask;
  }

  public static int len(int num, int twists) {
    return len(num, num, twists);
  }

  public int startLen() {
    int l = 1;
    for (int i = unknownOri() - 1; i > 0; i--) l *= twists;
    return l;
  }

  public int start(int x) {
    for (int i = 0, t = x, tt = 0, n = unknownOri() - 1; i < num; t /= omask[i++] == 1 ? 1 : twists)
      tt += setTwist(i, omask[i] == 1 ? 0 : (n-- > 0 ? t % twists : lastTwist(tt)));
    return pack();
  }

  private int unknownOri() {
    int n = 0;
    for (int i = 0; i < num; i++)
      if (omask[i] == 0) n++;
    return n;
  }
}
