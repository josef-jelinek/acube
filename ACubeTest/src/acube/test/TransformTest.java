package acube.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import acube.Corner;
import acube.Edge;
import acube.Turn;
import acube.TurnB;
import acube.transform.ITableMove;
import acube.transform.MoveTableComposed;
import acube.transform.Transform;
import acube.transform.TransformB;

public final class TransformTest {
  private Transform t;
  private TransformB tB;
  private Corner[] cornerMask = { Corner.UFR, Corner.URB, Corner.UBL, Corner.ULF };
  private Edge[] edgeMask = {
      Edge.UF, Edge.UR, Edge.UB, Edge.UL, Edge.DF, Edge.DR, Edge.FR, Edge.FL };
  private Corner[] cornerTwistMask = { Corner.ULF, Corner.DRF, Corner.DFL, Corner.DLB, Corner.DBR };
  private Edge[] edgeFlipMask = { Edge.UL, Edge.DF, Edge.DR, Edge.DB, Edge.DL, Edge.FR, Edge.FL };

  @Before
  public void setUp() {
    t = Transform.instance(cornerMask, edgeMask, cornerTwistMask, edgeFlipMask, Turn.values());
    tB = TransformB.instance(cornerMask, edgeMask, Turn.values());
  }

  private void checkTable(ITableMove table, int states) {
    assertEquals(table.stateSize(), states);
    // reachability test (depth-first maze walkthrough)
    Turn[] turns = table.turns();
    boolean[] usedStates = new boolean[states];
    int[] previousStates = new int[states];
    int[] nextTurnIndices = new int[states];
    int currentState = goFresh(0, -1, previousStates, nextTurnIndices, usedStates);
    while (currentState >= 0) {
      while (nextTurnIndices[currentState] < turns.length) {
        int newState = table.turn(turns[nextTurnIndices[currentState]++], currentState);
        if (!usedStates[newState])
          currentState =
              goFresh(newState, currentState, previousStates, nextTurnIndices, usedStates);
      }
      currentState = previousStates[currentState];
    }
    for (int i = 0; i < states; i++)
      assertTrue("unreachable states", usedStates[i]);
  }

  private static int goFresh(int newState, int currentState, int[] previousStates,
      int[] nextTurnIndices, boolean[] usedStates) {
    previousStates[newState] = currentState;
    usedStates[newState] = true;
    nextTurnIndices[newState] = 0;
    return newState;
  }

  @Test
  public void test_tables() {
    checkTable(t.cornerPosition, 8 * 7 * 6 * 5);
    checkTable(t.cornerTwist, 2187);
    checkTable(t.edgeFlip, 2048);
    checkTable(t.mEdgePositionSet, 12 * 11 / 2);
    checkTable(t.mEdgePosition, 12 * 11);
    checkTable(t.uEdgePosition, 12 * 11 * 10 * 9);
    checkTable(t.dEdgePosition, 12 * 11);
    checkTable(tB.mEdgePosition, 4 * 3);
    checkTable(tB.oEdgePosition, 8 * 7 * 6 * 5 * 4 * 3);
  }

  @Test(expected = IllegalArgumentException.class)
  public void test_table_2_excepion() {
    new MoveTableComposed(t.dEdgePosition, tB.mEdgePosition);
  }

