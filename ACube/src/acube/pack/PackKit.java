package acube.pack;

import acube.Cubie;

public final class PackKit {
  private static final int[] midgeMask = {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1};
  private static final int[] udgeMask = {1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0};
  private static final int[] dedgeMask = {0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0};

  private static final int[] corns = {
    Cubie.UFR, Cubie.URB, Cubie.UBL, Cubie.ULF, 
    Cubie.DRF, Cubie.DFL, Cubie.DLB, Cubie.DBR,
  };

  private static final int[] edges = {
    Cubie.UF, Cubie.UR, Cubie.UB, Cubie.UL,
    Cubie.DF, Cubie.DR, Cubie.DB, Cubie.DL,
    Cubie.FR, Cubie.FL, Cubie.BR, Cubie.BL,
  };

  private static final int[] midgesB = {
    Cubie.FR, Cubie.FL, Cubie.BR, Cubie.BL,
  };

  private static final int[] edgesB = {
    Cubie.UF, Cubie.UR, Cubie.UB, Cubie.UL,
    Cubie.DF, Cubie.DR, Cubie.DB, Cubie.DL,
  };

  public static Pack cornPos(int[] mask) {
    return setupPack(new PackPerm(mask), corns);
  }

  public static PackTwist cornTwist(int[] mask, int[] oriMask) {
    return setupPackTwist(PackTwist.obj(mask, oriMask, 3), corns);
  }

  public static PackTwist edgeFlip(int[] mask, int[] oriMask) {
    return setupPackTwist(PackTwist.obj(mask, oriMask, 2), edges);
  }

  public static Pack midgeLoc(int[] mask) {
    return setupPack(new PackLoc(mask, midgeMask), edges);
  }

  public static Pack midgePos(int[] mask) {
    return setupPack(new PackPos(mask, midgeMask), edges);
  }

  public static Pack udgePos(int[] mask) {
    return setupPack(new PackPos(mask, udgeMask), edges);
  }

  public static Pack dedgePos(int[] mask) {
    return setupPack(new PackPos(mask, dedgeMask), edges);
  }

  public static Pack midgePosB(int[] mask) {
    return setupPack(new PackPerm(mask), midgesB);
  }

  public static Pack edgePosB(int[] mask) {
    return setupPack(new PackPerm(mask), edgesB);
  }

  private static Pack setupPack(Pack pack, int[] parts) {
    pack.parts(parts);
    return pack;
  }

  private static PackTwist setupPackTwist(PackTwist pack, int[] parts) {
    pack.parts(parts);
    return pack;
  }

  private PackKit() {}
}
