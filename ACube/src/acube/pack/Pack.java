package acube.pack;

public abstract class Pack {
  final CombPart comb;
  final int[] arr;
  final int[] mask;
  private int[] id = null;
  final int num;
  final int used;

  Pack(CombPart comb, int[] usedmask) {
    this.comb = comb;
    num = usedmask.length;
    arr = new int[num];
    mask = (int[])usedmask.clone();
    used = CombPart.used(mask);
  }

  public int len() {
    return comb.len(num, used);
  }

  public int pack() {
    return comb.pack(arr);
  }

  public void unpack(int x) {
    comb.unpack(arr, used, x);
  }

  public int startLen() {
    return 1;
  }

  public int start(int x) {
    int r = 1;
    for (int i = 0; i < num; i++)
      arr[i] = mask[i] == 0 ? 0 : r++;
    return pack();
  }

  void parts(int[] id) {
    if (this.id != null) throw new IllegalStateException("Double initialization");
    if (id.length != num) throw new IllegalArgumentException("Argument size does not match the size of the packed array");
    this.id = (int[])id.clone();
  }

  public void convert(Pack from) {
    if (id == null) throw new IllegalArgumentException("Object part ids not initialized");
    if (from.id == null) throw new IllegalArgumentException("Source part ids not initialized");
    for (int i = 0; i < num; i++)
      arr[i] = -1;
    for (int i = 0; i < from.num; i++) {
      boolean found = false;
      for (int j = 0; j < num; j++) {
        if (id[j] == from.id[i]) {
          if (arr[j] >= 0) throw new IllegalStateException("Id " + id[j] + " not unique");
          arr[j] = from.arr[i];
          found = true;
        }
      }
      if (!found && from.arr[i] > 0) throw new IllegalStateException("Unused id " + from.id[i] + " contains non zero item");
    }
    for (int i = 0; i < num; i++)
      if (arr[i] < 0) throw new IllegalStateException("Id " + id[i] + " not used");
  }

  public void combine(Pack from) {
    int max = 0;
   main:
    for (int i = 0; i < num; i++) {
      for (int j = 0; j < from.num; j++) {
        if (id[i] == from.id[j]) {
          if (from.arr[j] > 0) {
            if (arr[i] > 0) throw new IllegalStateException("Combine collision");
            max = Math.max(max, from.arr[j]);
            arr[i] = from.arr[j];
          }
          continue main;
        }
      }
      throw new IllegalStateException("Incomplete conversion for id " + id[i]);
    }
    for (int i = 0; i < num; i++)
      for (int j = 0; j < from.num; j++)
        if (id[i] == from.id[j] && arr[i] > 0 && from.arr[j] == 0)
          arr[i] += max;
  }

  public void swap(int i1, int i2) {
    int t = arr[i1];
    arr[i1] = arr[i2];
    arr[i2] = t;
  }

  public void cycle(int i1, int i2, int i3, int i4) {
    int t = arr[i1];
    arr[i1] = arr[i2];
    arr[i2] = arr[i3];
    arr[i3] = arr[i4];
    arr[i4] = t;
  }

  protected int unknownPos() {
    int n = 0;
    for (int i = 0; i < num; i++)
      if (mask[i] == 0) n++;
    return n;
  }

  public String toString() {
    if (arr.length == 0) return "";
    String s = "" + arr[0];
    for (int i = 1; i < arr.length; i++)
      s += " " + arr[i];
    return s;
  }
}
