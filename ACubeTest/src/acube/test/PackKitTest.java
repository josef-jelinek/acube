package acube.test;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import acube.Corner;
import acube.Edge;
import acube.pack.CoderPart;
import acube.pack.Pack;
import acube.pack.PackKit;
import acube.pack.PackOrientation;

public final class PackKitTest {
  private void checkStart(Pack pack, int size, String first, String last) {
    assertEquals(size, pack.startSize());
    pack.unpack(pack.start(0));
    assertEquals(first, pack.toString());
    pack.unpack(pack.start(pack.startSize() - 1));
    assertEquals(last, pack.toString());
  }

  private void checkStart(Pack pack, String one) {
    checkStart(pack, 1, one, one);
  }

  private void checkSize(Pack pack, int size) {
    assertEquals(size, pack.size());
  }

  private void checkFirst(Pack pack, String first) {
    pack.unpack(0);
    assertEquals(first, pack.toString());
  }

  private void checkCycle(Pack pack, int i0, int i1, int i2, int i3, String expected) {
    pack.cycle(i0, i1, i2, i3);
    assertEquals(expected, pack.toString());
  }

  private void checkSwap(Pack pack, int i0, int i1, String expected) {
    pack.swap(i0, i1);
    assertEquals(expected, pack.toString());
  }

  private void checkOrient(PackOrientation pack, int i0, int i1, int i2, int i3, String expected) {
    pack.changeOrientation(i0, i1, i2, i3);
    assertEquals(expected, pack.toString());
  }

  @Test
  public void test_corner_position() {
    Corner[] corners = { Corner.ULF, Corner.DRF, Corner.DFL, Corner.DLB, Corner.DBR };
    Pack pack = PackKit.cornerPosition(corners);
    checkSize(pack, CoderPart.ordered.size(8, 5));
    checkFirst(pack, "0 0 0 1 2 3 4 5");
    checkCycle(pack, 0, 1, 4, 5, "0 2 0 1 3 0 4 5");
    checkStart(pack, "0 0 0 1 2 3 4 5");
  }

  @Test
  public void test_corner_twist_void_positions() {
    Corner[] cornersPosition = { Corner.ULF, Corner.DRF, Corner.DFL };
    Corner[] cornersOrientation = { Corner.DFL, Corner.DLB, Corner.DBR };
    PackOrientation pack = PackKit.cornerTwist(cornersPosition, cornersOrientation);
    checkSize(pack, 27 * CoderPart.unordered.size(8, 3));
    checkFirst(pack, "0 0 0 0 0 1 1 1");
    checkCycle(pack, 3, 4, 5, 6, "0 0 0 0 1 1 0 1");
    checkOrient(pack, 3, 4, 5, 6, "0 0 0 0 3 2 0 1");
    checkStart(pack, CoderPart.unordered.size(5, 2), "0 0 0 0 0 1 1 1", "1 1 0 0 0 1 0 0");
  }

  @Test
  public void test_corn_twist_all_positions() {
    PackOrientation pack =
        PackKit.cornerTwist(new Corner[] { Corner.ULF, Corner.DRF, Corner.DFL }, new Corner[] {
            Corner.DRF, Corner.DFL, Corner.DLB, Corner.DBR });
    checkSize(pack, 2187);
    checkFirst(pack, "1 1 1 1 1 1 1 1");
    checkOrient(pack, 3, 4, 5, 6, "1 1 1 2 3 2 3 1");
    checkCycle(pack, 2, 3, 4, 5, "1 1 2 3 2 1 3 1");
    checkStart(pack, 27, "1 1 1 1 1 1 1 1", "3 3 3 1 1 1 1 1");
  }

  @Test
  public void test_edge_flip_void_positions() {
    Edge[] edgesPosition = { Edge.DL, Edge.FR, Edge.FL };
    Edge[] edgesOrientation = { Edge.FL, Edge.BR, Edge.BL };
    PackOrientation pack = PackKit.edgeFlip(edgesPosition, edgesOrientation);
    checkSize(pack, 8 * CoderPart.unordered.size(12, 3));
    checkFirst(pack, "0 0 0 0 0 0 0 0 0 1 1 1");
    checkCycle(pack, 7, 8, 9, 10, "0 0 0 0 0 0 0 0 1 1 0 1");
    checkOrient(pack, 7, 8, 9, 10, "0 0 0 0 0 0 0 0 2 2 0 1");
    checkStart(pack, CoderPart.unordered.size(9, 2), "0 0 0 0 0 0 0 0 0 1 1 1",
        "1 1 0 0 0 0 0 0 0 1 0 0");
  }

