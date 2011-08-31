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
import static org.junit.Assert.assertTrue;
import java.util.EnumSet;
import org.junit.Test;
import acube.Corner;
import acube.Edge;
import acube.pack.Coder;
import acube.pack.Pack;
import acube.pack.PackKit;
import acube.pack.PackOrientation;

public final class PackKitTest {

  private <T> void checkStart(final Pack<T> pack, final int size, final String first, final String last) {
    assertEquals(size, pack.startSize());
    pack.unpack(pack.start(0));
    assertEquals(first, pack.toString());
    pack.unpack(pack.start(pack.startSize() - 1));
    assertEquals(last, pack.toString());
  }

  private <T> void checkStart(final Pack<T> pack, final String one) {
    checkStart(pack, 1, one, one);
  }

  private <T> void checkSize(final Pack<T> pack, final int size) {
    assertEquals(size, pack.size());
  }

  private <T> void checkFirst(final Pack<T> pack, final String first) {
    pack.unpack(0);
    assertEquals(first, pack.toString());
  }

  private <T> void checkCycle(final Pack<T> pack, final int[] indices, final String expected) {
    pack.cycle(indices[0], indices[1], indices[2], indices[3]);
    assertEquals(expected, pack.toString());
  }

  private <T> void checkSwap(final Pack<T> pack, final int i0, final int i1, final String expected) {
    pack.swap(i0, i1);
    assertEquals(expected, pack.toString());
  }

  private <T> void checkOrient(final PackOrientation<T> pack, final int[] indices, final String expected) {
    pack.changeOrientation(indices[0], indices[1], indices[2], indices[3]);
    assertEquals(expected, pack.toString());
  }

  @Test
  public void corner_position() {
    final EnumSet<Corner> corners = EnumSet.of(ULF, DRF, DFL, DLB, DBR);
    final Pack<Corner> pack = PackKit.cornerPos(corners);
    checkSize(pack, Coder.ordered.size(8, 5));
    checkFirst(pack, ". . . 0 1 2 3 4");
    checkCycle(pack, new int[] { 0, 1, 4, 5 }, ". 1 . 0 2 . 3 4");
    checkStart(pack, ". . . 0 1 2 3 4");
  }

  @Test
  public void corner_twist_some_positions_without_orientation() {
    final EnumSet<Corner> cornersPosition = EnumSet.of(ULF, DRF, DFL);
    final EnumSet<Corner> cornersOrientation = EnumSet.of(DFL, DLB, DBR);
    final PackOrientation<Corner> pack = PackKit.cornerTwist(cornersPosition, cornersOrientation);
    checkSize(pack, 27 * Coder.unordered.size(8, 3));
    checkFirst(pack, ". . . . . 0 0 0");
    checkCycle(pack, new int[] { 3, 4, 5, 6 }, ". . . . 0 0 . 0");
    checkOrient(pack, new int[] { 3, 4, 5, 6 }, ". . . . 2 1 . 0");
    checkStart(pack, Coder.unordered.size(5, 2), ". . . . . 0 0 0", "0 0 . . . 0 . .");
  }

  @Test
  public void corner_twist_all_positions_with_orientation() {
    final EnumSet<Corner> cornersPosition = EnumSet.of(ULF, DRF, DFL);
    final EnumSet<Corner> cornersOrientation = EnumSet.of(DRF, DFL, DLB, DBR);
    final PackOrientation<Corner> pack = PackKit.cornerTwist(cornersPosition, cornersOrientation);
    checkSize(pack, 2187);
    checkFirst(pack, "0 0 0 0 0 0 0 0");
    checkOrient(pack, new int[] { 3, 4, 5, 6 }, "0 0 0 1 2 1 2 0");
    checkCycle(pack, new int[] { 2, 3, 4, 5 }, "0 0 1 2 1 0 2 0");
    checkStart(pack, 27, "0 0 0 0 0 0 0 0", "2 2 2 0 0 0 0 0");
  }

  @Test
  public void edge_flip_some_positions_without_orientation() {
    final EnumSet<Edge> edgesPosition = EnumSet.of(DL, FR, FL);
    final EnumSet<Edge> edgesOrientation = EnumSet.of(FL, BR, BL);
    final PackOrientation<Edge> pack = PackKit.edgeFlip(edgesPosition, edgesOrientation);
    checkSize(pack, 8 * Coder.unordered.size(12, 3));
    checkFirst(pack, ". . . . . . . . . 0 0 0");
    checkCycle(pack, new int[] { 7, 8, 9, 10 }, ". . . . . . . . 0 0 . 0");
    checkOrient(pack, new int[] { 7, 8, 9, 10 }, ". . . . . . . . 1 1 . 0");
    checkStart(pack, Coder.unordered.size(9, 2), ". . . . . . . . . 0 0 0", "0 0 . . . . . . . 0 . .");
  }

  @Test
  public void edge_flip_all_positions_with_orientation() {
    final EnumSet<Edge> edgesPosition = EnumSet.of(UB, UL, DF, DL, FR, FL);
    final EnumSet<Edge> edgesOrientation = EnumSet.of(UB, UL, DF, FL, BR, BL);
    final PackOrientation<Edge> pack = PackKit.edgeFlip(edgesPosition, edgesOrientation);
    checkSize(pack, 2048);
    checkFirst(pack, "0 0 0 0 0 0 0 0 0 0 0 0");
    checkOrient(pack, new int[] { 7, 8, 9, 10 }, "0 0 0 0 0 0 0 1 1 1 1 0");
    checkCycle(pack, new int[] { 6, 7, 8, 9 }, "0 0 0 0 0 0 1 1 1 0 1 0");
    checkStart(pack, 32, "0 0 0 0 0 0 0 0 0 0 0 0", "1 1 0 0 0 1 1 1 1 0 0 0");
  }