  @Test
  public void test_table_2() {
    MoveTableComposed move = new MoveTableComposed(t.mEdgePositionSet, t.dEdgePosition);
    assertEquals(move.stateSize(), t.mEdgePositionSet.stateSize() * t.dEdgePosition.stateSize());
    assertTrue(Arrays.equals(move.turns(), t.mEdgePositionSet.turns()));
    assertTrue(Arrays.equals(move.turns(), t.dEdgePosition.turns()));
    assertEquals(move.startSize(), t.mEdgePositionSet.startSize() * t.dEdgePosition.startSize());
    Turn[] turns = move.turns();
    for (int i = 0; i < move.stateSize(); i++) {
      for (int j = 0; j < turns.length; j++) {
        assertEquals(move.turn(turns[j], i) / t.dEdgePosition.stateSize(),
            t.mEdgePositionSet.turn(turns[j], i / t.dEdgePosition.stateSize()));
        assertEquals(move.turn(turns[j], i) % t.dEdgePosition.stateSize(),
            t.dEdgePosition.turn(turns[j], i % t.dEdgePosition.stateSize()));
      }
    }
    for (int i = 0; i < move.startSize(); i++) {
      assertEquals(move.start(i) / t.dEdgePosition.stateSize(),
          t.mEdgePositionSet.start(i / t.dEdgePosition.startSize()));
      assertEquals(move.start(i) % t.dEdgePosition.stateSize(),
          t.dEdgePosition.start(i % t.dEdgePosition.startSize()));
    }
  }

  @Test
  public void test_table_B() {
    assertEquals(tB.cornerPosition.stateSize(), t.cornerPosition.stateSize());
    assertEquals(t.cornerPosition.turns().length, Turn.size());
    assertEquals(tB.cornerPosition.turns().length, TurnB.size());
    assertEquals(tB.cornerPosition.startSize(), t.cornerPosition.startSize());
    for (Turn turn : tB.cornerPosition.turns())
      for (int i = 0; i < tB.cornerPosition.stateSize(); i++)
        assertEquals(t.cornerPosition.turn(turn, i), tB.cornerPosition.turn(turn, i));
    for (int i = 0; i < tB.cornerPosition.startSize(); i++)
      assertEquals(tB.cornerPosition.start(i), t.cornerPosition.start(i));
  }

  Turn[][] revB = {
      { Turn.U1, Turn.U3 }, { Turn.U2, Turn.U2 }, { Turn.D1, Turn.D3 }, { Turn.D2, Turn.D2 },
      { Turn.F2, Turn.F2 }, { Turn.B2, Turn.B2 }, { Turn.L2, Turn.L2 }, { Turn.R2, Turn.R2 },
      { Turn.E1, Turn.E3 }, { Turn.E2, Turn.E2 }, { Turn.S2, Turn.S2 }, { Turn.M2, Turn.M2 },
      { Turn.u1, Turn.u3 }, { Turn.u2, Turn.u2 }, { Turn.d1, Turn.d3 }, { Turn.d2, Turn.d2 },
      { Turn.f2, Turn.f2 }, { Turn.b2, Turn.b2 }, { Turn.l2, Turn.l2 }, { Turn.r2, Turn.r2 }, };

  @Test
  public void testReverseB() {
    for (int i = 0; i < tB.mEdgePosition.stateSize(); i++) {
      for (int j = 0; j < revB.length; j++) {
        assertEquals(i, tB.mEdgePosition.turn(revB[j][0], tB.mEdgePosition.turn(revB[j][1], i)));
        assertEquals(i, tB.mEdgePosition.turn(revB[j][1], tB.mEdgePosition.turn(revB[j][0], i)));
      }
    }
    for (int i = 0; i < tB.oEdgePosition.stateSize(); i++) {
      for (int j = 0; j < revB.length; j++) {
        assertEquals(i, tB.oEdgePosition.turn(revB[j][0], tB.oEdgePosition.turn(revB[j][1], i)));
        assertEquals(i, tB.oEdgePosition.turn(revB[j][1], tB.oEdgePosition.turn(revB[j][0], i)));
      }
    }
    for (int i = 0; i < tB.cornerPosition.stateSize(); i++) {
      for (int j = 0; j < revB.length; j++) {
        assertEquals(i, tB.cornerPosition.turn(revB[j][0], tB.cornerPosition.turn(revB[j][1], i)));
        assertEquals(i, tB.cornerPosition.turn(revB[j][1], tB.cornerPosition.turn(revB[j][0], i)));
      }
    }
  }
}
