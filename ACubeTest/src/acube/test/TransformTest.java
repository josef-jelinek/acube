package acube.test;

import static acube.Corner.UBL;
import static acube.Corner.UFR;
import static acube.Corner.ULF;
import static acube.Corner.URB;
import static acube.Edge.DF;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.EnumSet;
import org.junit.Test;
import acube.Corner;
import acube.Edge;
import acube.Turn;
import acube.console.ConsoleReporter;
import acube.transform.MoveTable2in1;
import acube.transform.Transform;
import acube.transform.TransformB;
import acube.transform.TurnTable;

public final class TransformTest {
  private final EnumSet<Corner> cornerMask = EnumSet.of(UFR, URB, UBL, ULF);
  private final EnumSet<Corner> twistMask = EnumSet.of(ULF);
  private final EnumSet<Edge> edgeMask = EnumSet.of(UF, UR, UB, UL, DF, DR, FR, FL);
  private final EnumSet<Edge> flipMask = EnumSet.of(UL, DF, DR, FR, FL);

  @Test
  public void tables_lengths_matches_combinatorics() {
    final Transform t = new Transform(cornerMask, edgeMask, twistMask, flipMask, 4, 2, new ConsoleReporter());
    final TransformB tB = new TransformB(cornerMask, edgeMask, new ConsoleReporter());
    final EnumSet<Turn> turns = Turn.valueSet;
    final EnumSet<Turn> turnsB = Turn.getValidB(turns);
    checkTable(t.cornerPosTable, turns, 8 * 7 * 6 * 5);
    checkTable(t.twistTable, turns, 8 * 7 * 6 / (3 * 2) * 3 * 3 * 3 * 3 * 3);
    checkTable(t.flipTable, turns, 12 * 11 * 10 * 9 * 8 / (5 * 4 * 3 * 2) * 128);
    checkTable(t.mEdgePosSetTable, turns, 12 * 11 / 2);
    checkTable(t.mEdgePosTable, turns, 12 * 11);
    checkTable(t.uEdgePosTable, turns, 12 * 11 * 10 * 9);
    checkTable(t.dEdgePosTable, turns, 12 * 11);
    checkTable(tB.mEdgePosTable, turnsB, 4 * 3);
    checkTable(tB.udEdgePosTable, turnsB, 8 * 7 * 6 * 5 * 4 * 3);
  }

