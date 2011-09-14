package acube.test;

import static acube.Metric.FACE;
import static acube.Turn.U2;
import static acube.Turn.r2;
import static org.junit.Assert.assertEquals;
import java.util.EnumSet;
import org.junit.Test;
import acube.Corner;
import acube.CubeState;
import acube.Edge;
import acube.Turn;
import acube.console.ConsoleReporter;
import acube.format.CycleParser;
import acube.format.ReidParser;
import acube.format.TurnParser;

public class SolverTest {
  @Test
  public void solve_solved() {
    final CubeState state = new CubeState(Corner.values(), Edge.values(), new int[8], new int[12]);
    final ConsoleReporter reporter = new ConsoleReporter();
    state.solve(FACE, Turn.valueSet, 20, false, reporter);
    assertEquals(1, reporter.getSequences().size());
    assertEquals("", reporter.getSequences().get(0));
  }

  @Test
  public void solve_T() {
    final CubeState state = CycleParser.parse("(UL UR) (URF UBR)");
    final ConsoleReporter reporter = new ConsoleReporter();
    state.solve(FACE, TurnParser.parse("U U' D D' F2 B2 R2"), 11, false, reporter);
    assertEquals(1, reporter.getSequences().size());
    assertEquals(". U F2 U' F2 D R2 B2 U B2 D' R2", reporter.getSequences().get(0));
  }

  @Test
  public void solve_with_wide_turns() {
    final CubeState state = CycleParser.parse("(UF UB) (DF DB)");
    final ConsoleReporter reporter = new ConsoleReporter();
    state.solve(FACE, EnumSet.of(U2, r2), 12, false, reporter);
    assertEquals(1, reporter.getSequences().size());
    assertEquals(". U2 r2 U2 r2 U2 r2 U2 r2 U2 r2 U2 r2", reporter.getSequences().get(0));
  }

  @Test
  public void solve_flip_with_many_ignored() {
    final CubeState state = ReidParser.parse("@? @? @? @? DF DR DB DL RF @? @? ? UFR URB UBL ULF DRF DFL DLB DBR");
    final ConsoleReporter reporter = new ConsoleReporter();
    state.solve(FACE, Turn.valueSet, 12, false, reporter);
    assertEquals(1, reporter.getSequences().size());
    assertEquals("B2 D2 F U' D R . U D B2 U2", reporter.getSequences().get(0));
  }
}
