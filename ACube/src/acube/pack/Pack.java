package acube.pack;

import acube.Tools;

public abstract class Pack {
  protected final CoderPart coder;
  protected final int[] values;
  protected final int[] usedMask;
  private int[] partIds = null;
  protected final int used;

  public Pack(final CoderPart coder, final int[] usedMask) {
    this.coder = coder;
    this.usedMask = usedMask.clone();
    values = new int[usedMask.length];
    used = CoderTools.valuesUsed(usedMask);
  }

  public int size() {
    return coder.size(values.length, used);
  }

  public int pack() {
    return coder.encode(values);
  }

  public void unpack(final int x) {
    coder.decode(values, used, x);
  }

  public int startSize() {
    return 1;
  }

  public int start(final int x) {
    int r = 1;
    for (int i = 0; i < values.length; i++)
      values[i] = usedMask[i] == 0 ? 0 : r++;
    return pack();
  }

  void parts(final int[] partIds) {
    if (this.partIds != null)
      throw new IllegalStateException("Double initialization");
    if (partIds.length != values.length)
      throw new IllegalArgumentException(
          "Argument size does not match the size of the packed array");
    this.partIds = partIds.clone();
  }

  public void convert(final Pack from) {
    if (partIds == null)
      throw new IllegalArgumentException("Object part ids not initialized");
    if (from.partIds == null)
      throw new IllegalArgumentException("Source part ids not initialized");
    for (int i = 0; i < values.length; i++)
      values[i] = -1;
    for (int i = 0; i < from.values.length; i++) {
      boolean found = false;
      for (int j = 0; j < values.length; j++)
        if (partIds[j] == from.partIds[i]) {
          if (values[j] >= 0)
            throw new IllegalStateException("Id " + partIds[j] + " not unique");
          values[j] = from.values[i];
          found = true;
        }
      if (!found && from.values[i] > 0)
        throw new IllegalStateException("Unused id " + from.partIds[i] + " contains non zero item");
    }
    for (int i = 0; i < values.length; i++)
      if (values[i] < 0)
        throw new IllegalStateException("Id " + partIds[i] + " not used");
  }

  public void combine(final Pack from) {
    int max = 0;
    main: for (int i = 0; i < values.length; i++) {
      for (int j = 0; j < from.values.length; j++)
        if (partIds[i] == from.partIds[j]) {
          if (from.values[j] > 0) {
            if (values[i] > 0)
              throw new IllegalStateException("Combine collision");
            max = Math.max(max, from.values[j]);
            values[i] = from.values[j];
          }
          continue main;
        }
      throw new IllegalStateException("Incomplete conversion for id " + partIds[i]);
    }
    for (int i = 0; i < values.length; i++)
      for (int j = 0; j < from.values.length; j++)
        if (partIds[i] == from.partIds[j] && values[i] > 0 && from.values[j] == 0)
          values[i] += max;
  }

  public void swap(final int i1, final int i2) {
    final int t = values[i1];
    values[i1] = values[i2];
    values[i2] = t;
  }

  public void cycle(final int i1, final int i2, final int i3) {
    final int t = values[i1];
    values[i1] = values[i2];
    values[i2] = values[i3];
    values[i3] = t;
  }

  public void cycle(final int i1, final int i2, final int i3, final int i4) {
    final int t = values[i1];
    values[i1] = values[i2];
    values[i2] = values[i3];
    values[i3] = values[i4];
    values[i4] = t;
  }

  protected final int unknownPositions() {
    int n = 0;
    for (int i = 0; i < values.length; i++)
      if (usedMask[i] == 0)
        n++;
    return n;
  }

  @Override
  public String toString() {
    return Tools.intArrayToString(values);
  }
}
