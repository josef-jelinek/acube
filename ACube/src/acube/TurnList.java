package acube;

import static java.lang.Math.max;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import acube.transform.Transform;
import acube.transform.TransformB;

public final class TurnList {
  private static final int MAX_DEPTH = 3;
  private static final Turn[] empty = {};
  private final Turn[][] turnListTable;
  private final int[][] nextStateTable;
  private final int[] turnIndices;

  public int getInitialState(final int symmetry) {
    return symmetry;
  }

  public int getNextState(final int state, final Turn t) {
    return nextStateTable[state][turnIndices[t.ordinal()]];
  }

  public Turn[] getAvailableTurns(final int state) {
    return state < 0 ? empty : turnListTable[state];
  }

  public TurnList(final Transform transform, final EnumSet<Turn> turns, final Metric metric) {
    final Turn[] sortedTurns = getSortedTurns(turns, metric);
    turnIndices = getTurnIndices(sortedTurns);
    nextStateTable = compactStateTable(createStateTable(transform, sortedTurns));
    turnListTable = getAllowedTurnsTable(nextStateTable, sortedTurns);
  }

  public TurnList(final TransformB transform, final EnumSet<Turn> turns, final Metric metric) {
    final Turn[] sortedTurns = getSortedTurns(turns, metric);
    turnIndices = getTurnIndices(sortedTurns);
    nextStateTable = compactStateTable(createStateTable(transform, sortedTurns));
    turnListTable = getAllowedTurnsTable(nextStateTable, sortedTurns);
  }

  private static Turn[] getSortedTurns(final EnumSet<Turn> turns, final Metric metric) {
    final Turn[] sortedTurns = turns.toArray(new Turn[0]);
    Arrays.sort(sortedTurns, new Comparator<Turn>() {
      @Override
      public int compare(final Turn t1, final Turn t2) {
        return metric.length(t1) - metric.length(t2);
      }
    });
    return sortedTurns;
  }

  private int[] getTurnIndices(final Turn[] turns) {
    final int[] a = new int[Turn.size];
    Arrays.fill(a, -1);
    int ti = 0;
    for (final Turn turn : turns)
      a[turn.ordinal()] = ti++;
    return a;
  }

  private static List<int[]> createStateTable(final Transform transform, final Turn[] turns) {
    final List<State> states = new ArrayList<State>();
    final Map<State, Integer> stateIndices = new HashMap<State, Integer>();
    final List<int[]> table = new ArrayList<int[]>();
    for (int cubeSym = 0; cubeSym < SymTransform.SYM_COUNT; cubeSym++) {
      final State state0 = State.start(cubeSym, transform);
      stateIndices.put(state0, states.size());
      states.add(state0);
    }
    for (int i = 0; i < states.size(); i++)
      table.add(expandStateRow(states, i, stateIndices, transform, turns));
    for (int i = 0; i < table.size(); i++)
      linkStateRow(states.get(i), table.get(i), states, stateIndices, transform, turns);
    return table;
  }

  private static List<int[]> createStateTable(final TransformB transform, final Turn[] turns) {
    final List<StateB> states = new ArrayList<StateB>();
    final Map<StateB, Integer> stateIndices = new HashMap<StateB, Integer>();
    final List<int[]> table = new ArrayList<int[]>();
    for (int cubeSym = 0; cubeSym < SymTransform.SYM_COUNT; cubeSym++) {
      final StateB state0 = StateB.start(cubeSym, transform);
      stateIndices.put(state0, states.size());
      states.add(state0);
    }
    for (int i = 0; i < states.size(); i++)
      table.add(expandStateRow(states, i, stateIndices, transform, turns));
    for (int i = 0; i < table.size(); i++)
      linkStateRow(states.get(i), table.get(i), states, stateIndices, transform, turns);
    return table;
  }

