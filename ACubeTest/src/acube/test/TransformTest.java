package acube.test;

import static acube.Corner.DBR;
import static acube.Corner.DFL;
import static acube.Corner.DLB;
import static acube.Corner.DRF;
import static acube.Corner.UBL;
import static acube.Corner.UFR;
import static acube.Corner.ULF;
import static acube.Corner.URB;
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
import static acube.Turn.B2;
import static acube.Turn.D1;
import static acube.Turn.D2;
import static acube.Turn.D3;
import static acube.Turn.E1;
import static acube.Turn.E2;
import static acube.Turn.E3;
import static acube.Turn.F2;
import static acube.Turn.L2;
import static acube.Turn.M2;
import static acube.Turn.R2;
import static acube.Turn.S2;
import static acube.Turn.U1;
import static acube.Turn.U2;
import static acube.Turn.U3;
import static acube.Turn.b2;
import static acube.Turn.d1;
import static acube.Turn.d2;
import static acube.Turn.d3;
import static acube.Turn.f2;
import static acube.Turn.l2;
import static acube.Turn.r2;
import static acube.Turn.u1;
import static acube.Turn.u2;
import static acube.Turn.u3;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import acube.Corner;
import acube.Edge;
import acube.Turn;
import acube.TurnB;
import acube.transform.MoveTableComposed;
import acube.transform.Transform;
import acube.transform.TransformB;
import acube.transform.TurnTable;

public final class TransformTest {
  private final Corner[] cornerMask = { UFR, URB, UBL, ULF };
  private final Edge[] edgeMask = { UF, UR, UB, UL, DF, DR, FR, FL };
  private final Corner[] cornerTwistMask = { ULF, DRF, DFL, DLB, DBR };
  private final Edge[] edgeFlipMask = { UL, DF, DR, DB, DL, FR, FL };

