package acube.pack;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public final class PackPosLocTest {

  private void checkPackUnpack(Pack p) {
    for (int i = 0, l = p.len(); i < l; i++) {
      p.unpack(i);
      assertEquals(i, p.pack());
    }
  }

  @Test
  public void testPack() {
    PackPos p = new PackPos(new int[] {0, 1, 1, 1, 0}, new int[] {0, 0, 1, 1, 1});
    assertEquals(CombPart.var.len(5, 2), p.len());
    p.unpack(0);
    assertEquals("0 0 0 1 2", p.toString());
    p.unpack(1);
    assertEquals("0 0 0 2 1", p.toString());
    p.unpack(p.len() - 2);
    assertEquals("1 2 0 0 0", p.toString());
    p.unpack(p.len() - 1);
    assertEquals("2 1 0 0 0", p.toString());
    checkPackUnpack(p);
  }

  @Test
  public void testStart() {
    PackPos p = new PackPos(new int[] {0, 1, 1, 1, 0}, new int[] {0, 0, 1, 1, 1});
    assertEquals(1, p.startLen());
    p.unpack(p.start(0));
    assertEquals("0 0 1 2 0", p.toString());
  }

  @Test
  public void testLocPack() {
    PackLoc p = new PackLoc(new int[] {0, 1, 1, 1, 0}, new int[] {0, 0, 1, 1, 1});
    assertEquals(CombPart.comb.len(5, 2), p.len());
    p.unpack(0);
    assertEquals("0 0 0 1 1", p.toString());
    p.unpack(1);
    assertEquals("0 0 1 0 1", p.toString());
    p.unpack(p.len() - 2);
    assertEquals("1 0 1 0 0", p.toString());
    p.unpack(p.len() - 1);
    assertEquals("1 1 0 0 0", p.toString());
    checkPackUnpack(p);
  }

  @Test
  public void testLocStart() {
    PackLoc p = new PackLoc(new int[] {0, 1, 1, 1, 0}, new int[] {0, 0, 1, 1, 1});
    assertEquals(3, p.startLen());
    p.unpack(p.start(0));
    assertEquals("0 0 0 1 1", p.toString());
    p.unpack(p.start(1));
    assertEquals("0 0 1 0 1", p.toString());
    p.unpack(p.start(2));
    assertEquals("0 0 1 1 0", p.toString());
  }
}
