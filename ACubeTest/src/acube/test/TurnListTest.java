package acube.test;

import static acube.Turn.M1;
import static acube.Turn.M2;
import static acube.Turn.M3;
import static acube.Turn.R1;
import static acube.Turn.R2;
import static acube.Turn.R3;
import static acube.Turn.U1;
import static acube.Turn.U2;
import static acube.Turn.U3;
import static acube.Turn.r1;
import static acube.Turn.r2;
import static acube.Turn.r3;
import static acube.Turn.u1;
import static acube.Turn.u2;
import static acube.Turn.u3;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.EnumSet;
import org.junit.Test;
import acube.Reporter;
import acube.Turn;
import acube.TurnList;
import acube.console.ConsoleReporter;
import acube.transform.Transform;

public final class TurnListTest {
  @Test
  public void one_allowed_turn_allows_one_turn_3_times() {
    final Reporter reporter = new ConsoleReporter();
    final TurnList tl = new TurnList(new Transform(reporter), EnumSet.of(U1), reporter);
    for (final Turn t : Turn.values())
      assertEquals(t == U1 ? 1 : -1, tl.getNextState(0, t));
    for (final Turn t : Turn.values())
      assertEquals(t == U1 ? 2 : -1, tl.getNextState(1, t));
    for (final Turn t : Turn.values())
      assertEquals(t == U1 ? 3 : -1, tl.getNextState(2, t));
    for (final Turn t : Turn.values())
      assertEquals(-1, tl.getNextState(3, t));
    assertEquals(1, tl.getAvailableTurns(0).length);
    assertEquals(U1, tl.getAvailableTurns(0)[0]);
    assertEquals(1, tl.getAvailableTurns(1).length);
    assertEquals(U1, tl.getAvailableTurns(1)[0]);
    assertEquals(1, tl.getAvailableTurns(2).length);
    assertEquals(U1, tl.getAvailableTurns(2)[0]);
    assertEquals(0, tl.getAvailableTurns(3).length);
  }

  @Test
  public void two_allowed_faces_filters_duplicities() {
    final Reporter reporter = new ConsoleReporter();
    final TurnList tl = new TurnList(new Transform(reporter), EnumSet.of(U1, U2, U3, R1, R2, R3), reporter);
    assertTrue(-1 != tl.getNextState(tl.getNextState(0, U1), R2));
    assertTrue(-1 == tl.getNextState(tl.getNextState(0, U1), U2));
    assertTrue(-1 == tl.getNextState(tl.getNextState(tl.getNextState(0, U1), R2), R1));
    assertTrue(-1 != tl.getNextState(tl.getNextState(tl.getNextState(0, U1), R2), U1));
    assertEquals(6, tl.getAvailableTurns(0).length);
    assertEquals(3, tl.getAvailableTurns(tl.getNextState(0, U1)).length);
    assertEquals(R1, tl.getAvailableTurns(tl.getNextState(0, U1))[0]);
    assertEquals(R2, tl.getAvailableTurns(tl.getNextState(0, U1))[1]);
    assertEquals(R3, tl.getAvailableTurns(tl.getNextState(0, U1))[2]);
    assertEquals(3, tl.getAvailableTurns(tl.getNextState(tl.getNextState(0, U1), R2)).length);
  }

  @Test
  public void all_allowed_turns_filters_duplicities() {
    final Reporter reporter = new ConsoleReporter();
    final TurnList tl = new TurnList(new Transform(reporter), Turn.valueSet, reporter);
    for (final Turn t : Turn.values())
      assertTrue(-1 != tl.getNextState(0, t));
    assertTrue(-1 != tl.getNextState(tl.getNextState(0, U1), R2));
    assertTrue(-1 == tl.getNextState(tl.getNextState(0, U1), U2));
    assertTrue(-1 == tl.getNextState(tl.getNextState(tl.getNextState(0, U1), R2), R1));
    assertTrue(-1 != tl.getNextState(tl.getNextState(tl.getNextState(0, U1), R2), U1));
  }

  @Test
  public void cube_rotations_change_filters() {
    final Reporter reporter = new ConsoleReporter();
    final TurnList tl = new TurnList(new Transform(reporter), Turn.valueSet, reporter);
    assertTrue(-1 != tl.getNextState(tl.getNextState(0, U1), u1));
    assertTrue(-1 == tl.getNextState(tl.getNextState(0, U1), u3));
    assertTrue(-1 == tl.getNextState(tl.getNextState(0, u1), u2));
    assertTrue(-1 == tl.getNextState(tl.getNextState(0, r2), M2));
  }

  @Test
  public void including_double_layer_turns() {
    final Reporter reporter = new ConsoleReporter();
    final Transform t = new Transform(reporter);
    final TurnList tl = new TurnList(t, EnumSet.of(U1, U2, U3, R1, R2, R3, M1, M2, M3, r1, r2, r3), reporter);
    assertTrue(-1 != tl.getNextState(tl.getNextState(0, R1), r1));
    assertTrue(-1 == tl.getNextState(tl.getNextState(0, R1), r3));
    assertTrue(-1 == tl.getNextState(tl.getNextState(0, r1), r2));
    assertTrue(-1 == tl.getNextState(tl.getNextState(0, r2), M2));
    assertTrue(-1 == tl.getNextState(tl.getNextState(0, r2), M1));
    assertTrue(-1 != tl.getNextState(tl.getNextState(0, R1), r1));
    assertTrue(-1 == tl.getNextState(tl.getNextState(0, R2), M3));
    assertTrue(-1 == tl.getNextState(tl.getNextState(0, R2), M2));
  }
}
