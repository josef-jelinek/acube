package acube.test;

import static acube.Corner.DBR;
import static acube.Corner.DFL;
import static acube.Corner.DLB;
import static acube.Corner.DRF;
import static acube.Corner.ULF;
import static acube.Edge.BL;
import static acube.Edge.BR;
import static acube.Edge.DB;
import static acube.Edge.DF;
import static acube.Edge.DL;
import static acube.Edge.DR;
import static acube.Edge.FL;
import static acube.Edge.FR;
import static acube.Edge.UB;
import static acube.Edge.UL;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import acube.Corner;
import acube.Edge;
import acube.pack.CoderPart;
import acube.pack.Pack;
import acube.pack.PackKit;
import acube.pack.PackOrientation;

public final class PackKitTest {
  private void checkStart(final Pack pack, final int size, final String first, final String last) {
    assertEquals(size, pack.startSize());
    pack.unpack(pack.start(0));
    assertEquals(first, pack.toString());
    pack.unpack(pack.start(pack.startSize() - 1));
    assertEquals(last, pack.toString());
  }

  private void checkStart(final Pack pack, final String one) {
    checkStart(pack, 1, one, one);
  }

  private void checkSize(final Pack pack, final int size) {
    assertEquals(size, pack.size());
  }

  private void checkFirst(final Pack pack, final String first) {
    pack.unpack(0);
    assertEquals(first, pack.toString());
  }

  private void checkCycle(final Pack pack, final int[] indices, final String expected) {
    pack.cycle(indices[0], indices[1], indices[2], indices[3]);
    assertEquals(expected, pack.toString());
  }

  private void checkSwap(final Pack pack, final int i0, final int i1, final String expected) {
    pack.swap(i0, i1);
    assertEquals(expected, pack.toString());
  }

  private void checkOrient(final PackOrientation pack, final int[] indices, final String expected) {
    pack.changeOrientation(indices[0], indices[1], indices[2], indices[3]);
    assertEquals(expected, pack.toString());
  }

  @Test
  public void corner_position() {
    final Corner[] corners = { ULF, DRF, DFL, DLB, DBR };
    final Pack pack = PackKit.cornerPosition(corners);
    checkSize(pack, CoderPart.ordered.size(8, 5));
    checkFirst(pack, "0 0 0 1 2 3 4 5");
    checkCycle(pack, new int[] { 0, 1, 4, 5 }, "0 2 0 1 3 0 4 5");
    checkStart(pack, "0 0 0 1 2 3 4 5");
  }

  @Test
  public void corner_twist_some_positions_without_orientation() {
    final Corner[] cornersPosition = { ULF, DRF, DFL };
    final Corner[] cornersOrientation = { DFL, DLB, DBR };
    final PackOrientation pack = PackKit.cornerTwist(cornersPosition, cornersOrientation);
    checkSize(pack, 27 * CoderPart.unordered.size(8, 3));
    checkFirst(pack, "0 0 0 0 0 1 1 1");
    checkCycle(pack, new int[] { 3, 4, 5, 6 }, "0 0 0 0 1 1 0 1");
    checkOrient(pack, new int[] { 3, 4, 5, 6 }, "0 0 0 0 3 2 0 1");
    checkStart(pack, CoderPart.unordered.size(5, 2), "0 0 0 0 0 1 1 1", "1 1 0 0 0 1 0 0");
  }

  @Test
  public void corner_twist_all_positions_with_orientation() {
    final Corner[] cornersPosition = { ULF, DRF, DFL };
    final Corner[] cornersOrientation = { DRF, DFL, DLB, DBR };
    final PackOrientation pack = PackKit.cornerTwist(cornersPosition, cornersOrientation);
    checkSize(pack, 2187);
    checkFirst(pack, "1 1 1 1 1 1 1 1");
    checkOrient(pack, new int[] { 3, 4, 5, 6 }, "1 1 1 2 3 2 3 1");
    checkCycle(pack, new int[] { 2, 3, 4, 5 }, "1 1 2 3 2 1 3 1");
    checkStart(pack, 27, "1 1 1 1 1 1 1 1", "3 3 3 1 1 1 1 1");
  }

  @Test
  public void edge_flip_some_positions_without_orientation() {
    final Edge[] edgesPosition = { DL, FR, FL };
    final Edge[] edgesOrientation = { FL, BR, BL };
    final PackOrientation pack = PackKit.edgeFlip(edgesPosition, edgesOrientation);
    checkSize(pack, 8 * CoderPart.unordered.size(12, 3));
    checkFirst(pack, "0 0 0 0 0 0 0 0 0 1 1 1");
    checkCycle(pack, new int[] { 7, 8, 9, 10 }, "0 0 0 0 0 0 0 0 1 1 0 1");
    checkOrient(pack, new int[] { 7, 8, 9, 10 }, "0 0 0 0 0 0 0 0 2 2 0 1");
    checkStart(pack, CoderPart.unordered.size(9, 2), "0 0 0 0 0 0 0 0 0 1 1 1", "1 1 0 0 0 0 0 0 0 1 0 0");
  }

