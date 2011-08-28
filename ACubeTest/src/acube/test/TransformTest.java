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
import static acube.Turn.F1;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import acube.Corner;
import acube.Edge;
import acube.Turn;
import acube.console.ConsoleReporter;
import acube.transform.MoveTableComposed;
import acube.transform.Transform;
import acube.transform.TransformB;
import acube.transform.TurnTable;

public final class TransformTest {
  private final Set<Corner> cornerMask = asSet(new Corner[] { UFR, URB, UBL, ULF });
  private final Set<Edge> edgeMask = asSet(new Edge[] { UF, UR, UB, UL, DF, DR, FR, FL });
  private final Set<Corner> cornerTwistMask = asSet(new Corner[] { ULF, DRF, DFL, DLB, DBR });
  private final Set<Edge> edgeFlipMask = asSet(new Edge[] { UL, DF, DR, DB, DL, FR, FL });

  private <T> Set<T> asSet(final T[] a) {
    return new HashSet<T>(Arrays.asList(a));
  }

  @Test
  public void tables_lengths_matches_combinatorics() {
    final Transform t =
        new Transform(cornerMask, edgeMask, cornerTwistMask, edgeFlipMask, Turn.valueSet, new ConsoleReporter());
    final TransformB tB = new TransformB(cornerMask, edgeMask, Turn.valueSet, new ConsoleReporter());
    checkTable(t.cornerPos, 8 * 7 * 6 * 5);
    checkTable(t.twist, 2187);
    checkTable(t.flip, 2048);
    checkTable(t.mEdgePosSet, 12 * 11 / 2);
    checkTable(t.mEdgePos, 12 * 11);
    checkTable(t.uEdgePos, 12 * 11 * 10 * 9);
    checkTable(t.dEdgePos, 12 * 11);
    checkTable(tB.mEdgePos, 4 * 3);
    checkTable(tB.oEdgePos, 8 * 7 * 6 * 5 * 4 * 3);
  }

  private void checkTable(final TurnTable table, final int states) {
    assertEquals(table.stateSize(), states);
    // reachability test (depth-first maze walkthrough)
    final Turn[] turns = table.turnMaskArray();
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
    final Transform t =
        new Transform(cornerMask, edgeMask, cornerTwistMask, edgeFlipMask, Turn.valueSet, new ConsoleReporter());
    final TransformB tB = new TransformB(cornerMask, edgeMask, Turn.valueSet, new ConsoleReporter());
    assertTrue(null != new MoveTableComposed(t.dEdgePos, tB.mEdgePos));
  }

  @Test
  public void table_composed_move() {
    final Transform t =
        new Transform(cornerMask, edgeMask, cornerTwistMask, edgeFlipMask, Turn.valueSet, new ConsoleReporter());
    final MoveTableComposed move = new MoveTableComposed(t.mEdgePosSet, t.dEdgePos);
    assertEquals(move.stateSize(), t.mEdgePosSet.stateSize() * t.dEdgePos.stateSize());
    assertArrayEquals(move.turnMaskArray(), t.mEdgePosSet.turnMaskArray());
    assertArrayEquals(move.turnMaskArray(), t.dEdgePos.turnMaskArray());
    assertEquals(move.startSize(), t.mEdgePosSet.startSize() * t.dEdgePos.startSize());
    for (int i = 0; i < move.stateSize(); i++)
      for (final Turn turn : move.turnMask()) {
        assertEquals(move.turn(turn, i) / t.dEdgePos.stateSize(), t.mEdgePosSet.turn(turn, i / t.dEdgePos.stateSize()));
        assertEquals(move.turn(turn, i) % t.dEdgePos.stateSize(), t.dEdgePos.turn(turn, i % t.dEdgePos.stateSize()));
      }
    for (int i = 0; i < move.startSize(); i++) {
      assertEquals(move.start(i) / t.dEdgePos.stateSize(), t.mEdgePosSet.start(i / t.dEdgePos.startSize()));
      assertEquals(move.start(i) % t.dEdgePos.stateSize(), t.dEdgePos.start(i % t.dEdgePos.startSize()));
    }
  }

