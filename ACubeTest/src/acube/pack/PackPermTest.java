package acube.pack;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public final class PackPermTest {
	
  @Test
  public void testPack() {
    Pack pack = new PackPerm(new int[] {0, 0, 1, 1});
    assertEquals(12, pack.len());
    assertEquals(1, pack.startLen());
    assertEquals(0, pack.start(0));
    pack.unpack(0);
    assertEquals("0 0 1 2", pack.toString());
    pack.unpack(11);
    assertEquals("2 1 0 0", pack.toString());
  }

  @Test
  public void testCycle() {
    Pack pack = new PackPerm(new int[] {0, 1, 1, 1, 1});
    pack.unpack(0);
    assertEquals("0 1 2 3 4", pack.toString());
    pack.swap(2, 4);
    assertEquals("0 1 4 3 2", pack.toString());
    pack.cycle(1, 2, 3, 4);
    assertEquals("0 4 3 2 1", pack.toString());
    pack.swap(0, 2);
    assertEquals("3 4 0 2 1", pack.toString());
  }
}