  @Test
  public void edge_flip_all_positions_with_orientation() {
    final Edge[] edgesPosition = { UB, UL, DF, DL, FR, FL };
    final Edge[] edgesOrientation = { UB, UL, DF, FL, BR, BL };
    final PackOrientation pack = PackKit.edgeFlip(edgesPosition, edgesOrientation);
    checkSize(pack, 2048);
    checkFirst(pack, "1 1 1 1 1 1 1 1 1 1 1 1");
    checkOrient(pack, new int[] { 7, 8, 9, 10 }, "1 1 1 1 1 1 1 2 2 2 2 1");
    checkCycle(pack, new int[] { 6, 7, 8, 9 }, "1 1 1 1 1 1 2 2 2 1 2 1");
    checkStart(pack, 32, "1 1 1 1 1 1 1 1 1 1 1 1", "2 2 1 1 1 2 2 2 2 1 1 1");
  }

  @Test
  public void middle_edge_position_ordered() {
    final Edge[] edges = { UL, DF, DR, DB, DL, FR, FL };
    final Pack pack = PackKit.mEdgePosition(edges);
    checkSize(pack, CoderPart.ordered.size(12, 2));
    checkFirst(pack, "0 0 0 0 0 0 0 0 0 0 1 2");
    checkCycle(pack, new int[] { 7, 8, 9, 10 }, "0 0 0 0 0 0 0 0 0 1 0 2");
    checkStart(pack, "0 0 0 0 0 0 0 0 1 2 0 0");
  }

  @Test
  public void middle_edge_position_unordered() {
    final Edge[] edges = { UL, DF, DR, DB, DL, FR, FL };
    final Pack pack = PackKit.mEdgePositionSet(edges);
    checkSize(pack, CoderPart.unordered.size(12, 2));
    checkFirst(pack, "0 0 0 0 0 0 0 0 0 0 1 1");
    checkCycle(pack, new int[] { 7, 8, 9, 10 }, "0 0 0 0 0 0 0 0 0 1 0 1");
    checkStart(pack, CoderPart.unordered.size(4, 2), "0 0 0 0 0 0 0 0 0 0 1 1", "0 0 0 0 0 0 0 0 1 1 0 0");
  }

  @Test
  public void middle_edge_position_ordered_in_phase_B() {
    final Edge[] edgesB = { FR, FL };
    final Pack packB = PackKit.mEdgePositionB(edgesB);
    checkSize(packB, CoderPart.ordered.size(4, 2));
    checkFirst(packB, "0 0 1 2");
    checkStart(packB, "1 2 0 0");
  }

  @Test
  public void middle_edge_position_ordered_to_middle_edge_position_ordered_in_phase_B() {
    final Edge[] edgesA = { UL, DF, DR, DB, DL, FR, FL };
    final Edge[] edgesB = { FR, FL };
    final Pack packA = PackKit.mEdgePosition(edgesA);
    packA.unpack(0);
    packA.cycle(7, 8, 9, 10);
    final Pack packB = PackKit.mEdgePositionB(edgesB);
    packB.convert(packA);
    assertEquals("0 1 0 2", packB.toString());
  }

  @Test
  public void up_edge_position_ordered() {
    final Edge[] edges = { UB, UL, DF, DR, DB, FR, FL };
    final Pack pack = PackKit.uEdgePosition(edges);
    checkSize(pack, CoderPart.ordered.size(12, 2));
    checkFirst(pack, "0 0 0 0 0 0 0 0 0 0 1 2");
    checkSwap(pack, 1, 11, "0 2 0 0 0 0 0 0 0 0 1 0");
    checkSwap(pack, 3, 10, "0 2 0 1 0 0 0 0 0 0 0 0");
  }

  @Test
  public void down_edge_position_ordered() {
    final Edge[] edges = { UB, UL, DF, DR, DB, FR, FL };
    final Pack pack = PackKit.dEdgePosition(edges);
    checkSize(pack, CoderPart.ordered.size(12, 3));
    checkFirst(pack, "0 0 0 0 0 0 0 0 0 1 2 3");
    checkSwap(pack, 0, 11, "3 0 0 0 0 0 0 0 0 1 2 0");
    checkSwap(pack, 2, 10, "3 0 2 0 0 0 0 0 0 1 0 0");
    checkSwap(pack, 4, 9, "3 0 2 0 1 0 0 0 0 0 0 0");
  }

  @Test
  public void up_edge_down_edge_combine_to_o_edge_in_phase_B() {
    final Edge[] edges = { UB, UL, DF, DR, DB, FR, FL };
    final Pack packU = PackKit.uEdgePosition(edges);
    packU.unpack(0);
    packU.swap(1, 11);
    packU.swap(3, 10);
    final Pack packD = PackKit.dEdgePosition(edges);
    packD.unpack(0);
    packD.swap(0, 11);
    packD.swap(2, 10);
    packD.swap(4, 9);
    final Edge[] edgesB = { UB, UL, DF, DR, DB };
    final Pack packB = PackKit.oEdgePositionB(edgesB);
    packB.convert(packU);
    packB.combine(packD);
    assertEquals("3 5 2 4 1 0 0 0", packB.toString());
    checkSize(packB, CoderPart.ordered.size(8, 5));
    checkFirst(packB, "0 0 0 1 2 3 4 5");
    checkStart(packB, "0 0 1 2 3 4 5 0");
  }
}
