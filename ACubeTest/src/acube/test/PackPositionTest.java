package acube.test;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import acube.pack.CoderPart;
import acube.pack.Pack;
import acube.pack.PackPositionFull;
import acube.pack.PackPositionPartOrdered;
import acube.pack.PackPositionPartUnordered;

public final class PackPositionTest {
  private static final int[] PART_IDS_4 = { 0, 1, 2, 3 };
  private static final int[] PART_IDS_5 = { 0, 1, 2, 3, 4 };

  @Test
  public void full_pack() {
    final Pack pack = new PackPositionFull(getBools("0011"), PART_IDS_4);
    assertEquals(12, pack.size());
    assertEquals(1, pack.startSize());
    assertEquals(0, pack.start(0));
    pack.unpack(0);
    assertEquals("0 0 1 2", pack.toString());
    pack.unpack(11);
    assertEquals("2 1 0 0", pack.toString());
  }

  @Test
  public void full_cycle() {
    final Pack pack = new PackPositionFull(getBools("01111"), PART_IDS_5);
    pack.unpack(0);
    assertEquals("0 1 2 3 4", pack.toString());
    pack.swap(2, 4);
    assertEquals("0 1 4 3 2", pack.toString());
    pack.cycle(1, 2, 3, 4);
    assertEquals("0 4 3 2 1", pack.toString());
    pack.swap(0, 2);
    assertEquals("3 4 0 2 1", pack.toString());
  }

  @Test
  public void part_pack() {
    final PackPositionPartOrdered p = new PackPositionPartOrdered(getBools("01110"), getBools("00111"), PART_IDS_5);
    assertEquals(CoderPart.ordered.size(5, 2), p.size());
    p.unpack(0);
    assertEquals("0 0 0 1 2", p.toString());
    p.unpack(1);
    assertEquals("0 0 0 2 1", p.toString());
    p.unpack(p.size() - 2);
    assertEquals("1 2 0 0 0", p.toString());
    p.unpack(p.size() - 1);
    assertEquals("2 1 0 0 0", p.toString());
    checkPackUnpack(p);
  }

  @Test
  public void part_start() {
    final PackPositionPartOrdered p = new PackPositionPartOrdered(getBools("01110"), getBools("00111"), PART_IDS_5);
    assertEquals(1, p.startSize());
    p.unpack(p.start(0));
    assertEquals("0 0 1 2 0", p.toString());
  }

  @Test
  public void part_loc_pack() {
    final PackPositionPartUnordered p = new PackPositionPartUnordered(getBools("01110"), getBools("00111"), PART_IDS_5);
    assertEquals(CoderPart.unordered.size(5, 2), p.size());
    p.unpack(0);
    assertEquals("0 0 0 1 1", p.toString());
    p.unpack(1);
    assertEquals("0 0 1 0 1", p.toString());
    p.unpack(p.size() - 2);
    assertEquals("1 0 1 0 0", p.toString());
    p.unpack(p.size() - 1);
    assertEquals("1 1 0 0 0", p.toString());
    checkPackUnpack(p);
  }

  @Test
  public void part_loc_start() {
    final PackPositionPartUnordered p = new PackPositionPartUnordered(getBools("01110"), getBools("00111"), PART_IDS_5);
    assertEquals(3, p.startSize());
    p.unpack(p.start(0));
    assertEquals("0 0 0 1 1", p.toString());
    p.unpack(p.start(1));
    assertEquals("0 0 1 0 1", p.toString());
    p.unpack(p.start(2));
    assertEquals("0 0 1 1 0", p.toString());
  }

  private void checkPackUnpack(final Pack p) {
    for (int i = 0, l = p.size(); i < l; i++) {
      p.unpack(i);
      assertEquals(i, p.pack());
    }
  }

  private boolean[] getBools(final String s) {
    final boolean[] b = new boolean[s.length()];
    for (int i = 0; i < s.length(); i++)
      b[i] = s.charAt(i) == '1';
    return b;
  }
}
