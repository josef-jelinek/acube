package acube.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import acube.pack.Coder;
import acube.pack.Pack;
import acube.pack.PackPositionOrdered;
import acube.pack.PackPositionUnordered;

public final class PackPositionTest {
  private static final Integer[] PART_IDS_4 = { 0, 1, 2, 3 };
  private static final Integer[] PART_IDS_5 = { 0, 1, 2, 3, 4 };
  private static final Integer[] PART_IDS_6 = { 0, 1, 2, 3, 4, 5 };

  private void checkUnpack(final String s, final Pack<Integer> p, final int state) {
    p.unpack(state);
    assertEquals(s, p.toString());
  }

  @Test
  public void full_pack() {
    final Pack<Integer> pack = new PackPositionOrdered<Integer>(getBools("0011"), PART_IDS_4);
    assertEquals(12, pack.size());
    assertEquals(1, pack.startSize());
    assertEquals(0, pack.start(0));
    checkUnpack(". . 0 1", pack, 0);
    checkUnpack("1 0 . .", pack, 11);
  }

  @Test
  public void full_cycle() {
    final Pack<Integer> pack = new PackPositionOrdered<Integer>(getBools("01111"), PART_IDS_5);
    checkUnpack(". 0 1 2 3", pack, 0);
    pack.swap(2, 4);
    assertEquals(". 0 3 2 1", pack.toString());
    pack.cycle(1, 2, 3, 4);
    assertEquals(". 3 2 1 0", pack.toString());
    pack.swap(0, 2);
    assertEquals("2 3 . 1 0", pack.toString());
  }

  @Test
  public void part_pack() {
    final PackPositionOrdered<Integer> p =
        new PackPositionOrdered<Integer>(getBools("01110"), getBools("00111"), PART_IDS_5);
    assertEquals(Coder.ordered.size(5, 2), p.size());
    checkUnpack(". . . 0 1", p, 0);
    checkUnpack(". . . 1 0", p, 1);
    checkUnpack("0 1 . . .", p, p.size() - 2);
    checkUnpack("1 0 . . .", p, p.size() - 1);
    checkPackUnpack(p);
  }

  @Test
  public void part_start() {
    final PackPositionOrdered<Integer> p =
        new PackPositionOrdered<Integer>(getBools("01110"), getBools("00111"), PART_IDS_5);
    assertEquals(1, p.startSize());
    checkUnpack(". . 0 1 .", p, p.start(0));
  }

  @Test
  public void part_loc_pack() {
    final PackPositionUnordered<Integer> p =
        new PackPositionUnordered<Integer>(getBools("01110"), getBools("00111"), PART_IDS_5);
    assertEquals(Coder.unordered.size(5, 2), p.size());
    checkUnpack(". . . # #", p, 0);
    checkUnpack(". . # . #", p, 1);
    checkUnpack("# . # . .", p, p.size() - 2);
    checkUnpack("# # . . .", p, p.size() - 1);
    checkPackUnpack(p);
  }

  @Test
  public void part_loc_start() {
    final PackPositionUnordered<Integer> p =
        new PackPositionUnordered<Integer>(getBools("01110"), getBools("00111"), PART_IDS_5);
    assertEquals(3, p.startSize());
    checkUnpack(". . . # #", p, p.start(0));
    checkUnpack(". . # . #", p, p.start(1));
    checkUnpack(". . # # .", p, p.start(2));
  }

  @Test
  public void convert() {
    final Pack<Integer> packI = new PackPositionOrdered<Integer>(getBools("111000"), PART_IDS_6);
    final Pack<Integer> packO = new PackPositionOrdered<Integer>(getBools("010101"), PART_IDS_6);
    checkUnpack(". . . 0 1 2", packI, 0);
    checkUnpack(". . . 0 1 2", packO, 0);
    packO.convert(packI);
    assertEquals(". . . 0 1 2", packO.toString());
    checkUnpack("0 1 2 . . .", packI, packI.start(0));
    checkUnpack(". 0 . 1 . 2", packO, packO.start(0));
    packO.convert(packI);
    assertEquals("0 1 2 . . .", packO.toString());
    checkUnpack("2 1 0 . . .", packI, packI.size() - 1);
    checkUnpack("2 1 0 . . .", packO, packO.size() - 1);
    packO.convert(packI);
    assertEquals("2 1 0 . . .", packO.toString());
  }

