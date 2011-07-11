package acube.pack;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public final class PackKitTest {

	private void checkStart(Pack p, int len, String v0, String vn) {
    assertEquals(len, p.startLen());
    p.unpack(p.start(0));
    assertEquals(v0, p.toString());
    p.unpack(p.start(p.startLen() - 1));
    assertEquals(vn, p.toString());
  }

  private void checkStart(Pack p, String v) {
    checkStart(p, 1, v, v);
  }

  private void checkFirst(Pack p, int len, String v) {
    assertEquals(len, p.len());
    p.unpack(0);
    assertEquals(v, p.toString());
  }

  private void checkCycle(Pack p, int i0, int i1, int i2, int i3, String v) {
    p.cycle(i0, i1, i2, i3);
    assertEquals(v, p.toString());
  }

  private void checkSwap(Pack p, int i0, int i1, String v) {
    p.swap(i0, i1);
    assertEquals(v, p.toString());
  }

  private void checkTwist(PackTwist p, int i0, int i1, int i2, int i3, String v) {
    p.twist(i0, i1, i2, i3);
    assertEquals(v, p.toString());
  }

  @Test
  public void testCornPos() {
    Pack p = PackKit.cornPos(new int[] {0, 0, 0, 1, 1, 1, 1, 1});
    checkFirst(p, CombPart.var.len(8, 5), "0 0 0 1 2 3 4 5");
    checkCycle(p, 0, 1, 4, 5, "0 2 0 1 3 0 4 5");
    checkStart(p, "0 0 0 1 2 3 4 5");
  }

  @Test
  public void testCornTwist1() {
    PackTwist p = PackKit.cornTwist(new int[] {0, 0, 0, 1, 1, 1, 0, 0}, new int[] {0, 0, 0, 0, 0, 1, 1, 1});
    checkFirst(p, 27 * CombPart.comb.len(8, 3), "0 0 0 0 0 1 1 1");
    checkCycle(p, 3, 4, 5, 6, "0 0 0 0 1 1 0 1");
    checkTwist(p, 3, 4, 5, 6, "0 0 0 0 3 2 0 1");
    checkStart(p, CombPart.comb.len(5, 2), "0 0 0 0 0 1 1 1", "1 1 0 0 0 1 0 0");
  }

  @Test
  public void testCornTwist2() {
    PackTwist p = PackKit.cornTwist(new int[] {0, 0, 0, 1, 1, 1, 0, 0}, new int[] {0, 0, 0, 0, 1, 1, 1, 1});
    checkFirst(p, 2187, "1 1 1 1 1 1 1 1");
    checkTwist(p, 3, 4, 5, 6, "1 1 1 2 3 2 3 1");
    checkCycle(p, 2, 3, 4, 5, "1 1 2 3 2 1 3 1");
    checkStart(p, 27, "1 1 1 1 1 1 1 1", "3 3 3 1 1 1 1 1");
  }

  @Test
  public void testEdgeFlip1() {
    PackTwist p = PackKit.edgeFlip(new int[] {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0}, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1});
    checkFirst(p, 8 * CombPart.comb.len(12, 3), "0 0 0 0 0 0 0 0 0 1 1 1");
    checkCycle(p, 7, 8, 9, 10, "0 0 0 0 0 0 0 0 1 1 0 1");
    checkTwist(p, 7, 8, 9, 10, "0 0 0 0 0 0 0 0 2 2 0 1");
    checkStart(p, CombPart.comb.len(9, 2), "0 0 0 0 0 0 0 0 0 1 1 1", "1 1 0 0 0 0 0 0 0 1 0 0");
  }

  @Test
  public void testEdgeFlip2() {
    PackTwist p = PackKit.edgeFlip(new int[] {0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0}, new int[] {0, 0, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1});
    checkFirst(p, 2048, "1 1 1 1 1 1 1 1 1 1 1 1");
    checkTwist(p, 7, 8, 9, 10, "1 1 1 1 1 1 1 2 2 2 2 1");
    checkCycle(p, 6, 7, 8, 9, "1 1 1 1 1 1 2 2 2 1 2 1");
    checkStart(p, 32, "1 1 1 1 1 1 1 1 1 1 1 1", "2 2 1 1 1 2 2 2 2 1 1 1");
  }

  @Test
  public void testMidgePos() {
    Pack p = PackKit.midgePos(new int[] {0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0});
    checkFirst(p, CombPart.var.len(12, 2), "0 0 0 0 0 0 0 0 0 0 1 2");
    checkCycle(p, 7, 8, 9, 10, "0 0 0 0 0 0 0 0 0 1 0 2");
    checkStart(p, "0 0 0 0 0 0 0 0 1 2 0 0");
  }
 
  @Test
  public void testMidgeLoc() {
    Pack p = PackKit.midgeLoc(new int[] {0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0});
    checkFirst(p, CombPart.comb.len(12, 2), "0 0 0 0 0 0 0 0 0 0 1 1");
    checkCycle(p, 7, 8, 9, 10, "0 0 0 0 0 0 0 0 0 1 0 1");
    checkStart(p, CombPart.comb.len(4, 2), "0 0 0 0 0 0 0 0 0 0 1 1", "0 0 0 0 0 0 0 0 1 1 0 0");
  }

  @Test
  public void testMidgePosB() {
    Pack pa = PackKit.midgePos(new int[] {0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0});
    checkFirst(pa, CombPart.var.len(12, 2), "0 0 0 0 0 0 0 0 0 0 1 2");
    checkCycle(pa, 7, 8, 9, 10, "0 0 0 0 0 0 0 0 0 1 0 2");
    Pack pb = PackKit.midgePosB(new int[] {1, 1, 0, 0});
    pb.convert(pa);
    assertEquals("0 1 0 2", pb.toString());
    checkFirst(pb, CombPart.var.len(4, 2), "0 0 1 2");
    checkStart(pb, "1 2 0 0");
  }

  @Test
  public void testEdgePosB() {
    int[] mask = {0, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 0};
    int[] udmask = {0, 0, 1, 1, 1, 1, 1, 0};
    Pack pu = PackKit.udgePos(mask);
    checkFirst(pu, CombPart.var.len(12, 2), "0 0 0 0 0 0 0 0 0 0 1 2");
    checkSwap(pu, 1, 11, "0 2 0 0 0 0 0 0 0 0 1 0");
    checkSwap(pu, 3, 10, "0 2 0 1 0 0 0 0 0 0 0 0");
    Pack pd = PackKit.dedgePos(mask);
    checkFirst(pd, CombPart.var.len(12, 3), "0 0 0 0 0 0 0 0 0 1 2 3");
    checkSwap(pd, 0, 11, "3 0 0 0 0 0 0 0 0 1 2 0");
    checkSwap(pd, 2, 10, "3 0 2 0 0 0 0 0 0 1 0 0");
    checkSwap(pd, 4, 9, "3 0 2 0 1 0 0 0 0 0 0 0");
    Pack pb = PackKit.edgePosB(udmask);
    pb.convert(pu);
    pb.combine(pd);
    assertEquals("3 5 2 4 1 0 0 0", pb.toString());
    checkFirst(pb, CombPart.var.len(8, 5), "0 0 0 1 2 3 4 5");
    checkStart(pb, "0 0 1 2 3 4 5 0");
  }
}