  private static int[] expandStateRow(final List<State> states, final int i, final Map<State, Integer> stateIndices,
      final Transform transform, final Turn[] turns) {
    final State state = states.get(i);
    final int[] row = new int[turns.length];
    int ti = 0;
    for (final Turn turn : turns) {
      final State newState = state.turn(turn, transform);
      if (newState == null || stateIndices.containsKey(newState))
        row[ti++] = -1;
      else if (state.turns.length >= MAX_DEPTH)
        row[ti++] = 0;
      else {
        row[ti++] = states.size();
        stateIndices.put(newState, states.size());
        states.add(newState);
      }
    }
    return row;
  }

  private static int[] expandStateRow(final List<StateB> states, final int i, final Map<StateB, Integer> stateIndices,
      final TransformB transform, final Turn[] turns) {
    final StateB state = states.get(i);
    final int[] row = new int[turns.length];
    int ti = 0;
    for (final Turn turn : turns) {
      final StateB newState = state.turn(turn, transform);
      if (newState == null || stateIndices.containsKey(newState))
        row[ti++] = -1;
      else if (state.turns.length >= MAX_DEPTH)
        row[ti++] = 0;
      else {
        row[ti++] = states.size();
        stateIndices.put(newState, states.size());
        states.add(newState);
      }
    }
    return row;
  }

  private static void linkStateRow(final State state, final int[] row, final List<State> states,
      final Map<State, Integer> stateIndices, final Transform transform, final Turn[] turns) {
    int ti = 0;
    for (final Turn turn : turns)
      if (row[ti++] == 0)
        row[ti - 1] = getTailStateIndex(turn, state, states, stateIndices, transform);
  }

  private static void linkStateRow(final StateB state, final int[] row, final List<StateB> states,
      final Map<StateB, Integer> stateIndices, final TransformB transform, final Turn[] turns) {
    int ti = 0;
    for (final Turn turn : turns)
      if (row[ti++] == 0)
        row[ti - 1] = getTailStateIndex(turn, state, states, stateIndices, transform);
  }

  private static int getTailStateIndex(final Turn turn, final State state, final List<State> states,
      final Map<State, Integer> stateIndices, final Transform transform) {
    for (int cubeSym = 0; cubeSym < SymTransform.SYM_COUNT; cubeSym++) {
      final State state0 = states.get(cubeSym);
      for (int j = 1; j < state.turns.length; j++) {
        final State sTail = getTailState(state0, state.turns, j, transform);
        if (sTail != null) {
          final State s = sTail.turn(turn, transform);
          if (s != null && stateIndices.containsKey(s)) {
            final int index = stateIndices.get(s);
            return s.sameMove(states.get(index)) ? index : -1;
          }
        }
      }
    }
    throw new RuntimeException("State should have been found, but was not");
  }

  private static int getTailStateIndex(final Turn turn, final StateB state, final List<StateB> states,
      final Map<StateB, Integer> stateIndices, final TransformB transform) {
    for (int cubeSym = 0; cubeSym < SymTransform.SYM_COUNT; cubeSym++) {
      final StateB state0 = states.get(cubeSym);
      for (int j = 1; j < state.turns.length; j++) {
        final StateB sTail = getTailState(state0, state.turns, j, transform);
        if (sTail != null) {
          final StateB s = sTail.turn(turn, transform);
          if (s != null && stateIndices.containsKey(s)) {
            final int index = stateIndices.get(s);
            return s.sameMove(states.get(index)) ? index : -1;
          }
        }
      }
    }
    throw new RuntimeException("State should have been found, but was not");
  }

  private static State getTailState(State state, final Turn[] turns, final int from, final Transform transform) {
    for (int i = from; state != null && i < turns.length; i++)
      state = state.turn(turns[i], transform);
    return state;
  }

  private static StateB getTailState(StateB state, final Turn[] turns, final int from, final TransformB transform) {
    for (int i = from; state != null && i < turns.length; i++)
      state = state.turn(turns[i], transform);
    return state;
  }

  private static int[][] compactStateTable(final List<int[]> table) {
    final int[] indices = new int[table.size()];
    for (int i = 0; i < indices.length; i++)
      indices[i] = i;
    while (markEqualStates(table, indices))
      renumberEqualStates(table, indices);
    return compactStates(table);
  }

