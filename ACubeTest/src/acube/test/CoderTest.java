package acube.test;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import acube.pack.Coder;
import acube.pack.CoderOrdered;
import acube.pack.CoderTools;
import acube.pack.CoderUnordered;

public final class CoderTest {
  private String toString(final int[] array) {
    return CoderOrdered.toString(array);
  }

  private String toStringU(final int[] array) {
    return CoderUnordered.toString(array);
  }

  @Test
  public void test_coder_tools_size() {
    assertEquals(40320, CoderTools.sizeOfPermutation(8));
    assertEquals(1, CoderTools.sizeOfPermutation(0));
  }

  @Test
  public void test_coder_part_size() {
    assertEquals(40320 / 120 / 6, Coder.unordered.size(8, 3));
    assertEquals(40320 / 120, Coder.ordered.size(8, 3));
    assertEquals(1, Coder.unordered.size(4, 4));
    assertEquals(1, Coder.unordered.size(0, 0));
    assertEquals(1, Coder.unordered.size(1, 0));
    assertEquals(6, Coder.ordered.size(3, 3));
    assertEquals(1, Coder.ordered.size(0, 0));
    assertEquals(1, Coder.ordered.size(1, 0));
  }

  @Test
  public void test_coder_tools_unpack() {
    final int[] a = { 0, 0, 0, 0 };
    CoderTools.decodePermutationToUsed(a, 0);
    assertEquals("0 1 2 3", toString(a));
    CoderTools.decodePermutationToUsed(a, 6);
    assertEquals("1 0 2 3", toString(a));
    CoderTools.decodePermutationToUsed(a, 22);
    assertEquals("3 2 0 1", toString(a));
    CoderTools.decodePermutationToUsed(a, 23);
    assertEquals("3 2 1 0", toString(a));
    final int[] b = { -1, 0, -1, 0 };
    CoderTools.decodePermutationToUsed(b, 0);
    assertEquals(". 0 . 1", toString(b));
    CoderTools.decodePermutationToUsed(b, 1);
    assertEquals(". 1 . 0", toString(b));
  }

  @Test
  public void test_coder_tools_pack() {
    final int[] a = { 1, 1, 1, 1 };
    for (int i = 0; i < 24; i++) {
      CoderTools.decodePermutationToUsed(a, i);
      assertEquals(i, CoderTools.encodePermutationOfUsed(a));
    }
    final int[] b = { 0, 1, 0, 1 };
    for (int i = 0; i < 2; i++) {
      CoderTools.decodePermutationToUsed(b, i);
      assertEquals(i, CoderTools.encodePermutationOfUsed(b));
    }
  }

  @Test
  public void test_coder_part_unpack() {
    final int[] arr = new int[4];
    Coder.unordered.decode(arr, 0, 0);
    assertEquals(". . . .", toStringU(arr));
    Coder.unordered.decode(arr, 4, 0);
    assertEquals("# # # #", toStringU(arr));
    Coder.unordered.decode(arr, 2, 0);
    assertEquals(". . # #", toStringU(arr));
    Coder.unordered.decode(arr, 2, 4);
    assertEquals("# . # .", toStringU(arr));
    Coder.unordered.decode(arr, 2, 5);
    assertEquals("# # . .", toStringU(arr));
    Coder.ordered.decode(arr, 0, 0);
    assertEquals(". . . .", toString(arr));
    Coder.ordered.decode(arr, 4, 0);
    assertEquals("0 1 2 3", toString(arr));
    Coder.ordered.decode(arr, 2, 0);
    assertEquals(". . 0 1", toString(arr));
    Coder.ordered.decode(arr, 2, 8);
    assertEquals("0 . 1 .", toString(arr));
    Coder.ordered.decode(arr, 2, 11);
    assertEquals("1 0 . .", toString(arr));
  }

  @Test
  public void test_coder_part_pack() {
    final int[] a = new int[4];
    for (int i = 0; i < 6; i++) {
      Coder.unordered.decode(a, 2, i);
      assertEquals(i, Coder.unordered.encode(a));
    }
    assertEquals(0, Coder.unordered.encode(new int[] { 0, 0, 0, 0 }));
    assertEquals(0, Coder.unordered.encode(new int[] { 1, 1, 1, 1 }));
    for (int i = 0; i < 12; i++) {
      Coder.ordered.decode(a, 2, i);
      assertEquals(i, Coder.ordered.encode(a));
    }
  }
}
