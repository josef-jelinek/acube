package acube.pack;

import acube.Tools;

public abstract class Pack {
  protected final CoderPart coder;
  protected final int[] values;
  protected final int[] usedMask;
  private final int[] partIds;
  protected final int used;

  public Pack(final CoderPart coder, final int[] usedMask, final int[] partIds) {
    assert partIds.length == usedMask.length;
    this.coder = coder;
    this.usedMask = usedMask;
    this.partIds = partIds;
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

  public int start(@SuppressWarnings("unused") final int _) {
    int r = 1;
    for (int i = 0; i < values.length; i++)
      values[i] = usedMask[i] == 0 ? 0 : r++;
    return pack();
  }

  public void convert(final Pack from) {
    assert partIds != null && from.partIds != null;
    for (int i = 0; i < values.length; i++)
      values[i] = -1;
    for (int i = 0; i < from.values.length; i++) {
      boolean found = false;
      for (int j = 0; j < values.length; j++)
        if (partIds[j] == from.partIds[i]) {
          assert values[j] < 0;
          values[j] = from.values[i];
          found = true;
        }
      assert found || from.values[i] <= 0;
    }
    for (final int value : values)
      assert value >= 0;
  }

  public void combine(final Pack from) {
    int max = 0;
    for (int i = 0; i < values.length; i++)
      for (int j = 0; j < from.values.length; j++)
        if (partIds[i] == from.partIds[j] && from.values[j] > 0) {
          assert values[i] <= 0;
          values[i] = from.values[j];
          max = Math.max(max, values[i]);
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
