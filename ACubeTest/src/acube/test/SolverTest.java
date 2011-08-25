package acube.test;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import acube.Corner;
import acube.CubeState;
import acube.Edge;
import acube.Options;
import acube.Reporter;
import acube.Solver;
import acube.transform.Transform;

public class SolverTest {
  @Test
  public void solve_solved() {
    final List<String> found = new ArrayList<String>();
    final Options options = new Options();
    final Reporter reporter = new Reporter() {
      @Override
      public void sequenceFound(final String s) {
        found.add(s);
        System.out.println(s);
      }

      @Override
      public void problemOccured(final String s) {}

      @Override
      public void depthChanged(final int depth) {
        System.out.println("depth " + depth);
      }

      @Override
      public void onePhaseStatistics(final long checks, final long hits) {}

      @Override
      public void twoPhaseStatistics(final long checksA, final long hitsA, final long checksB, final long hitsB) {}
    };
    final CubeState state = new CubeState(Corner.values(), Edge.values(), new int[8], new int[12]);
    state.prepareTables(options);
    final Transform t = state.transform;
    state.setCornerTwist(t.twist.start(0));
    state.setCornerPos(t.cornerPos.start(0));
    state.setEdgeFlip(t.edgeFlip.start(0));
    state.setEdgePos(t.mEdgePos.start(0), t.uEdgePos.start(0), t.dEdgePos.start(0));
    final Solver solver = new Solver(options, reporter);
    solver.solve(state);
    assertEquals(1, found.size());
    assertEquals(". (0q, 0f, 0s, 0sq)", found.get(0));
    found.clear();
    state.setEdgeFlip(t.edgeFlip.stateSize() - 1);
    solver.solve(state);
  }
}