  @Test
  public void tables_for_phase_B() {
    final Transform t =
        new Transform(cornerMask, edgeMask, cornerTwistMask, edgeFlipMask, Turn.valueSet, new ConsoleReporter());
    final TransformB tB = new TransformB(cornerMask, edgeMask, Turn.valueSet, new ConsoleReporter());
    assertEquals(tB.cornerPos.stateSize(), t.cornerPos.stateSize());
    assertEquals(t.cornerPos.turnMask().size(), Turn.size);
    assertEquals(tB.cornerPos.turnMask().size(), Turn.getValidB(Turn.valueSet).size());
    assertEquals(tB.cornerPos.startSize(), t.cornerPos.startSize());
    for (final Turn turn : tB.cornerPos.turnMask())
      for (int i = 0; i < tB.cornerPos.stateSize(); i++)
        assertEquals(t.cornerPos.turn(turn, i), tB.cornerPos.turn(turn, i));
    for (int i = 0; i < tB.cornerPos.startSize(); i++)
      assertEquals(tB.cornerPos.start(i), t.cornerPos.start(i));
  }

  private static final Turn[][] revB = {
      { U1, U3 }, { U2, U2 }, { D1, D3 }, { D2, D2 }, { F2, F2 }, { B2, B2 }, { L2, L2 }, { R2, R2 }, { E1, E3 },
      { E2, E2 }, { S2, S2 }, { M2, M2 }, { u1, u3 }, { u2, u2 }, { d1, d3 }, { d2, d2 }, { f2, f2 }, { b2, b2 },
      { l2, l2 }, { r2, r2 } };

  @Test
  public void back_and_forth_turn_results_in_identity_in_phase_B() {
    final TransformB tB = new TransformB(cornerMask, edgeMask, Turn.valueSet, new ConsoleReporter());
    checkBackForthTurns(tB.mEdgePos);
    checkBackForthTurns(tB.oEdgePos);
    checkBackForthTurns(tB.cornerPos);
  }

  private void checkBackForthTurns(final TurnTable tab) {
    for (int i = 0; i < tab.stateSize(); i++)
      for (final Turn[] element : revB) {
        assertEquals(i, tab.turn(element[0], tab.turn(element[1], i)));
        assertEquals(i, tab.turn(element[1], tab.turn(element[0], i)));
      }
  }

  @Test
  public void conversion_mEdgePos_to_mEdgePosSet() {
    final Transform t =
        new Transform(cornerMask, edgeMask, cornerTwistMask, edgeFlipMask, Turn.valueSet, new ConsoleReporter());
    for (int mep = 0; mep < t.mEdgePos.stateSize(); mep++) {
      final int meps = t.convert_mEdgePos_to_mEdgePosSet(mep);
      assertEquals(t.mEdgePosSet.turn(F1, meps), t.convert_mEdgePos_to_mEdgePosSet(t.mEdgePos.turn(F1, mep)));
      assertEquals(t.mEdgePosSet.turn(R2, meps), t.convert_mEdgePos_to_mEdgePosSet(t.mEdgePos.turn(R2, mep)));
      assertEquals(t.mEdgePosSet.turn(U3, meps), t.convert_mEdgePos_to_mEdgePosSet(t.mEdgePos.turn(U3, mep)));
    }
    final int mepStart = t.mEdgePos.start(0);
    final int mepsF1 = t.convert_mEdgePos_to_mEdgePosSet(t.mEdgePos.turn(F1, mepStart));
    final int mepsR2 = t.convert_mEdgePos_to_mEdgePosSet(t.mEdgePos.turn(R2, mepStart));
    assertFalse(mepsF1 == mepsR2);
  }

  @Test
  public void conversion_to_phase_B() {
    final Transform t =
        new Transform(cornerMask, edgeMask, cornerTwistMask, edgeFlipMask, Turn.valueSet, new ConsoleReporter());
    final TransformB tB = new TransformB(cornerMask, edgeMask, Turn.valueSet, new ConsoleReporter());
    final TurnTable mep = t.mEdgePos;
    final TurnTable uep = t.uEdgePos;
    final TurnTable dep = t.dEdgePos;
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
    assertEquals(0, tB.convertTo_oEdgePos(2514, 70));
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
