package acube.test;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import acube.pack.CoderPart;
import acube.pack.Pack;
import acube.pack.PackOrientation;
import acube.pack.PackOrientationFull;
import acube.pack.PackOrientationPart;

public final class PackOrientationTest {
  @Test
  public void testTwist() {
    int[][] tw2 = { { 0, 0, 0 }, { 1, 2, 1 }, { 2, 1, 2 } };
    PackOrientation p2 = PackOrientation.instance(new int[] {}, new int[] {}, 2);
    for (int i = 0; i < tw2.length; i++) {
      assertEquals(tw2[i][0], p2.updateOrientation(tw2[i][1], -1));
      assertEquals(tw2[i][2], p2.updateOrientation(tw2[i][1], 1));
    }
    int[][] tw3 = { { 0, 0, 0 }, { 1, 2, 3 }, { 2, 3, 1 }, { 3, 1, 2 } };
    PackOrientation p3 = PackOrientation.instance(new int[] {}, new int[] {}, 3);
    for (int i = 0; i < tw3.length; i++) {
      assertEquals(tw3[i][0], p3.updateOrientation(tw3[i][1], -1));
      assertEquals(tw3[i][2], p3.updateOrientation(tw3[i][1], 1));
    }
  }

  private void checkPackUnpack(Pack p) {
    for (int i = 0, l = p.size(); i < l; i++) {
      p.unpack(i);
      assertEquals(i, p.pack());
    }
  }

  public void testFull3() {
    PackOrientation p = new PackOrientationFull(new int[] { 0, 1, 1, 1, 0 }, 3);
    assertEquals(81, p.size());
    p.unpack(0);
    assertEquals("1 1 1 1 1", p.toString());
    p.unpack(1);
    assertEquals("1 1 1 2 3", p.toString());
    p.unpack(p.size() - 2);
    assertEquals("3 3 3 2 3", p.toString());
    p.unpack(p.size() - 1);
    assertEquals("3 3 3 3 2", p.toString());
    checkPackUnpack(p);
  }

  @Test
  public void testFull2() {
    PackOrientation p = new PackOrientationFull(new int[] { 0, 1, 1, 1, 0 }, 2);
    assertEquals(16, p.size());
    p.unpack(0);
    assertEquals("1 1 1 1 1", p.toString());
    p.unpack(1);
    assertEquals("1 1 1 2 2", p.toString());
    p.unpack(p.size() - 2);
    assertEquals("2 2 2 1 2", p.toString());
    p.unpack(p.size() - 1);
    assertEquals("2 2 2 2 1", p.toString());
    checkPackUnpack(p);
  }

  @Test
  public void testFull3Start() {
    PackOrientation p = new PackOrientationFull(new int[] { 0, 1, 1, 1, 0 }, 3);
    assertEquals(3, p.startSize());
    p.unpack(p.start(0));
    assertEquals("1 1 1 1 1", p.toString());
    p.unpack(p.start(1));
    assertEquals("2 1 1 1 3", p.toString());
    p.unpack(p.start(2));
    assertEquals("3 1 1 1 2", p.toString());
  }

  @Test
  public void testFull2Start() {
    PackOrientation p = new PackOrientationFull(new int[] { 0, 1, 1, 1, 0 }, 2);
    assertEquals(2, p.startSize());
    p.unpack(p.start(0));
    assertEquals("1 1 1 1 1", p.toString());
    p.unpack(p.start(1));
    assertEquals("2 1 1 1 2", p.toString());
  }

  @Test
  public void testPart3() {
    PackOrientation p =
        new PackOrientationPart(new int[] { 0, 0, 1, 1, 1, 0, 0, 1, 1 }, new int[] {
            0, 0, 0, 0, 0, 1, 1, 1, 1 }, 3);
    assertEquals(81 * CoderPart.unordered.size(9, 4), p.size());
    p.unpack(0);
    assertEquals("0 0 0 0 0 1 1 1 1", p.toString());
    p.unpack(1);
    assertEquals("0 0 0 0 0 1 1 1 2", p.toString());
    p.unpack(p.size() - 2);
    assertEquals("3 3 3 2 0 0 0 0 0", p.toString());
    p.unpack(p.size() - 1);
    assertEquals("3 3 3 3 0 0 0 0 0", p.toString());
    checkPackUnpack(p);
  }

  @Test
  public void testPart3Start() {
    PackOrientation p =
        new PackOrientationPart(new int[] { 0, 0, 1, 1, 1, 0, 0, 1, 1 }, new int[] {
            0, 0, 0, 0, 0, 1, 1, 1, 1 }, 3);
    assertEquals(CoderPart.unordered.size(4, 2), p.startSize());
    p.unpack(p.start(0));
    assertEquals("0 0 0 0 0 1 1 1 1", p.toString());
    p.unpack(p.start(1));
    assertEquals("0 1 0 0 0 0 1 1 1", p.toString());
    p.unpack(p.start(2));
    assertEquals("0 1 0 0 0 1 0 1 1", p.toString());
    p.unpack(p.start(3));
    assertEquals("1 0 0 0 0 0 1 1 1", p.toString());
    p.unpack(p.start(4));
    assertEquals("1 0 0 0 0 1 0 1 1", p.toString());
    p.unpack(p.start(5));
    assertEquals("1 1 0 0 0 0 0 1 1", p.toString());
  }

  @Test
  public void testPart3Full() {
    PackOrientation p =
        new PackOrientationPart(new int[] { 0, 0, 1, 1, 1 }, new int[] { 1, 1, 1, 1, 1 }, 3);
    assertEquals(81, p.size());
    p.unpack(0);
    assertEquals("1 1 1 1 1", p.toString());
    p.unpack(1);
    assertEquals("1 1 1 2 3", p.toString());
    p.unpack(p.size() - 2);
    assertEquals("3 3 3 2 3", p.toString());
    p.unpack(p.size() - 1);
    assertEquals("3 3 3 3 2", p.toString());
    checkPackUnpack(p);
  }

  @Test
  public void testPart3FullStart() {
    PackOrientation p =
        new PackOrientationPart(new int[] { 0, 0, 1, 1, 1 }, new int[] { 1, 1, 1, 1, 1 }, 3);
    assertEquals(1, p.startSize());
    p.unpack(p.start(0));
    assertEquals("1 1 1 1 1", p.toString());
  }
}