  private void checkTable(final TurnTable table, final EnumSet<Turn> turns, final int states) {
    assertEquals(table.stateSize(), states);
    // reachability test (depth-first maze walkthrough)
    final boolean[] usedStates = new boolean[states];
    final int[] previousStates = new int[states];
    final int[] nextTurnIndices = new int[states];
    int currentState = goFresh(0, -1, previousStates, nextTurnIndices, usedStates);
    while (currentState >= 0) {
      while (nextTurnIndices[currentState] < Turn.values.length) {
        final Turn turn = Turn.values[nextTurnIndices[currentState]++];
        if (turns.contains(turn)) {
          final int newState = table.turn(turn, currentState);
          if (!usedStates[newState])
            currentState = goFresh(newState, currentState, previousStates, nextTurnIndices, usedStates);
        }
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

  @Test
  public void table_composed_move() {
    final Transform t = new Transform(cornerMask, edgeMask, twistMask, flipMask, 4, 2, new ConsoleReporter());
    final MoveTable2in1 move = new MoveTable2in1(t.mEdgePosSetTable, t.dEdgePosTable);
    assertEquals(move.stateSize(), t.mEdgePosSetTable.stateSize() * t.dEdgePosTable.stateSize());
    assertEquals(move.startSize(), t.mEdgePosSetTable.startSize() * t.dEdgePosTable.startSize());
    for (int i = 0; i < move.stateSize(); i++)
      for (final Turn turn : Turn.values) {
        assertEquals(move.turn(turn, i) / t.dEdgePosTable.stateSize(),
            t.mEdgePosSetTable.turn(turn, i / t.dEdgePosTable.stateSize()));
        assertEquals(move.turn(turn, i) % t.dEdgePosTable.stateSize(),
            t.dEdgePosTable.turn(turn, i % t.dEdgePosTable.stateSize()));
      }
    for (int i = 0; i < move.startSize(); i++) {
      assertEquals(move.start(i) / t.dEdgePosTable.stateSize(),
          t.mEdgePosSetTable.start(i / t.dEdgePosTable.startSize()));
      assertEquals(move.start(i) % t.dEdgePosTable.stateSize(), t.dEdgePosTable.start(i % t.dEdgePosTable.startSize()));
    }
  }

  @Test
  public void tables_for_phase_B() {
    final Transform t = new Transform(cornerMask, edgeMask, twistMask, flipMask, 4, 2, new ConsoleReporter());
    final TransformB tB = new TransformB(cornerMask, edgeMask, new ConsoleReporter());
    assertEquals(tB.cornerPosTable.stateSize(), t.cornerPosTable.stateSize());
    assertEquals(tB.cornerPosTable.startSize(), t.cornerPosTable.startSize());
    for (final Turn turn : Turn.values)
      if (turn.isB())
        for (int i = 0; i < tB.cornerPosTable.stateSize(); i++)
          assertEquals(t.cornerPosTable.turn(turn, i), tB.cornerPosTable.turn(turn, i));
    for (int i = 0; i < tB.cornerPosTable.startSize(); i++)
      assertEquals(tB.cornerPosTable.start(i), t.cornerPosTable.start(i));
  }

  private static final Turn[][] revB = {
      { U1, U3 }, { U2, U2 }, { D1, D3 }, { D2, D2 }, { F2, F2 }, { B2, B2 }, { L2, L2 }, { R2, R2 }, { E1, E3 },
      { E2, E2 }, { S2, S2 }, { M2, M2 }, { u1, u3 }, { u2, u2 }, { d1, d3 }, { d2, d2 }, { f2, f2 }, { b2, b2 },
      { l2, l2 }, { r2, r2 } };

  @Test
  public void back_and_forth_turn_results_in_identity_in_phase_B() {
    final TransformB tB = new TransformB(cornerMask, edgeMask, new ConsoleReporter());
    checkBackForthTurns(tB.mEdgePosTable);
    checkBackForthTurns(tB.udEdgePosTable);
    checkBackForthTurns(tB.cornerPosTable);
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
    final Transform t = new Transform(cornerMask, edgeMask, twistMask, flipMask, 4, 2, new ConsoleReporter());
    final TransformB tB = new TransformB(cornerMask, edgeMask, new ConsoleReporter());
    final TurnTable mep = t.mEdgePosTable;
    final TurnTable uep = t.uEdgePosTable;
    final TurnTable dep = t.dEdgePosTable;
    m_in_and_out_B(tB, mep);
    u_in_and_out_B(tB, uep);
    d_in_and_out_B(tB, dep);
    m_to_B_sizes(tB, mep);
    u_to_B_sizes(tB, uep);
    d_to_B_sizes(tB, dep);
    assertEquals(0, tB.convertTo_mEdgePos(0));
    assertEquals(1, tB.convertTo_mEdgePos(1));
    assertEquals(4 * 3 - 1, tB.convertTo_mEdgePos(4 * 3 - 1));
    assertEquals(-1, tB.convertTo_mEdgePos(4 * 3));
    assertEquals(-1, tB.convertTo_mEdgePos(12 * 11 - 1));
    assertEquals(0, tB.convert_uEdgePos_dEdgePos_to_udEdgePosB(2514, 70));
  }

  private void m_in_and_out_B(final TransformB tB, final TurnTable mep) {
    assertTrue(tB.is_mEdgePos_inB(mep.start(0)));
    assertFalse(tB.is_mEdgePos_inB(mep.turn(Turn.R1, mep.start(0))));
    assertTrue(tB.is_mEdgePos_inB(mep.turn(Turn.R2, mep.start(0))));
    assertTrue(tB.is_mEdgePos_inB(mep.turn(Turn.U1, mep.start(0))));
  }

  private void u_in_and_out_B(final TransformB tB, final TurnTable uep) {
    assertTrue(tB.is_uEdgePos_inB(uep.start(0)));
    assertFalse(tB.is_uEdgePos_inB(uep.turn(Turn.R1, uep.start(0))));
    assertTrue(tB.is_uEdgePos_inB(uep.turn(Turn.R2, uep.start(0))));
    assertTrue(tB.is_uEdgePos_inB(uep.turn(Turn.U1, uep.start(0))));
  }

  private void d_in_and_out_B(final TransformB tB, final TurnTable dep) {
    assertTrue(tB.is_dEdgePos_inB(dep.start(0)));
    assertFalse(tB.is_dEdgePos_inB(dep.turn(Turn.R1, dep.start(0))));
    assertTrue(tB.is_dEdgePos_inB(dep.turn(Turn.R2, dep.start(0))));
    assertTrue(tB.is_dEdgePos_inB(dep.turn(Turn.U1, dep.start(0))));
  }

  private void m_to_B_sizes(final TransformB tB, final TurnTable mep) {
    int mInB = 0;
    for (int i = 0; i < mep.stateSize(); i++)
      if (tB.is_mEdgePos_inB(i))
        mInB++;
    assertEquals(12 * 11, mep.stateSize());
    assertEquals(4 * 3, mInB);
  }

  private void u_to_B_sizes(final TransformB tB, final TurnTable uep) {
    int uInB = 0;
    for (int i = 0; i < uep.stateSize(); i++)
      if (tB.is_uEdgePos_inB(i))
        uInB++;
    assertEquals(12 * 11 * 10 * 9, uep.stateSize());
    assertEquals(8 * 7 * 6 * 5, uInB);
  }

  private void d_to_B_sizes(final TransformB tB, final TurnTable dep) {
    int dInB = 0;
    for (int i = 0; i < dep.stateSize(); i++)
      if (tB.is_dEdgePos_inB(i))
        dInB++;
    assertEquals(12 * 11, dep.stateSize());
    assertEquals(8 * 7, dInB);
  }
}
