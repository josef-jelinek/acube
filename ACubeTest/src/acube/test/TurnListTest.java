package acube.test;

import static acube.Metric.FACE;
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
import acube.Turn;
import acube.TurnList;

public final class TurnListTest {
  @Test
  public void two_allowed_faces_filters_duplicities() {
    final TurnList tl = new TurnList(EnumSet.of(U1, U2, U3, R1, R2, R3), FACE, TurnList.Phase.A);
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
    final TurnList tl = new TurnList(Turn.valueSet, FACE, TurnList.Phase.A);
    assertTrue(-1 != tl.getNextState(tl.getNextState(0, U1), R2));
    assertTrue(-1 == tl.getNextState(tl.getNextState(0, U1), U2));
    assertTrue(-1 == tl.getNextState(tl.getNextState(tl.getNextState(0, U1), R2), R1));
    assertTrue(-1 != tl.getNextState(tl.getNextState(tl.getNextState(0, U1), R2), U1));
  }

  @Test
  public void cube_rotations_change_filters() {
    final TurnList tl = new TurnList(Turn.valueSet, FACE, TurnList.Phase.A);
    assertTrue(-1 != tl.getNextState(tl.getNextState(0, U1), u1));
    assertTrue(-1 == tl.getNextState(tl.getNextState(0, U1), u3));
    assertTrue(-1 == tl.getNextState(tl.getNextState(0, u1), u2));
    assertTrue(-1 == tl.getNextState(tl.getNextState(0, r2), M2));
  }

  @Test
  public void including_double_layer_turns() {
    final TurnList tl =
        new TurnList(EnumSet.of(U1, U2, U3, R1, R2, R3, M1, M2, M3, r1, r2, r3), FACE, TurnList.Phase.A);
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
