package acube.pack;

class PackPos extends Pack {
  final int[] care;

  PackPos(CombPart comb, int[] mask, int[] caremask) {
    super(comb, careMask(mask, caremask));
    care = caremask;
  }

  PackPos(int[] mask, int[] caremask) {
    this(CombPart.var, mask, caremask);
  }

  private static int[] careMask(int[] mask, int[] care) {
    if (mask.length != care.length) throw new IllegalArgumentException("Agument sizes do not match");
    int[] caremask = new int[care.length];
    for (int i = 0; i < care.length; i++)
      caremask[i] = care[i] != 0 && mask[i] != 0 ? 1 : 0;
    return caremask;
  }
}
