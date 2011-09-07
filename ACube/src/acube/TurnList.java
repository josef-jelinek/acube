package acube;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import acube.transform.Transform;

public final class TurnList {
  public static final int INITIAL_STATE = 0;
  private static final int MAX_DEPTH = 3;
  private final Turn[][] turnListTable;
  private final int[][] nextStateTable;
  private final int[] turnIndices;

  public int getNextState(final int state, final Turn t) {
    return nextStateTable[state][turnIndices[t.ordinal()]];
  }

  public Turn[] getAvailableTurns(final int state) {
    return turnListTable[state];
  }

  public TurnList(final Transform transform, final EnumSet<Turn> turns, final Metric metric) {
    final Turn[] sortedTurns = turns.toArray(new Turn[0]);
    Arrays.sort(sortedTurns, new Comparator<Turn>() {
      @Override
      public int compare(final Turn t1, final Turn t2) {
        return metric.length(t1) - metric.length(t2);
      }
    });
    turnIndices = getTurnIndices(sortedTurns);
    nextStateTable = compactStateTable(createStateTable(transform, sortedTurns, SymTransform.I));
    turnListTable = getAllowedTurnsTable(nextStateTable, sortedTurns);
  }

  private int[] getTurnIndices(final Turn[] turns) {
    final int[] a = new int[Turn.size];
    Arrays.fill(a, -1);
    int ti = 0;
    for (final Turn turn : turns)
      a[turn.ordinal()] = ti++;
    return a;
  }

  private static List<int[]> createStateTable(final Transform transform, final Turn[] turns, final int cubeSym) {
    final List<State> states = new ArrayList<State>();
    final Map<State, Integer> stateIndices = new HashMap<State, Integer>();
    final List<int[]> table = new ArrayList<int[]>();
    final State state0 = State.start(cubeSym, transform);
    states.add(state0);
    stateIndices.put(state0, 0);
    for (int i = 0; i < states.size(); i++)
      table.add(expandStateRow(states, i, stateIndices, transform, turns));
    for (int i = 0; i < table.size(); i++)
      linkStateRow(states.get(i), table.get(i), state0, stateIndices, transform, turns);
    return table;
  }

  private static int[] expandStateRow(final List<State> states, final int i, final Map<State, Integer> stateIndices,
      final Transform transform, final Turn[] turns) {
    final State state = states.get(i);
    final int[] row = new int[turns.length];
    int ti = 0;
    for (final Turn turn : turns) {
      final State newState = state.turn(turn, transform);
      if (stateIndices.containsKey(newState))
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

  private static void linkStateRow(final State state, final int[] row, final State state0,
      final Map<State, Integer> stateIndices, final Transform transform, final Turn[] turns) {
    int ti = 0;
    for (final Turn turn : turns)
      if (row[ti++] == 0)
        for (int j = 1; j < state.turns.length; j++) {
          final State s = getTailSequenceState(state0, state.turns, j, transform).turn(turn, transform);
          if (stateIndices.containsKey(s)) {
            row[ti - 1] = stateIndices.get(s);
            break;
          }
        }
  }

  private static State getTailSequenceState(State state, final Turn[] turns, final int from, final Transform transform) {
    for (int i = from; i < turns.length; i++)
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
        for (int j = i + 1; j < table.size(); j++) {
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
    int activeCount = 0;
    final int[] indices = new int[table.size()];
    for (int i = 0; i < indices.length; i++)
      indices[i] = table.get(i) != null ? activeCount++ : -1;
    final int[][] newTable = new int[activeCount][];
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
