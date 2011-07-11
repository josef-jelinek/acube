package acube.pack;

public abstract class PackTwist extends Pack {
  final int[] omask;
  final int oused;
  final int twists;
  private final int tlen;

  static PackTwist obj(int[] usedmask, int[] orimask, int twists) {
    int oused = CombPart.used(orimask);
    int plen = PackTwistPart.len(orimask.length, oused, twists);
    int flen = PackTwistFull.len(orimask.length, twists);
    return plen < flen ? (PackTwist)(new PackTwistPart(usedmask, orimask, twists)) : (PackTwist)(new PackTwistFull(orimask, twists));
  }

  PackTwist(int[] usedmask, int[] orimask, int twists) {
    super(CombPart.comb, usedmask);
    if (usedmask.length != orimask.length) throw new IllegalArgumentException("Argument sizes do not match");
    this.twists = twists;
    omask = (int[])orimask.clone();
    oused = CombPart.used(omask);
    tlen = usedLen(num, oused, twists);
  }

  private static int usedLen(int num, int oused, int twists) {
    int len = 1;
    for (int i = 0; i < oused; i++) len *= twists;
    if (oused == num) len /= twists;
    return len;
  }

  static int len(int num, int oused, int twists) {
    return CombPart.comb.len(num, oused) * usedLen(num, oused, twists);
  }

  public int len() {
    return CombPart.comb.len(num, oused) * tlen;
  }

  public int pack() {
    int t = 0;
    for (int i = 0; i < num; i++)
      if (arr[i] != 0) t = twists * t + arr[i] - 1;
    return comb.pack(arr) * tlen + (oused == num ? t / twists : t);
  }

  public void unpack(int x) {
    if (oused == num) {
      for (int i = 0, t = x, tt = 0; i < num; i++, t /= twists)
        tt += i < num - 1 ? setTwist(num - i - 2, t % twists) : setTwist(i, lastTwist(tt));
    } else {
      comb.unpack(arr, oused, x / tlen);
      for (int i = num - 1, t = x % tlen; i >= 0; t /= arr[i--] == 0 ? 1 : twists)
        if (arr[i] != 0) setTwist(i, t % twists);
    }
  }

  int setTwist(int i, int twist) {
    arr[i] = twist + 1;
    return twist;
  }

  int lastTwist(int total) {
    return (twists - total % twists) % twists;
  }

  public void twist(int i1, int i2, int i3, int i4) {
    if (arr[i1] != 0) arr[i1] = twist(arr[i1], 1);
    if (arr[i2] != 0) arr[i2] = twist(arr[i2], -1);
    if (arr[i3] != 0) arr[i3] = twist(arr[i3], 1);
    if (arr[i4] != 0) arr[i4] = twist(arr[i4], -1);
  }

  public int twist(int i, int d) {
    return i == 0 ? 0 : (i - 1 + twists + d) % twists + 1;
  }
}
