package acube.transform;

public final class Transform {
  public final ToTabMove cornTwist;
  public final ToTabMove edgeFlip;
  public final ToTabMove midgeLoc;
  public final ToTabMove midgePos;
  public final ToTabMove udgePos;
  public final ToTabMove dedgePos;
  public final ToTabMove cornPos;

  public static Transform obj(int[] cornMask, int[] edgeMask, int[] cornOriMask, int[] edgeOriMask) {
    return new Transform(cornMask, edgeMask, cornOriMask, edgeOriMask);
  }

  private Transform(int[] cornMask, int[] edgeMask, int[] cornOriMask, int[] edgeOriMask) {
    cornTwist = MoveKit.cornTwist(cornMask, cornOriMask);
    edgeFlip = MoveKit.edgeFlip(edgeMask, edgeOriMask);
    midgeLoc = MoveKit.midgeLoc(edgeMask);
    midgePos = MoveKit.midgePos(edgeMask);
    udgePos = MoveKit.udgePos(edgeMask);
    dedgePos = MoveKit.dedgePos(edgeMask);
    cornPos = MoveKit.cornPos(cornMask);
  }
}
