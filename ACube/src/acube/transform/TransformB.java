package acube.transform;

public final class TransformB {
  public final ToTabMove midgePos;
  public final ToTabMove edgePos;
  public final ToTabMove cornPos;

  public static TransformB obj(int[] cornMask, int[] edgeMask) {
    return new TransformB(cornMask, edgeMask);
  }

  private TransformB(int[] cornMask, int[] edgeMask) {
    midgePos = MoveKitB.midgePos(edgeMask);
    edgePos = MoveKitB.edgePos(edgeMask);
    cornPos = new MoveTabB(MoveKit.cornPos(cornMask));
  }
}