  private static boolean markEqualStates(final List<int[]> table, final int[] indices) {
    boolean found = false;
    for (int i = 0; i < table.size() - 1; i++) {
      final int[] row1 = table.get(i);
      if (row1 != null)
        for (int j = max(SymTransform.SYM_COUNT, i + 1); j < table.size(); j++) {
          final int[] row2 = table.get(j);
          if (row2 != null && indices[i] != indices[j] && areEqual(row1, row2)) {
            indices[j] = i;
            table.set(j, null);
            found = true;
          }
        }
    }
    return found;
  }

  private static boolean areEqual(final int[] row1, final int[] row2) {
    for (int i = 0; i < row1.length; i++)
      if (row1[i] != row2[i])
        return false;
    return true;
  }

  private static void renumberEqualStates(final List<int[]> table, final int[] indices) {
    for (int i = 0; i < table.size(); i++) {
      final int[] row = table.get(i);
      if (row != null)
        for (int ti = 0; ti < row.length; ti++)
          if (row[ti] >= 0)
            row[ti] = indices[row[ti]];
    }
  }

  private static int[][] compactStates(final List<int[]> table) {
    int uniqueCount = 0;
    final int[] indices = new int[table.size()];
    for (int i = 0; i < indices.length; i++)
      indices[i] = table.get(i) != null ? uniqueCount++ : -1;
    final int[][] newTable = new int[uniqueCount][];
    for (int i = 0, j = 0; i < table.size(); i++)
      if (indices[i] >= 0) {
        final int[] row = table.get(i);
        for (int ti = 0; ti < row.length; ti++)
          if (row[ti] >= 0)
            row[ti] = (short)indices[row[ti]];
        newTable[j++] = row;
      }
    return newTable;
  }

  private static Turn[][] getAllowedTurnsTable(final int[][] nextStateTable, final Turn[] turns) {
    final Turn[][] turnTable = new Turn[nextStateTable.length][];
    for (int i = 0; i < nextStateTable.length; i++) {
      int turnCount = 0;
      for (int ti = 0; ti < nextStateTable[i].length; ti++)
        if (nextStateTable[i][ti] >= 0)
          turnCount++;
      turnTable[i] = new Turn[turnCount];
      int ti = 0, tj = 0;
      for (final Turn turn : turns)
        if (nextStateTable[i][ti++] >= 0)
          turnTable[i][tj++] = turn;
    }
    return turnTable;
  }
}

final class State {
  private final int cubeSym;
  private final int twist;
  private final int flip;
  private final int cornerPos;
  private final int mEdgePos;
  private final int uEdgePos;
  private final int dEdgePos;
  public final Turn[] turns;

  public static State start(final int cubeSym, final Transform t) {
    final int ct = t.twistTable.start(0);
    final int ef = t.flipTable.start(0);
    final int cp = t.cornerPosTable.start(0);
    final int mep = t.mEdgePosTable.start(0);
    final int uep = t.uEdgePosTable.start(0);
    final int dep = t.dEdgePosTable.start(0);
    return new State(cubeSym, ct, ef, cp, mep, uep, dep, new Turn[0]);
  }

  private State(final int cs, final int ct, final int ef, final int cp, final int mep, final int uep, final int dep,
      final Turn[] seq) {
    cubeSym = cs;
    twist = ct;
    flip = ef;
    cornerPos = cp;
    mEdgePos = mep;
    uEdgePos = uep;
    dEdgePos = dep;
    turns = seq;
  }

  private State(final int cs, final int ct, final int ef, final int cp, final int mep, final int uep, final int dep,
      final Turn[] seq, final Turn t) {
    this(cs, ct, ef, cp, mep, uep, dep, new Turn[seq.length + 1]);
    for (int i = 0; i < seq.length; i++)
      turns[i] = seq[i];
    turns[seq.length] = t;
  }