  @Test
  public void combine_full() {
    final Pack<Integer> pack1 = new PackPositionOrdered<Integer>(getBools("111000"), PART_IDS_6);
    final Pack<Integer> pack2 = new PackPositionOrdered<Integer>(getBools("000111"), PART_IDS_6);
    final Pack<Integer> packO = new PackPositionOrdered<Integer>(getBools("111111"), PART_IDS_6);
    checkUnpack("0 1 2 . . .", pack1, pack1.start(0));
    checkUnpack(". . . 0 1 2", pack2, pack2.start(0));
    assertTrue(packO.combine(pack1, pack2));
    assertEquals("0 1 2 3 4 5", packO.toString());
    assertTrue(packO.combine(pack2, pack1));
    assertEquals("0 1 2 3 4 5", packO.toString());
    checkUnpack(". . . 0 1 2", pack1, 0);
    checkUnpack("2 1 0 . . .", pack2, pack2.size() - 1);
    assertTrue(packO.combine(pack1, pack2));
    assertEquals("5 4 3 0 1 2", packO.toString());
    assertTrue(packO.combine(pack2, pack1));
    assertEquals("5 4 3 0 1 2", packO.toString());
  }

  @Test
  public void combine_partial() {
    final Pack<Integer> pack1 = new PackPositionOrdered<Integer>(getBools("100010"), getBools("101010"), PART_IDS_6);
    final Pack<Integer> pack2 = new PackPositionOrdered<Integer>(getBools("010001"), getBools("010101"), PART_IDS_6);
    final Pack<Integer> packO = new PackPositionOrdered<Integer>(getBools("110011"), getBools("111111"), PART_IDS_6);
    checkUnpack("0 . . . 1 .", pack1, pack1.start(0));
    checkUnpack(". 0 . . . 1", pack2, pack2.start(0));
    assertTrue(packO.combine(pack1, pack2));
    assertEquals("0 1 . . 2 3", packO.toString());
    assertTrue(packO.combine(pack2, pack1));
    assertEquals("0 1 . . 2 3", packO.toString());
    checkUnpack("1 0 . . . .", pack1, pack1.size() - 1);
    checkUnpack(". . . . 0 1", pack2, 0);
    assertTrue(packO.combine(pack1, pack2));
    assertEquals("2 0 . . 1 3", packO.toString());
    assertTrue(packO.combine(pack2, pack1));
    assertEquals("2 0 . . 1 3", packO.toString());
  }

  @Test
  public void convert_combine_are_comutative() {
    final Pack<Integer> pack1 = new PackPositionOrdered<Integer>(getBools("111000"), PART_IDS_6);
    final Pack<Integer> pack2 = new PackPositionOrdered<Integer>(getBools("000111"), PART_IDS_6);
    final Pack<Integer> packO = new PackPositionOrdered<Integer>(getBools("111111"), PART_IDS_6);
    check_all_combine(pack1, pack2, packO);
  }

  @Test
  public void partial_convert_combine_are_comutative() {
    final Pack<Integer> pack1 = new PackPositionOrdered<Integer>(getBools("100100"), PART_IDS_6);
    final Pack<Integer> pack2 = new PackPositionOrdered<Integer>(getBools("010001"), PART_IDS_6);
    final Pack<Integer> pack = new PackPositionOrdered<Integer>(getBools("110101"), PART_IDS_6);
    check_all_combine(pack1, pack2, pack);
  }

  private void check_all_combine(final Pack<Integer> pack1, final Pack<Integer> pack2, final Pack<Integer> pack) {
    for (int i1 = 0; i1 < pack1.size(); i1++)
      for (int i2 = 0; i2 < pack2.size(); i2++) {
        pack1.unpack(i1);
        pack2.unpack(i2);
        assertFalse(pack.combine(pack1, pack1));
        assertFalse(pack.combine(pack2, pack2));
        final boolean combine12 = pack.combine(pack1, pack2);
        final String s1 = pack.toString();
        final int cc12 = pack.pack();
        final boolean combine21 = pack.combine(pack2, pack1);
        final String s2 = pack.toString();
        final int cc21 = pack.pack();
        assertEquals(combine12, combine21);
        if (combine12)
          assertEquals(s1 + " | " + s2, cc12, cc21);
      }
  }

  private void checkPackUnpack(final Pack<Integer> p) {
    for (int i = 0; i < p.size(); i++) {
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
