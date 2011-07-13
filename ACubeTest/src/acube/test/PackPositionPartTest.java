package acube.test;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import acube.pack.CoderPart;
import acube.pack.Pack;
import acube.pack.PackPositionPartOrdered;
import acube.pack.PackPositionPartUnordered;

public final class PackPositionPartTest {
  private void checkPackUnpack(Pack p) {
    for (int i = 0, l = p.size(); i < l; i++) {
      p.unpack(i);
      assertEquals(i, p.pack());
    }
  }

  @Test
  public void testPack() {
    PackPositionPartOrdered p =
        new PackPositionPartOrdered(new int[] { 0, 1, 1, 1, 0 }, new int[] { 0, 0, 1, 1, 1 });
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
  public void testStart() {
    PackPositionPartOrdered p =
        new PackPositionPartOrdered(new int[] { 0, 1, 1, 1, 0 }, new int[] { 0, 0, 1, 1, 1 });
    assertEquals(1, p.startSize());
    p.unpack(p.start(0));
    assertEquals("0 0 1 2 0", p.toString());
  }

  @Test
  public void testLocPack() {
    PackPositionPartUnordered p =
        new PackPositionPartUnordered(new int[] { 0, 1, 1, 1, 0 }, new int[] { 0, 0, 1, 1, 1 });
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
  public void testLocStart() {
    PackPositionPartUnordered p =
        new PackPositionPartUnordered(new int[] { 0, 1, 1, 1, 0 }, new int[] { 0, 0, 1, 1, 1 });
    assertEquals(3, p.startSize());
    p.unpack(p.start(0));
    assertEquals("0 0 0 1 1", p.toString());
    p.unpack(p.start(1));
    assertEquals("0 0 1 0 1", p.toString());
    p.unpack(p.start(2));
    assertEquals("0 0 1 1 0", p.toString());
  }
}
