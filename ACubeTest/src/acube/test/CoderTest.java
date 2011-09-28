package acube.test;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import acube.pack.Coder;
import acube.pack.CoderTools;

public final class CoderTest {
  private String toString(final int[] array) {
    return Coder.ordered.toString(array);
  }

  private String toStringU(final int[] array) {
    return Coder.unordered.toString(array);
  }

  @Test
  public void size() {
    assertEquals(40320, CoderTools.permutationSize(8));
    assertEquals(1, CoderTools.permutationSize(0));
  }

  @Test
  public void part_size() {
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
  public void unpack() {
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
  public void pack() {
    final int[] a = { 1, 1, 1, 1 };
    for (int i = 0; i < 24; i++) {
      CoderTools.decodePermutationToUsed(a, i);
      assertEquals(i, CoderTools.encodeUsedPermutation(a));
    }
    final int[] b = { 0, 1, 0, 1 };
    for (int i = 0; i < 2; i++) {
      CoderTools.decodePermutationToUsed(b, i);
      assertEquals(i, CoderTools.encodeUsedPermutation(b));
    }
  }

  @Test
  public void coder_part_unpack() {
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
  public void coder_part_pack() {
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

  @Test
  public void parity() {
    assertEquals(0, CoderTools.permutationParity(new int[] { 0, 1, 2 }));
    assertEquals(1, CoderTools.permutationParity(new int[] { 0, 2, 1 }));
    assertEquals(1, CoderTools.permutationParity(new int[] { 2, 1, 0 }));
    assertEquals(0, CoderTools.permutationParity(new int[] { 1, 2, 0 }));
    assertEquals(0, CoderTools.permutationParity(new int[] { -1, 1, 2 }));
    assertEquals(1, CoderTools.permutationParity(new int[] { 0, 2, -1 }));
    assertEquals(1, CoderTools.permutationParity(new int[] { -1, 1, 0 }));
    assertEquals(0, CoderTools.permutationParity(new int[] { -1, 2, 0 }));
  }
}
