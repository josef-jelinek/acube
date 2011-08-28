package acube.test;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import acube.Corner;
import acube.CubeState;
import acube.Edge;
import acube.Options;
import acube.Solver;
import acube.console.ConsoleReporter;
import acube.transform.Transform;

public class SolverTest {
  @Test
  public void solve_solved() {
    final Options options = new Options();
    final CubeState state = new CubeState(Corner.values(), Edge.values(), new int[8], new int[12]);
    final ConsoleReporter reporter = new ConsoleReporter();
    state.prepareTables(options, reporter);
    final Transform t = state.transform;
    state.setCornerTwist(t.twist.start(0));
    state.setCornerPos(t.cornerPos.start(0));
    state.setEdgeFlip(t.flip.start(0));
    state.setEdgePos(t.mEdgePos.start(0), t.uEdgePos.start(0), t.dEdgePos.start(0));
    final Solver solver = new Solver(options, reporter);
    solver.solve(state);
    assertEquals(1, reporter.getSequences().size());
    assertEquals("", reporter.getSequences().get(0));
    reporter.reset();
    state.setEdgeFlip(t.flip.stateSize() - 1);
    solver.solve(state);
    assertEquals(1, reporter.getSequences().size());
    assertEquals("S U F2 U2 M D S . U' B2 R2 D' M2 U' L2 U' B2 R2 F2", reporter.getSequences().get(0));
  }
}
