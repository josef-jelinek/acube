package acube.pack;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

public final class PackTwistTest {

  @Test
  public void testTwist() {
    int[][] tw2 = {{0,0,0},{1,2,1},{2,1,2}};
    PackTwist p2 = PackTwist.obj(new int[] {}, new int[] {}, 2);
    for (int i = 0; i < tw2.length; i++) {
      Assert.assertEquals(tw2[i][0], p2.twist(tw2[i][1], -1));
      assertEquals(tw2[i][2], p2.twist(tw2[i][1], 1));
    }
    int[][] tw3 = {{0,0,0},{1,2,3},{2,3,1},{3,1,2}};
    PackTwist p3 = PackTwist.obj(new int[] {}, new int[] {}, 3);
    for (int i = 0; i < tw3.length; i++) {
      assertEquals(tw3[i][0], p3.twist(tw3[i][1], -1));
      assertEquals(tw3[i][2], p3.twist(tw3[i][1], 1));
    }
  }

  private void checkPackUnpack(Pack p) {
    for (int i = 0, l = p.len(); i < l; i++) {
      p.unpack(i);
      assertEquals(i, p.pack());
    }
  }

  @Test
  public void testFull3() {
    PackTwist p = new PackTwistFull(new int[] {0, 1, 1, 1, 0}, 3);
    assertEquals(81, p.len());
    p.unpack(0);
    assertEquals("1 1 1 1 1", p.toString());
    p.unpack(1);
    assertEquals("1 1 1 2 3", p.toString());
    p.unpack(p.len() - 2);
    assertEquals("3 3 3 2 3", p.toString());
    p.unpack(p.len() - 1);
    assertEquals("3 3 3 3 2", p.toString());
    checkPackUnpack(p);
  }

  @Test
  public void testFull2() {
    PackTwist p = new PackTwistFull(new int[] {0, 1, 1, 1, 0}, 2);
    assertEquals(16, p.len());
    p.unpack(0);
    assertEquals("1 1 1 1 1", p.toString());
    p.unpack(1);
    assertEquals("1 1 1 2 2", p.toString());
    p.unpack(p.len() - 2);
    assertEquals("2 2 2 1 2", p.toString());
    p.unpack(p.len() - 1);
    assertEquals("2 2 2 2 1", p.toString());
    checkPackUnpack(p);
  }

  @Test
  public void testFull3Start() {
    PackTwist p = new PackTwistFull(new int[] {0, 1, 1, 1, 0}, 3);
    assertEquals(3, p.startLen());
    p.unpack(p.start(0));
    assertEquals("1 1 1 1 1", p.toString());
    p.unpack(p.start(1));
    assertEquals("2 1 1 1 3", p.toString());
    p.unpack(p.start(2));
    assertEquals("3 1 1 1 2", p.toString());
  }

  @Test
  public void testFull2Start() {
    PackTwist p = new PackTwistFull(new int[] {0, 1, 1, 1, 0}, 2);
    assertEquals(2, p.startLen());
    p.unpack(p.start(0));
    assertEquals("1 1 1 1 1", p.toString());
    p.unpack(p.start(1));
    assertEquals("2 1 1 1 2", p.toString());
  }

  @Test
  public void testPart3() {
    PackTwist p = new PackTwistPart(new int[] {0, 0, 1, 1, 1, 0, 0, 1, 1}, new int[] {0, 0, 0, 0, 0, 1, 1, 1, 1}, 3);
    assertEquals(81 * CombPart.comb.len(9, 4), p.len());
    p.unpack(0);
    assertEquals("0 0 0 0 0 1 1 1 1", p.toString());
    p.unpack(1);
    assertEquals("0 0 0 0 0 1 1 1 2", p.toString());
    p.unpack(p.len() - 2);
    assertEquals("3 3 3 2 0 0 0 0 0", p.toString());
    p.unpack(p.len() - 1);
    assertEquals("3 3 3 3 0 0 0 0 0", p.toString());
    checkPackUnpack(p);
  }

  @Test
  public void testPart3Start() {
    PackTwist p = new PackTwistPart(new int[] {0, 0, 1, 1, 1, 0, 0, 1, 1}, new int[] {0, 0, 0, 0, 0, 1, 1, 1, 1}, 3);
    assertEquals(CombPart.comb.len(4, 2), p.startLen());
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
    PackTwist p = new PackTwistPart(new int[] {0, 0, 1, 1, 1}, new int[] {1, 1, 1, 1, 1}, 3);
    assertEquals(81, p.len());
    p.unpack(0);
    assertEquals("1 1 1 1 1", p.toString());
    p.unpack(1);
    assertEquals("1 1 1 2 3", p.toString());
    p.unpack(p.len() - 2);
    assertEquals("3 3 3 2 3", p.toString());
    p.unpack(p.len() - 1);
    assertEquals("3 3 3 3 2", p.toString());
    checkPackUnpack(p);
  }

  @Test
  public void testPart3FullStart() {
    PackTwist p = new PackTwistPart(new int[] {0, 0, 1, 1, 1}, new int[] {1, 1, 1, 1, 1}, 3);
    assertEquals(1, p.startLen());
    p.unpack(p.start(0));
    assertEquals("1 1 1 1 1", p.toString());
  }
}
