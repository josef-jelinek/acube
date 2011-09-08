package acube.test;

import static acube.Corner.UFR;
import static acube.Edge.UR;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import acube.Corner;
import acube.Edge;
import acube.Tools;
import acube.format.CycleParser;
import acube.format.ReidParser;

/** @author Stefan Pochmann (original cycle notation parser), Josef Jelinek */
public final class CubeTextParseTest {
  private static final String ReidSource = "UF UR UB UL DF DR DB DL FR FL BR BL UFR URB UBL ULF DRF DFL DLB DBR";
  private static final String ReidResult = "RD DL BU LU UF DB FL RF BR BL DF UR DBR UBL FRU FDR FUL LDF URB BDL";

  // Test a full scramble, the one from Leyan Lo's blindfold world record:
  // R2 B2 F2 U F' D2 F' L2 F2 R' U B2 D2 L' R B' D R F R U2 F U' L2 F'
  @Test
  public void full_scramble() {
    compare(ReidResult, " (  UR  BL FL DB DR,FU, FD RB RF DL ) UL+UB+ (UBL URB  DLB BRD FRU)(RFD FUL)RFD+, DFL-");
  }

  // Test each edge-case of my blindcubing method (i.e. swap UFR with URB and UR with any other edge.
  @Test
  public void cycle_blindfold_edges() {
    for (int i = 0; i < 12; i++)
      if (i != 1)
        for (int j = 0; j < 2; j++) {
          final String[] reid = ReidSource.split(" ");
          Tools.swap(reid, 12, 13);
          final String s = reid[1];
          reid[1] = Tools.rotate(reid[i], j);
          reid[i] = Tools.rotate(s, (2 - j) % 2);
          final String stateReid = Tools.arrayToString(reid);
          final String stateCycle = "(UFR URB) (" + s + " " + reid[1] + ")";
          compare(stateReid, stateCycle);
        }
  }

  // Test each corner-case of my blindcubing method (i.e. swap UL with UB and UBL with any other corner.
  @Test
  public void cycle_blindfold_corners() {
    for (int i = 0; i < 8; i++)
      if (i != 2)
        for (int j = 0; j < 3; j++) {
          final String[] reid = ReidSource.split(" ");
          Tools.swap(reid, 2, 3);
          final String s = reid[12 + 2];
          reid[12 + 2] = Tools.rotate(reid[12 + i], j);
          reid[12 + i] = Tools.rotate(s, (3 - j) % 3);
          final String stateReid = Tools.arrayToString(reid);
          final String stateCycle = "(UL UB) (" + s + " " + reid[12 + 2] + ")";
          compare(stateReid, stateCycle);
        }
  }

  @Test
  public void individual_piece_orientations() {
    for (int i = 0; i < 20; i++) {
      final String[] reid = ReidSource.split(" ");
      for (int j = 1; j < reid[i].length(); j++) {
        reid[i] = Tools.rotate(reid[i], 1);
        final String stateReid = Tools.arrayToString(reid);
        final String stateCycle = reid[i] + (j == 1 ? "+" : "-");
        compare(stateReid, stateCycle);
      }
    }
  }

  private void compare(final String stateReid, final String stateCycle) {
    assertEquals(stateReid, CycleParser.parse(stateCycle).reidString());
  }

  @Test
  public void corners_edges_string_for_incomplete() {
    assertEquals("@UR", Edge.name(UR, -1));
    assertEquals("UR", Edge.name(UR, 0));
    assertEquals("RU", Edge.name(UR, 1));
    assertEquals("@?", Edge.name(null, -1));
    assertEquals("?", Edge.name(null, 0));
    assertEquals("-?", Edge.name(null, 1));
    assertEquals("@UFR", Corner.name(UFR, -1));
    assertEquals("UFR", Corner.name(UFR, 0));
    assertEquals("FRU", Corner.name(UFR, 1));
    assertEquals("RUF", Corner.name(UFR, 2));
    assertEquals("@?", Corner.name(null, -1));
    assertEquals("?", Corner.name(null, 0));
    assertEquals("+?", Corner.name(null, 1));
    assertEquals("-?", Corner.name(null, 2));
  }

  @Test
  public void ignored_positions() {
    final String s1 = "UF ? UB ? DF ? DB DL FR FL BR BL UFR URB UBL ULF DRF ? DLB ?";
    assertEquals(s1, CycleParser.parse("[UL DFL DR UR RBD]").reidString());
    final String s2 = "UF -? -? ? DF DR DB DL FR FL BR BL UFR URB UBL ULF DRF DFL -? +?";
    assertEquals(s2, CycleParser.parse("DLB- DBR+[DLB DBR] UR- UB- UL [UR UB UL]").reidString());
  }

  @Test
  public void ignored_orientations() {
    final String s1 = "@? ? @UB UL DF DR DB DL FR FL BR BL @? ? @UBL ULF DRF DFL DLB DBR";
    assertEquals(s1, CycleParser.parse("UF? UB? UFR? UBL? [UF UR*]").reidString());
  }

  @Test
  public void reid_notation() {
    final String s1 = "@? ? @UB UL DF DR DB DL FR FL BR BL @? ? @UBL ULF DRF DFL DLB DBR";
    assertEquals(s1, ReidParser.parse(s1).reidString());
    final String s2 = "UF -? -? ? DF DR DB DL FR FL BR BL UFR URB UBL ULF DRF DFL -? +?";
    assertEquals(s2, ReidParser.parse(s2).reidString());
  }
}
