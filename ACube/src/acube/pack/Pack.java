package acube.pack;

import java.util.Arrays;
import acube.Tools;

public abstract class Pack {
  protected final CoderPart coder;
  protected final int[] values;
  protected final boolean[] usedMask;
  private final int[] partIds;
  protected final int used;

  public Pack(final CoderPart coder, final boolean[] usedMask, final int[] partIds) {
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
      values[i] = !usedMask[i] ? 0 : r++;
    return pack();
  }

  public void convert(final Pack p) {
    Arrays.fill(values, 0);
    for (int i = 0; i < values.length; i++) {
      final int pi = p.findPartIndex(partIds[i]);
      if (pi >= 0)
        values[i] = p.values[pi];
    }
  }

  public boolean combine(final Pack p1, final Pack p2) {
    Arrays.fill(values, 0);
    for (int i = 0; i < values.length; i++) {
      final int p1i = p1.findPartIndex(partIds[i]);
      final int p2i = p2.findPartIndex(partIds[i]);
      final boolean is1 = p1i >= 0 && p1.values[p1i] > 0;
      final boolean is2 = p2i >= 0 && p2.values[p2i] > 0;
      if (is1 && is2)
        return false;
      if (is1)
        values[i] = p1.valueInOrder(p2, p1i);
      if (is2)
        values[i] = p2.valueInOrder(p1, p2i);
    }
    return true;
  }

  private int valueInOrder(final Pack p, final int pi) {
    int value = values[pi];
    final int to = nthUsedIndex(value - 1);
    for (int i = 0; i < to; i++)
      if (p.usedMask[i])
        value++;
    return value;
  }

  private int nthUsedIndex(final int n) {
    int count = 0;
    for (int i = 0; i < usedMask.length; i++)
      if (usedMask[i]) {
        if (count == n)
          return i;
        count++;
      }
    return -1;
  }

  private int findPartIndex(final int partId) {
    for (int i = 0; i < values.length; i++)
      if (partId == partIds[i])
        return i;
    return -1;
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
      if (!usedMask[i])
        n++;
    return n;
  }

  public final boolean areUsedIn(final boolean[] allowedMask) {
    assert allowedMask.length == values.length;
    for (int i = 0; i < values.length; i++)
      if (!allowedMask[i] && values[i] > 0)
        return false;
    return true;
  }

  @Override
  public String toString() {
    return Tools.intArrayToString(values);
  }
}