  @Test
  public void test_edge_flip_all_poitions() {
    Edge[] edgesPosition = { Edge.UB, Edge.UL, Edge.DF, Edge.DL, Edge.FR, Edge.FL };
    Edge[] edgesOrientation = { Edge.UB, Edge.UL, Edge.DF, Edge.FL, Edge.BR, Edge.BL };
    PackOrientation pack = PackKit.edgeFlip(edgesPosition, edgesOrientation);
    checkSize(pack, 2048);
    checkFirst(pack, "1 1 1 1 1 1 1 1 1 1 1 1");
    checkOrient(pack, 7, 8, 9, 10, "1 1 1 1 1 1 1 2 2 2 2 1");
    checkCycle(pack, 6, 7, 8, 9, "1 1 1 1 1 1 2 2 2 1 2 1");
    checkStart(pack, 32, "1 1 1 1 1 1 1 1 1 1 1 1", "2 2 1 1 1 2 2 2 2 1 1 1");
  }

  @Test
  public void test_middle_edge_position_ordered() {
    Edge[] edges = { Edge.UL, Edge.DF, Edge.DR, Edge.DB, Edge.DL, Edge.FR, Edge.FL };
    Pack pack = PackKit.mEdgePosition(edges);
    checkSize(pack, CoderPart.ordered.size(12, 2));
    checkFirst(pack, "0 0 0 0 0 0 0 0 0 0 1 2");
    checkCycle(pack, 7, 8, 9, 10, "0 0 0 0 0 0 0 0 0 1 0 2");
    checkStart(pack, "0 0 0 0 0 0 0 0 1 2 0 0");
  }

  @Test
  public void test_middle_edge_position_unordered() {
    Edge[] edges = { Edge.UL, Edge.DF, Edge.DR, Edge.DB, Edge.DL, Edge.FR, Edge.FL };
    Pack pack = PackKit.mEdgePositionSet(edges);
    checkSize(pack, CoderPart.unordered.size(12, 2));
    checkFirst(pack, "0 0 0 0 0 0 0 0 0 0 1 1");
    checkCycle(pack, 7, 8, 9, 10, "0 0 0 0 0 0 0 0 0 1 0 1");
    checkStart(pack, CoderPart.unordered.size(4, 2), "0 0 0 0 0 0 0 0 0 0 1 1",
        "0 0 0 0 0 0 0 0 1 1 0 0");
  }

  @Test
  public void test_middle_edge_position_ordered_B() {
    Edge[] edgesA = { Edge.UL, Edge.DF, Edge.DR, Edge.DB, Edge.DL, Edge.FR, Edge.FL };
    Pack packA = PackKit.mEdgePosition(edgesA);
    checkSize(packA, CoderPart.ordered.size(12, 2));
    checkFirst(packA, "0 0 0 0 0 0 0 0 0 0 1 2");
    checkCycle(packA, 7, 8, 9, 10, "0 0 0 0 0 0 0 0 0 1 0 2");
    Edge[] edgesB = { Edge.FR, Edge.FL };
    Pack packB = PackKit.mEdgePositionB(edgesB);
    packB.convert(packA);
    assertEquals("0 1 0 2", packB.toString());
    checkSize(packB, CoderPart.ordered.size(4, 2));
    checkFirst(packB, "0 0 1 2");
    checkStart(packB, "1 2 0 0");
  }

  @Test
  public void test_up_down_edge_position_ordered_B() {
    Edge[] edges = { Edge.UB, Edge.UL, Edge.DF, Edge.DR, Edge.DB, Edge.FR, Edge.FL };
    Pack packU = PackKit.uEdgePosition(edges);
    checkSize(packU, CoderPart.ordered.size(12, 2));
    checkFirst(packU, "0 0 0 0 0 0 0 0 0 0 1 2");
    checkSwap(packU, 1, 11, "0 2 0 0 0 0 0 0 0 0 1 0");
    checkSwap(packU, 3, 10, "0 2 0 1 0 0 0 0 0 0 0 0");
    Pack packD = PackKit.dEdgePosition(edges);
    checkSize(packD, CoderPart.ordered.size(12, 3));
    checkFirst(packD, "0 0 0 0 0 0 0 0 0 1 2 3");
    checkSwap(packD, 0, 11, "3 0 0 0 0 0 0 0 0 1 2 0");
    checkSwap(packD, 2, 10, "3 0 2 0 0 0 0 0 0 1 0 0");
    checkSwap(packD, 4, 9, "3 0 2 0 1 0 0 0 0 0 0 0");
    Edge[] edgesB = { Edge.UB, Edge.UL, Edge.DF, Edge.DR, Edge.DB };
    Pack packB = PackKit.oEdgePositionB(edgesB);
    packB.convert(packU);
    packB.combine(packD);
    assertEquals("3 5 2 4 1 0 0 0", packB.toString());
    checkSize(packB, CoderPart.ordered.size(8, 5));
    checkFirst(packB, "0 0 0 1 2 3 4 5");
    checkStart(packB, "0 0 1 2 3 4 5 0");
  }
}