  @Test
  public void tables_lengths_matches_combinatorics() {
    final Transform t = new Transform(cornerMask, edgeMask, cornerTwistMask, edgeFlipMask, Turn.values());
    final TransformB tB = new TransformB(cornerMask, edgeMask, Turn.values());
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

  private void checkTable(final TurnTable table, final int states) {
    assertEquals(table.stateSize(), states);
    // reachability test (depth-first maze walkthrough)
    final Turn[] turns = table.turns();
    final boolean[] usedStates = new boolean[states];
    final int[] previousStates = new int[states];
    final int[] nextTurnIndices = new int[states];
    int currentState = goFresh(0, -1, previousStates, nextTurnIndices, usedStates);
    while (currentState >= 0) {
      while (nextTurnIndices[currentState] < turns.length) {
        final int newState = table.turn(turns[nextTurnIndices[currentState]++], currentState);
        if (!usedStates[newState])
          currentState = goFresh(newState, currentState, previousStates, nextTurnIndices, usedStates);
      }
      currentState = previousStates[currentState];
    }
    for (int i = 0; i < states; i++)
      assertTrue("unreachable states", usedStates[i]);
  }

  private static int goFresh(final int newState, final int currentState, final int[] previousStates,
      final int[] nextTurnIndices, final boolean[] usedStates) {
    previousStates[newState] = currentState;
    usedStates[newState] = true;
    nextTurnIndices[newState] = 0;
    return newState;
  }

  @Test(expected = IllegalArgumentException.class)
  public void composing_incompatible_tables_throws_excepion() {
    final Transform t = new Transform(cornerMask, edgeMask, cornerTwistMask, edgeFlipMask, Turn.values());
    final TransformB tB = new TransformB(cornerMask, edgeMask, Turn.values());
    assertTrue(null != new MoveTableComposed(t.dEdgePosition, tB.mEdgePosition));
  }

  @Test
  public void table_composed_move() {
    final Transform t = new Transform(cornerMask, edgeMask, cornerTwistMask, edgeFlipMask, Turn.values());
    final MoveTableComposed move = new MoveTableComposed(t.mEdgePositionSet, t.dEdgePosition);
    assertEquals(move.stateSize(), t.mEdgePositionSet.stateSize() * t.dEdgePosition.stateSize());
    assertArrayEquals(move.turns(), t.mEdgePositionSet.turns());
    assertArrayEquals(move.turns(), t.dEdgePosition.turns());
    assertEquals(move.startSize(), t.mEdgePositionSet.startSize() * t.dEdgePosition.startSize());
    final Turn[] turns = move.turns();
    for (int i = 0; i < move.stateSize(); i++)
      for (final Turn turn : turns) {
        assertEquals(move.turn(turn, i) / t.dEdgePosition.stateSize(),
            t.mEdgePositionSet.turn(turn, i / t.dEdgePosition.stateSize()));
        assertEquals(move.turn(turn, i) % t.dEdgePosition.stateSize(),
            t.dEdgePosition.turn(turn, i % t.dEdgePosition.stateSize()));
      }
    for (int i = 0; i < move.startSize(); i++) {
      assertEquals(move.start(i) / t.dEdgePosition.stateSize(),
          t.mEdgePositionSet.start(i / t.dEdgePosition.startSize()));
      assertEquals(move.start(i) % t.dEdgePosition.stateSize(), t.dEdgePosition.start(i % t.dEdgePosition.startSize()));
    }
  }

  @Test
  public void tables_for_phase_B() {
    final Transform t = new Transform(cornerMask, edgeMask, cornerTwistMask, edgeFlipMask, Turn.values());
    final TransformB tB = new TransformB(cornerMask, edgeMask, Turn.values());
    assertEquals(tB.cornerPosition.stateSize(), t.cornerPosition.stateSize());
    assertEquals(t.cornerPosition.turns().length, Turn.size());
    assertEquals(tB.cornerPosition.turns().length, TurnB.size());
    assertEquals(tB.cornerPosition.startSize(), t.cornerPosition.startSize());
    for (final Turn turn : tB.cornerPosition.turns())
      for (int i = 0; i < tB.cornerPosition.stateSize(); i++)
        assertEquals(t.cornerPosition.turn(turn, i), tB.cornerPosition.turn(turn, i));
    for (int i = 0; i < tB.cornerPosition.startSize(); i++)
      assertEquals(tB.cornerPosition.start(i), t.cornerPosition.start(i));
  }

  private static final Turn[][] revB = {
      { U1, U3 }, { U2, U2 }, { D1, D3 }, { D2, D2 }, { F2, F2 }, { B2, B2 }, { L2, L2 }, { R2, R2 }, { E1, E3 },
      { E2, E2 }, { S2, S2 }, { M2, M2 }, { u1, u3 }, { u2, u2 }, { d1, d3 }, { d2, d2 }, { f2, f2 }, { b2, b2 },
      { l2, l2 }, { r2, r2 } };

  @Test
  public void back_and_forth_turn_results_in_identity_in_phase_B() {
    final TransformB tB = new TransformB(cornerMask, edgeMask, Turn.values());
    checkBackForthTurns(tB.mEdgePosition);
    checkBackForthTurns(tB.oEdgePosition);
    checkBackForthTurns(tB.cornerPosition);
  }

  private void checkBackForthTurns(final TurnTable tab) {
    for (int i = 0; i < tab.stateSize(); i++)
      for (final Turn[] element : revB) {
        assertEquals(i, tab.turn(element[0], tab.turn(element[1], i)));
        assertEquals(i, tab.turn(element[1], tab.turn(element[0], i)));
      }
  }

  @Test
  public void conversion_to_phase_B() {
    final Transform t = new Transform(cornerMask, edgeMask, cornerTwistMask, edgeFlipMask, Turn.values());
    final TransformB tB = new TransformB(cornerMask, edgeMask, Turn.values());
    final TurnTable mep = t.mEdgePosition;
    final TurnTable uep = t.uEdgePosition;
    final TurnTable dep = t.dEdgePosition;
    m_in_and_out_B(tB, mep);
    u_in_and_out_B(tB, uep);
    d_in_and_out_B(tB, dep);
    m_to_B_sizes(tB, mep);
    u_to_B_sizes(tB, uep);
    d_to_B_sizes(tB, dep);
    assertEquals(0, tB.convertToMEdgePosition(0));
    assertEquals(1, tB.convertToMEdgePosition(1));
    assertEquals(4 * 3 - 1, tB.convertToMEdgePosition(4 * 3 - 1));
    assertEquals(-1, tB.convertToMEdgePosition(4 * 3));
    assertEquals(-1, tB.convertToMEdgePosition(12 * 11 - 1));
    assertEquals(-1, tB.convertToOEdgePosition(0, 0));
    assertEquals(0, tB.convertToOEdgePosition(2514, 7 * 5 * 2));
    assertEquals(8 * 7 * 6 * 5 * 4 * 3 - 1, tB.convertToOEdgePosition(11879, 55));
    assertEquals(8 * 7 * 6 * 5 * 4 * 3 - 1 - 1, tB.convertToOEdgePosition(11879, 54));
    assertEquals(8 * 7 * 6 * 5 * 4 * 3 - 1 - 6, tB.convertToOEdgePosition(11878, 55));
  }

  private void m_in_and_out_B(final TransformB tB, final TurnTable mep) {
    assertTrue(tB.isMEdgePositionInB(mep.start(0)));
    assertFalse(tB.isMEdgePositionInB(mep.turn(Turn.R1, mep.start(0))));
    assertTrue(tB.isMEdgePositionInB(mep.turn(Turn.R2, mep.start(0))));
    assertTrue(tB.isMEdgePositionInB(mep.turn(Turn.U1, mep.start(0))));
  }

  private void u_in_and_out_B(final TransformB tB, final TurnTable uep) {
    assertTrue(tB.isUEdgePositionInB(uep.start(0)));
    assertFalse(tB.isUEdgePositionInB(uep.turn(Turn.R1, uep.start(0))));
    assertTrue(tB.isUEdgePositionInB(uep.turn(Turn.R2, uep.start(0))));
    assertTrue(tB.isUEdgePositionInB(uep.turn(Turn.U1, uep.start(0))));
  }

  private void d_in_and_out_B(final TransformB tB, final TurnTable dep) {
    assertTrue(tB.isDEdgePositionInB(dep.start(0)));
    assertFalse(tB.isDEdgePositionInB(dep.turn(Turn.R1, dep.start(0))));
    assertTrue(tB.isDEdgePositionInB(dep.turn(Turn.R2, dep.start(0))));
    assertTrue(tB.isDEdgePositionInB(dep.turn(Turn.U1, dep.start(0))));
  }

  private void m_to_B_sizes(final TransformB tB, final TurnTable mep) {
    int mInB = 0;
    for (int i = 0; i < mep.stateSize(); i++)
      if (tB.isMEdgePositionInB(i))
        mInB++;
    assertEquals(12 * 11, mep.stateSize());
    assertEquals(4 * 3, mInB);
  }

  private void u_to_B_sizes(final TransformB tB, final TurnTable uep) {
    int uInB = 0;
    for (int i = 0; i < uep.stateSize(); i++)
      if (tB.isUEdgePositionInB(i))
        uInB++;
    assertEquals(12 * 11 * 10 * 9, uep.stateSize());
    assertEquals(8 * 7 * 6 * 5, uInB);
  }

  private void d_to_B_sizes(final TransformB tB, final TurnTable dep) {
    int dInB = 0;
    for (int i = 0; i < dep.stateSize(); i++)
      if (tB.isDEdgePositionInB(i))
        dInB++;
    assertEquals(12 * 11, dep.stateSize());
    assertEquals(8 * 7, dInB);
  }
}
