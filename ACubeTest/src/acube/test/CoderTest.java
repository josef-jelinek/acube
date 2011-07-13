package acube.test;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import acube.Tools;
import acube.pack.CoderPart;
import acube.pack.CoderTools;

public final class CoderTest {
  private String toString(int[] array) {
    return Tools.intArrayToString(array);
  }

  @Test
  public void test_coder_tools_size() {
    assertEquals(40320, CoderTools.sizeOfPermutation(8));
    assertEquals(1, CoderTools.sizeOfPermutation(0));
  }

  @Test
  public void test_coder_part_size() {
    assertEquals(40320 / 120 / 6, CoderPart.unordered.size(8, 3));
    assertEquals(40320 / 120, CoderPart.ordered.size(8, 3));
    assertEquals(1, CoderPart.unordered.size(4, 4));
    assertEquals(1, CoderPart.unordered.size(0, 0));
    assertEquals(1, CoderPart.unordered.size(1, 0));
    assertEquals(6, CoderPart.ordered.size(3, 3));
    assertEquals(1, CoderPart.ordered.size(0, 0));
    assertEquals(1, CoderPart.ordered.size(1, 0));
  }

  @Test
  public void test_coder_tools_unpack() {
    int[] a = { 1, 1, 1, 1 };
    CoderTools.decodePermutationToUsed(a, 0);
    assertEquals("1 2 3 4", toString(a));
    CoderTools.decodePermutationToUsed(a, 6);
    assertEquals("2 1 3 4", toString(a));
    CoderTools.decodePermutationToUsed(a, 22);
    assertEquals("4 3 1 2", toString(a));
    CoderTools.decodePermutationToUsed(a, 23);
    assertEquals("4 3 2 1", toString(a));
    int[] b = { 0, 1, 0, 1 };
    CoderTools.decodePermutationToUsed(b, 0);
    assertEquals("0 1 0 2", toString(b));
    CoderTools.decodePermutationToUsed(b, 1);
    assertEquals("0 2 0 1", toString(b));
  }

  @Test
  public void test_coder_tools_pack() {
    int[] a = { 1, 1, 1, 1 };
    for (int i = 0; i < 24; i++) {
      CoderTools.decodePermutationToUsed(a, i);
      assertEquals(i, CoderTools.encodePermutationOfUsed(a));
    }
    int[] b = { 0, 1, 0, 1 };
    for (int i = 0; i < 2; i++) {
      CoderTools.decodePermutationToUsed(b, i);
      assertEquals(i, CoderTools.encodePermutationOfUsed(b));
    }
  }

  @Test
  public void test_coder_part_unpack() {
    int[] arr = new int[4];
    CoderPart.unordered.decode(arr, 0, 0);
    assertEquals("0 0 0 0", toString(arr));
    CoderPart.unordered.decode(arr, 4, 0);
    assertEquals("1 1 1 1", toString(arr));
    CoderPart.unordered.decode(arr, 2, 0);
    assertEquals("0 0 1 1", toString(arr));
    CoderPart.unordered.decode(arr, 2, 4);
    assertEquals("1 0 1 0", toString(arr));
    CoderPart.unordered.decode(arr, 2, 5);
    assertEquals("1 1 0 0", toString(arr));
    CoderPart.ordered.decode(arr, 0, 0);
    assertEquals("0 0 0 0", toString(arr));
    CoderPart.ordered.decode(arr, 4, 0);
    assertEquals("1 2 3 4", toString(arr));
    CoderPart.ordered.decode(arr, 2, 0);
    assertEquals("0 0 1 2", toString(arr));
    CoderPart.ordered.decode(arr, 2, 8);
    assertEquals("1 0 2 0", toString(arr));
    CoderPart.ordered.decode(arr, 2, 11);
    assertEquals("2 1 0 0", toString(arr));
  }

  @Test
  public void test_coder_part_pack() {
    int[] a = new int[4];
    for (int i = 0; i < 6; i++) {
      CoderPart.unordered.decode(a, 2, i);
      assertEquals(i, CoderPart.unordered.encode(a));
    }
    assertEquals(0, CoderPart.unordered.encode(new int[] { 0, 0, 0, 0 }));
    assertEquals(0, CoderPart.unordered.encode(new int[] { 1, 1, 1, 1 }));
    for (int i = 0; i < 12; i++) {
      CoderPart.ordered.decode(a, 2, i);
      assertEquals(i, CoderPart.ordered.encode(a));
    }
  }
}
