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
import static acube.Edge.UF;
import static acube.Edge.UL;
import static acube.Edge.UR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.EnumSet;
import org.junit.Test;
import acube.Corner;
import acube.Edge;
import acube.pack.Coder;
import acube.pack.Pack;
import acube.pack.PackKit;
import acube.pack.PackPositionOrdered;

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

  @Test
  public void corner_position() {
    final EnumSet<Corner> corners = EnumSet.of(ULF, DRF, DFL, DLB, DBR);
    final Pack<Corner> pack = new PackPositionOrdered<Corner>(PackKit.cornerMask(corners), Corner.values());
    checkSize(pack, Coder.ordered.size(8, 5));
    checkFirst(pack, ". . . 0 1 2 3 4");
    checkCycle(pack, new int[] { 0, 1, 4, 5 }, ". 1 . 0 2 . 3 4");
    checkStart(pack, ". . . 0 1 2 3 4");
  }

  @Test
  public void middle_edge_position_ordered() {
    final EnumSet<Edge> edges = EnumSet.of(UL, DF, DR, DB, DL, FR, FL);
    final Pack<Edge> pack =
        new PackPositionOrdered<Edge>(PackKit.edgeMask(edges), PackKit.edgeMask(EnumSet.of(FR, FL, BR, BL)),
            Edge.values());
    checkSize(pack, Coder.ordered.size(12, 2));
    checkFirst(pack, ". . . . . . . . . . 0 1");
    checkCycle(pack, new int[] { 7, 8, 9, 10 }, ". . . . . . . . . 0 . 1");
    checkStart(pack, ". . . . . . . . 0 1 . .");
  }

  @Test
  public void up_edge_position_ordered() {
    final EnumSet<Edge> edges = EnumSet.of(UB, UL, DF, DR, DB, FR, FL);
    final Pack<Edge> pack =
        new PackPositionOrdered<Edge>(PackKit.edgeMask(edges), PackKit.edgeMask(EnumSet.of(UF, UR, UB, UL)),
            Edge.values());
    checkSize(pack, Coder.ordered.size(12, 2));
    checkFirst(pack, ". . . . . . . . . . 0 1");
    checkSwap(pack, 1, 11, ". 1 . . . . . . . . 0 .");
    checkSwap(pack, 3, 10, ". 1 . 0 . . . . . . . .");
  }

  @Test
  public void down_edge_position_ordered() {
    final EnumSet<Edge> edges = EnumSet.of(UB, UL, DF, DR, DB, FR, FL);
    final Pack<Edge> pack =
        new PackPositionOrdered<Edge>(PackKit.edgeMask(edges), PackKit.edgeMask(EnumSet.of(DF, DR, DB, DL)),
            Edge.values());
    checkSize(pack, Coder.ordered.size(12, 3));
    checkFirst(pack, ". . . . . . . . . 0 1 2");
    checkSwap(pack, 0, 11, "2 . . . . . . . . 0 1 .");
    checkSwap(pack, 2, 10, "2 . 1 . . . . . . 0 . .");
    checkSwap(pack, 4, 9, "2 . 1 . 0 . . . . . . .");
  }

  @Test
  public void up_edge_down_edge_combine_to_o_edge_in_phase_B() {
    final EnumSet<Edge> edges = EnumSet.of(UB, UL, DF, DR, DB, FR, FL);
    final Pack<Edge> packU =
        new PackPositionOrdered<Edge>(PackKit.edgeMask(edges), PackKit.edgeMask(EnumSet.of(UF, UR, UB, UL)),
            Edge.values());
    packU.unpack(0);
    packU.swap(1, 11);
    packU.swap(3, 10);
    final Pack<Edge> packD =
        new PackPositionOrdered<Edge>(PackKit.edgeMask(edges), PackKit.edgeMask(EnumSet.of(DF, DR, DB, DL)),
            Edge.values());
    packD.unpack(0);
    packD.swap(0, 11);
    packD.swap(2, 10);
    packD.swap(4, 9);
    final EnumSet<Edge> edgesB = EnumSet.of(UB, UL, DF, DR, DB);
    final Pack<Edge> packB =
        new PackPositionOrdered<Edge>(PackKit.udEdgeMaskB(edgesB), EnumSet.of(UF, UR, UB, UL, DF, DR, DB, DL).toArray(
            new Edge[0]));
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
    assertEquals(". . . . . . . . 0 1 2 3",
        Coder.ordered.toString(PackKit.fillIndices(Edge.values(), EnumSet.of(FR, FL, BR, BL).toArray(new Edge[0]))));
    assertEquals("0 1 2 3 . . . . . . . .",
        Coder.ordered.toString(PackKit.fillIndices(Edge.values(), EnumSet.of(UF, UR, UB, UL).toArray(new Edge[0]))));
    assertEquals(". . . . 0 1 2 3 . . . .",
        Coder.ordered.toString(PackKit.fillIndices(Edge.values(), EnumSet.of(DF, DR, DB, DL).toArray(new Edge[0]))));
  }
}
