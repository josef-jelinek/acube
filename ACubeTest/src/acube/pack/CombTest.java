package acube.pack;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public final class CombTest {
  
  @Test
  public void testCombNumbers() {
    assertEquals(40320, CombFull.perm.len(8));
    assertEquals(40320/120/6, CombPart.comb.len(8, 3));
    assertEquals(40320/120, CombPart.var.len(8, 3));
    assertEquals(1, CombFull.perm.len(0));
    assertEquals(1, CombPart.comb.len(4, 4));
    assertEquals(1, CombPart.comb.len(0, 0));
    assertEquals(1, CombPart.comb.len(1, 0));
    assertEquals(6, CombPart.var.len(3, 3));
    assertEquals(1, CombPart.var.len(0, 0));
    assertEquals(1, CombPart.var.len(1, 0));
  }

  @Test
  public void testCombUnpack() {
    int[] arr = new int[4];
    CombFull.perm.unpack(arr, 0);
    assertEquals("0 1 2 3", aStr(arr));
    CombFull.perm.unpack(arr, 6);
    assertEquals("1 0 2 3", aStr(arr));
    CombFull.perm.unpack(arr, 22);
    assertEquals("3 2 0 1", aStr(arr));
    CombFull.perm.unpack(arr, 23);
    assertEquals("3 2 1 0", aStr(arr));
    CombPart.comb.unpack(arr, 0, 0);
    assertEquals("0 0 0 0", aStr(arr));
    CombPart.comb.unpack(arr, 4, 0);
    assertEquals("1 1 1 1", aStr(arr));
    CombPart.comb.unpack(arr, 2, 0);
    assertEquals("0 0 1 1", aStr(arr));
    CombPart.comb.unpack(arr, 2, 4);
    assertEquals("1 0 1 0", aStr(arr));
    CombPart.comb.unpack(arr, 2, 5);
    assertEquals("1 1 0 0", aStr(arr));
    CombPart.var.unpack(arr, 0, 0);
    assertEquals("0 0 0 0", aStr(arr));
    CombPart.var.unpack(arr, 4, 0);
    assertEquals("1 2 3 4", aStr(arr));
    CombPart.var.unpack(arr, 2, 0);
    assertEquals("0 0 1 2", aStr(arr));
    CombPart.var.unpack(arr, 2, 8);
    assertEquals("1 0 2 0", aStr(arr));
    CombPart.var.unpack(arr, 2, 11);
    assertEquals("2 1 0 0", aStr(arr));
  }

  @Test
  public void testCombPack() {
    int[] arr = new int[4];
    for (int i = 0; i < 24; i++) {
      CombFull.perm.unpack(arr, i);
      assertEquals(i, CombFull.perm.pack(arr));
    }
    for (int i = 0; i < 6; i++) {
      CombPart.comb.unpack(arr, 2, i);
      assertEquals(i, CombPart.comb.pack(arr));
    }
    assertEquals(0, CombPart.comb.pack(new int[] {0, 0, 0, 0}));
    assertEquals(0, CombPart.comb.pack(new int[] {1, 1, 1, 1}));
    for (int i = 0; i < 12; i++) {
      CombPart.var.unpack(arr, 2, i);
      assertEquals(i, CombPart.var.pack(arr));
    }
  }

  private String aStr(int[] arr) {
    if (arr.length == 0) return "";
    String s = "" + arr[0];
    for (int i = 1; i < arr.length; i++)
      s += " " + arr[i];
    return s;
  }
}