  @Test
  public void middle_edge_position_ordered() {
    final EnumSet<Edge> edges = EnumSet.of(UL, DF, DR, DB, DL, FR, FL);
    final Pack<Edge> pack = PackKit.mEdgePos(edges);
    checkSize(pack, Coder.ordered.size(12, 2));
    checkFirst(pack, ". . . . . . . . . . 0 1");
    checkCycle(pack, new int[] { 7, 8, 9, 10 }, ". . . . . . . . . 0 . 1");
    checkStart(pack, ". . . . . . . . 0 1 . .");
  }

  @Test
  public void middle_edge_position_unordered() {
    final EnumSet<Edge> edges = EnumSet.of(UL, DF, DR, DB, DL, FR, FL);
    final Pack<Edge> pack = PackKit.mEdgePosSet(edges);
    checkSize(pack, Coder.unordered.size(12, 2));
    checkFirst(pack, ". . . . . . . . . . # #");
    checkCycle(pack, new int[] { 7, 8, 9, 10 }, ". . . . . . . . . # . #");
    checkStart(pack, Coder.unordered.size(4, 2), ". . . . . . . . . . # #", ". . . . . . . . # # . .");
  }

  @Test
  public void middle_edge_position_ordered_in_phase_B() {
    final EnumSet<Edge> edgesB = EnumSet.of(FR, FL);
    final Pack<Edge> packB = PackKit.mEdgePosB(edgesB);
    checkSize(packB, Coder.ordered.size(4, 2));
    checkFirst(packB, ". . 0 1");
    checkStart(packB, "0 1 . .");
  }

  @Test
  public void middle_edge_position_ordered_to_middle_edge_position_ordered_in_phase_B() {
    final EnumSet<Edge> edgesA = EnumSet.of(UL, DF, DR, DB, DL, FR, FL);
    final EnumSet<Edge> edgesB = EnumSet.of(FR, FL);
    final Pack<Edge> packA = PackKit.mEdgePos(edgesA);
    packA.unpack(0);
    packA.cycle(7, 8, 9, 10);
    final Pack<Edge> packB = PackKit.mEdgePosB(edgesB);
    packB.convert(packA);
    assertEquals(". 0 . 1", packB.toString());
  }

  @Test
  public void up_edge_position_ordered() {
    final EnumSet<Edge> edges = EnumSet.of(UB, UL, DF, DR, DB, FR, FL);
    final Pack<Edge> pack = PackKit.uEdgePos(edges);
    checkSize(pack, Coder.ordered.size(12, 2));
    checkFirst(pack, ". . . . . . . . . . 0 1");
    checkSwap(pack, 1, 11, ". 1 . . . . . . . . 0 .");
    checkSwap(pack, 3, 10, ". 1 . 0 . . . . . . . .");
  }

  @Test
  public void down_edge_position_ordered() {
    final EnumSet<Edge> edges = EnumSet.of(UB, UL, DF, DR, DB, FR, FL);
    final Pack<Edge> pack = PackKit.dEdgePos(edges);
    checkSize(pack, Coder.ordered.size(12, 3));
    checkFirst(pack, ". . . . . . . . . 0 1 2");
    checkSwap(pack, 0, 11, "2 . . . . . . . . 0 1 .");
    checkSwap(pack, 2, 10, "2 . 1 . . . . . . 0 . .");
    checkSwap(pack, 4, 9, "2 . 1 . 0 . . . . . . .");
  }

  @Test
  public void up_edge_down_edge_combine_to_o_edge_in_phase_B() {
    final EnumSet<Edge> edges = EnumSet.of(UB, UL, DF, DR, DB, FR, FL);
    final Pack<Edge> packU = PackKit.uEdgePos(edges);
    packU.unpack(0);
    packU.swap(1, 11);
    packU.swap(3, 10);
    final Pack<Edge> packD = PackKit.dEdgePos(edges);
    packD.unpack(0);
    packD.swap(0, 11);
    packD.swap(2, 10);
    packD.swap(4, 9);
    final EnumSet<Edge> edgesB = EnumSet.of(UB, UL, DF, DR, DB);
    final Pack<Edge> packB = PackKit.udEdgePosB(edgesB);
    assertTrue(packB.combine(packU, packD));
    assertEquals(". 1 . 0 . . . . . . . .", packU.toString());
    assertEquals("2 . 1 . 0 . . . . . . .", packD.toString());
    assertEquals("4 1 3 0 2 . . .", packB.toString());
    checkSize(packB, Coder.ordered.size(8, 5));
    checkFirst(packB, ". . . 0 1 2 3 4");
    checkStart(packB, ". . 0 1 2 3 4 .");
  }

  @Test
  public void edge_ordinals_array() {
    assertEquals(". . . . . . . . 0 1 2 3", Coder.ordered.toString(PackKit.mEdgePosOrdinals(Edge.values())));
    assertEquals("0 1 2 3 . . . . . . . .", Coder.ordered.toString(PackKit.uEdgePosOrdinals(Edge.values())));
    assertEquals(". . . . 0 1 2 3 . . . .", Coder.ordered.toString(PackKit.dEdgePosOrdinals(Edge.values())));
  }
}