  public State turn(final Turn userTurn, final Transform t) {
    final Turn cubeTurn = SymTransform.getTurn(userTurn, cubeSym);
    final int cs = SymTransform.getSymmetry(cubeSym, userTurn);
    final int ct = t.twistTable.turn(cubeTurn, twist);
    final int ef = t.flipTable.turn(cubeTurn, flip);
    final int cp = t.cornerPosTable.turn(cubeTurn, cornerPos);
    final int mep = t.mEdgePosTable.turn(cubeTurn, mEdgePos);
    final int uep = t.uEdgePosTable.turn(cubeTurn, uEdgePos);
    final int dep = t.dEdgePosTable.turn(cubeTurn, dEdgePos);
    return new State(cs, ct, ef, cp, mep, uep, dep, turns, userTurn);
  }

  public boolean sameMove(final State s) {
    if (turns.length != s.turns.length)
      return false;
    for (int i = 0; i < turns.length; i++)
      if (turns[i] != s.turns[i])
        return false;
    return true;
  }

  @Override
  public boolean equals(final Object other) {
    if (!(other instanceof State))
      return false;
    final State o = (State)other;
    return cubeSym == o.cubeSym && twist == o.twist && flip == o.flip && cornerPos == o.cornerPos &&
        mEdgePos == o.mEdgePos && uEdgePos == o.uEdgePos && dEdgePos == o.dEdgePos;
  }

  @Override
  public int hashCode() {
    final int h = ((cubeSym * 113 + twist) * 113 + flip) * 113 + cornerPos;
    return ((h * 113 + mEdgePos) * 113 + uEdgePos) * 113 + dEdgePos;
  }
}

final class StateB {
  private final int cubeSym;
  private final int cornerPos;
  private final int mEdgePos;
  private final int udEdgePos;
  public final Turn[] turns;

  public static StateB start(final int cubeSym, final TransformB t) {
    final int cp = t.cornerPosTable.start(0);
    final int mep = t.mEdgePosTable.start(0);
    final int udep = t.udEdgePosTable.start(0);
    return new StateB(cubeSym, cp, mep, udep, new Turn[0]);
  }

  private StateB(final int cs, final int cp, final int mep, final int udep, final Turn[] seq) {
    cubeSym = cs;
    cornerPos = cp;
    mEdgePos = mep;
    udEdgePos = udep;
    turns = seq;
  }

  private StateB(final int cs, final int cp, final int mep, final int udep, final Turn[] seq, final Turn t) {
    this(cs, cp, mep, udep, new Turn[seq.length + 1]);
    for (int i = 0; i < seq.length; i++)
      turns[i] = seq[i];
    turns[seq.length] = t;
  }

  public StateB turn(final Turn userTurn, final TransformB t) {
    final Turn cubeTurn = SymTransform.getTurn(userTurn, cubeSym);
    if (!cubeTurn.isB())
      return null;
    final int cs = SymTransform.getSymmetry(cubeSym, userTurn);
    final int cp = t.cornerPosTable.turn(cubeTurn, cornerPos);
    final int mep = t.mEdgePosTable.turn(cubeTurn, mEdgePos);
    final int udep = t.udEdgePosTable.turn(cubeTurn, udEdgePos);
    return new StateB(cs, cp, mep, udep, turns, userTurn);
  }

  public boolean sameMove(final StateB s) {
    if (turns.length != s.turns.length)
      return false;
    for (int i = 0; i < turns.length; i++)
      if (turns[i] != s.turns[i])
        return false;
    return true;
  }

  @Override
  public boolean equals(final Object other) {
    if (!(other instanceof StateB))
      return false;
    final StateB o = (StateB)other;
    return cubeSym == o.cubeSym && cornerPos == o.cornerPos && mEdgePos == o.mEdgePos && udEdgePos == o.udEdgePos;
  }

  @Override
  public int hashCode() {
    return ((cubeSym * 113 + cornerPos) * 113 + mEdgePos) * 113 + udEdgePos;
  }
}
