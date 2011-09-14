package acube.test;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import acube.pack.Coder;
import acube.pack.Pack;
import acube.pack.PackOrientation;

public final class PackOrientationTest {
  private static final Integer[] PART_IDS_8 = { 0, 1, 2, 3, 4, 5, 6, 7 };
  private static final Integer[] PART_IDS_12 = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };

  private boolean[] getBools(final String s) {
    final boolean[] b = new boolean[s.length()];
    for (int i = 0; i < s.length(); i++)
      b[i] = s.charAt(i) == '1';
    return b;
  }

  private void checkPackUnpack(final Pack<Integer> p) {
    for (int i = 0, l = p.size(); i < l; i++) {
      p.unpack(i);
      assertEquals(i, p.pack());
    }
  }

  @Test
  public void pack_unpack_3() {
    final PackOrientation<Integer> p =
        new PackOrientation<Integer>(getBools("00110011"), getBools("00000011"), 2, PART_IDS_8, 3);
    assertEquals(3 * 3 * 3 * 3 * Coder.unordered.size(8, 4), p.size());
    p.unpack(0);
    assertEquals(". . . . 0 0 0 0", p.toString());
    p.unpack(1);
    assertEquals(". . . . 0 0 0 1", p.toString());
    p.unpack(p.size() - 2);
    assertEquals("2 2 2 1 . . . .", p.toString());
    p.unpack(p.size() - 1);
    assertEquals("2 2 2 2 . . . .", p.toString());
    checkPackUnpack(p);
  }

  @Test
  public void start_3() {
    final PackOrientation<Integer> p =
        new PackOrientation<Integer>(getBools("00110011"), getBools("00000011"), 2, PART_IDS_8, 3);
    assertEquals(Coder.unordered.size(4, 2), p.startSize());
    p.unpack(p.start(0));
    assertEquals(". . . . 0 0 0 0", p.toString());
    p.unpack(p.start(1));
    assertEquals(". 0 . . . 0 0 0", p.toString());
    p.unpack(p.start(2));
    assertEquals(". 0 . . 0 . 0 0", p.toString());
    p.unpack(p.start(3));
    assertEquals("0 . . . . 0 0 0", p.toString());
    p.unpack(p.start(4));
    assertEquals("0 . . . 0 . 0 0", p.toString());
    p.unpack(p.start(5));
    assertEquals("0 0 . . . . 0 0", p.toString());
  }

  @Test
  public void part_sizes_3() {
    assertEquals(1, getSize8_3("00000000"));
    assertEquals(24, getSize8_3("00000001"));
    assertEquals(252, getSize8_3("00000011"));
    assertEquals(1512, getSize8_3("00000111"));
    assertEquals(5670, getSize8_3("00001111"));
    assertEquals(13608, getSize8_3("00011111"));
    assertEquals(20412, getSize8_3("00111111"));
    assertEquals(17496, getSize8_3("01111111"));
    assertEquals(2187, getSize8_3("11111111"));
  }

  private int getSize8_3(final String orientation) {
    return new PackOrientation<Integer>(getBools("11111111"), getBools(orientation), 0, PART_IDS_8, 3).size();
  }

  @Test
  public void part_sizes_2() {
    assertEquals(1, getSize12_2("000000000000"));
    assertEquals(24, getSize12_2("000000000001"));
    assertEquals(264, getSize12_2("000000000011"));
    assertEquals(1760, getSize12_2("000000000111"));
    assertEquals(7920, getSize12_2("000000001111"));
    assertEquals(25344, getSize12_2("000000011111"));
    assertEquals(59136, getSize12_2("000000111111"));
    assertEquals(101376, getSize12_2("000001111111"));
    assertEquals(126720, getSize12_2("000011111111"));
    assertEquals(112640, getSize12_2("000111111111"));
    assertEquals(67584, getSize12_2("001111111111"));
    assertEquals(24576, getSize12_2("011111111111"));
    assertEquals(2048, getSize12_2("111111111111"));
  }

  private int getSize12_2(final String orientation) {
    return new PackOrientation<Integer>(getBools("111111111111"), getBools(orientation), 0, PART_IDS_12, 2).size();
  }
}
