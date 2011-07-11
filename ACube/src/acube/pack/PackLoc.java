package acube.pack;

class PackLoc extends PackPos {
  PackLoc(int[] mask, int[] caremask) {
    super(CombPart.comb, mask, caremask);
  }

  public int startLen() {
    return comb.len(care(), careNotPos());
  }

  public int start(int x) {
    int[] a = new int[care()];
    comb.unpack(a, careNotPos(), x);
    for (int i = 0, j = 0; i < num; i++)
      arr[i] = care[i] == 1 ? a[j++] : 0;
    return pack();
  }

  int care() {
    int n = 0;
    for (int i = 0; i < num; i++)
      if (care[i] == 1) n++;
    return n;
  }

  int careNotPos() {
    int n = 0;
    for (int i = 0; i < num; i++)
      if (care[i] == 1 && mask[i] == 0) n++;
    return n;
  }
}
