package acube.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import acube.Corner;
import acube.Tools;
import acube.format.CycleParser;

/** @author Stefan Pochmann */
public final class CycleParseTest {
  private static final String ReidSource = "UF UR UB UL DF DR DB DL FR FL BR BL UFR URB UBL ULF DRF DFL DLB DBR";
  private static final String ReidResult = "RD DL BU LU UF DB FL RF BR BL DF UR DBR UBL FRU FDR FUL LDF URB BDL";

  // Test a full scramble, the one from Leyan Lo's blindfold world record:
  // R2 B2 F2 U F' D2 F' L2 F2 R' U B2 D2 L' R B' D R F R U2 F U' L2 F'
  @Test
  public void test_full_scramble() {
    compare(ReidResult, " (  UR  BL FL DB DR,FU, FD RB RF DL ) UL+UB+ (UBL URB  DLB BRD FRU)(RFD FUL)RFD+, DFL-");
  }

  // Test each edge-case of my blindcubing method (i.e. swap UFR with URB and UR with any other edge.
  @Test
  public void test_cycle_blindfold_edges() {
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
  public void test_cycle_blindfold_corners() {
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
  public void test_individual_piece_orientations() {
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

  void compare(final String stateReid, final String stateCycle) {
    assertEquals(CycleParser.parse(stateCycle).toReid(), stateReid);
  }

  @Test
  public void test_enum_values_array_is_defensive() {
    final Corner[] values = Corner.values();
    assertTrue(values[0] == Corner.values()[0]);
    values[0] = values[1];
    assertFalse(values[0] == Corner.values()[0]);
  }
}
