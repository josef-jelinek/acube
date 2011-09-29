package acube;

import static acube.SymTransform.SYM_COUNT;
import static java.lang.Math.max;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
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
  private static final int MAX_DEPTH = 2;
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

  public TurnList(final EnumSet<Turn> turns, final Metric metric, final boolean phaseA) {
    final Turn[] sortedTurns = getSortedTurns(turns, metric);
    turnIndices = getTurnIndices(sortedTurns);
    final String prefix = phaseA ? "ttA_" : "ttB_";
    final int[][] table = load(prefix, sortedTurns);
    if (table != null)
      nextStateTable = table;
    else {
      nextStateTable =
          compactStateTable(phaseA ? createStateTable(new Transform(), sortedTurns) : createStateTable(
              new TransformB(), sortedTurns));
      save(prefix, nextStateTable, sortedTurns);
    }
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

  private static final String CACHE_DIR = "cache";

  private int[][] load(final String prefix, final Turn[] turns) {
    BufferedReader r = null;
    try {
      if (new File(fileName(prefix, turns)).exists()) {
        r = new BufferedReader(new FileReader(fileName(prefix, turns)));
        final List<int[]> list = new ArrayList<int[]>();
        String line = r.readLine(); // header
        while ((line = r.readLine()) != null && line.length() > 0) {
          final String[] items = line.split("\\s+");
          final int[] row = new int[turns.length];
          for (int i = 0; i < turns.length; i++)
            row[i] = items[i].equals("-") ? -1 : Integer.parseInt(items[i]);
          list.add(row);
        }
        return list.toArray(new int[0][]);
      }
    } catch (final Exception e) {} finally {
      try {
        if (r != null)
          r.close();
      } catch (final IOException ee) {}
    }
    return null;
  }

  private void save(final String prefix, final int[][] table, final Turn[] turns) {
    Writer w = null;
    try {
      new File(CACHE_DIR).mkdir();
      w = new FileWriter(fileName(prefix, turns));
      String header = "";
      for (final Turn t : turns)
        header += t + "\t";
      w.append(header.trim() + "\r\n");
      for (final int[] element : table) {
        String line = "";
        for (int col = 0; col < element.length; col++)
          line += element[col] < 0 ? "-\t" : element[col] + "\t";
        w.append(line.trim() + "\r\n");
      }
    } catch (final Exception e) {} finally {
      try {
        if (w != null)
          w.close();
      } catch (final Exception ee) {}
    }
  }

  private static String fileName(final String prefix, final Turn[] turns) {
    String name = "";
    for (final Turn t : turns)
      name += t.name();
    return CACHE_DIR + File.separator + prefix + name + ".txt";
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

  private static int[] expandStateRow(final List<State> states, final int stateIndex,
      final Map<State, Integer> stateIndices, final Transform transform, final Turn[] turns) {
    final State state = states.get(stateIndex);
    final int[] row = new int[turns.length];
    int ti = 0;
    for (final Turn turn : turns) {
      final State newState = state.turn(turn, transform);
      if (newState == null || stateIndices.containsKey(newState))
        row[ti++] = -1;
      else if (state.turns.length >= MAX_DEPTH)
        row[ti++] = Integer.MAX_VALUE;
      else {
        row[ti++] = states.size();
        stateIndices.put(newState, states.size());
        states.add(newState);
      }
    }
    return row;
  }

  private static int[] expandStateRow(final List<StateB> states, final int stateIndex,
      final Map<StateB, Integer> stateIndices, final TransformB transform, final Turn[] turns) {
    final StateB state = states.get(stateIndex);
    final int[] row = new int[turns.length];
    int ti = 0;
    for (final Turn turn : turns) {
      final StateB newState = state.turn(turn, transform);
      if (newState == null || stateIndices.containsKey(newState))
        row[ti++] = -1;
      else if (state.turns.length >= MAX_DEPTH)
        row[ti++] = Integer.MAX_VALUE;
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
    final State state0 = states.get(state.startSym);
    int ti = 0;
    for (final Turn turn : turns)
      if (row[ti++] == Integer.MAX_VALUE)
        row[ti - 1] = getTailStateIndex(turn, state, state0, states, stateIndices, transform);
  }

  private static void linkStateRow(final StateB state, final int[] row, final List<StateB> states,
      final Map<StateB, Integer> stateIndices, final TransformB transform, final Turn[] turns) {
    final StateB state0 = states.get(state.startSym);
    int ti = 0;
    for (final Turn turn : turns)
      if (row[ti++] == Integer.MAX_VALUE)
        row[ti - 1] = getTailStateIndex(turn, state, state0, states, stateIndices, transform);
  }

  private static int getTailStateIndex(final Turn turn, final State state, final State state0,
      final List<State> states, final Map<State, Integer> stateIndices, final Transform transform) {
    for (int from = 1; from <= state.turns.length; from++) {
      final State sTail = getTailState(state0, state.turns, from, transform);
      if (sTail != null) {
        final State s = sTail.turn(turn, transform);
        if (s != null && stateIndices.containsKey(s)) {
          final int index = stateIndices.get(s);
          return s.sameMove(states.get(index)) ? index : -1;
        }
      }
    }
    throw new RuntimeException("State should have been found, but was not");
  }

  private static int getTailStateIndex(final Turn turn, final StateB state, final StateB state0,
      final List<StateB> states, final Map<StateB, Integer> stateIndices, final TransformB transform) {
    for (int from = 1; from <= state.turns.length; from++) {
      final StateB sTail = getTailState(state0, state.turns, from, transform);
      if (sTail != null) {
        final StateB s = sTail.turn(turn, transform);
        if (s != null && stateIndices.containsKey(s)) {
          final int index = stateIndices.get(s);
          return s.sameMove(states.get(index)) ? index : -1;
        }
      }
    }
    return stateIndices.get(state0);
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
        for (int j = max(SYM_COUNT, i + 1); j < table.size(); j++) {
          final int[] row2 = table.get(j);
          if (row2 != null && indices[i] != indices[j] && Arrays.equals(row1, row2)) {
            indices[j] = i;
            table.set(j, null);
            found = true;
          }
        }
    }
    return found;
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
  final int startSym;
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
    return new State(cubeSym, cubeSym, ct, ef, cp, mep, uep, dep, new Turn[0]);
  }

  private State(final int ss, final int cs, final int ct, final int ef, final int cp, final int mep, final int uep,
      final int dep, final Turn[] seq) {
    startSym = ss;
    cubeSym = cs;
    twist = ct;
    flip = ef;
    cornerPos = cp;
    mEdgePos = mep;
    uEdgePos = uep;
    dEdgePos = dep;
    turns = seq;
  }

  private State(final int ss, final int cs, final int ct, final int ef, final int cp, final int mep, final int uep,
      final int dep, final Turn[] seq, final Turn t) {
    this(ss, cs, ct, ef, cp, mep, uep, dep, new Turn[seq.length + 1]);
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
    return new State(startSym, cs, ct, ef, cp, mep, uep, dep, turns, userTurn);
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
    return startSym == o.startSym && cubeSym == o.cubeSym && twist == o.twist && flip == o.flip &&
        cornerPos == o.cornerPos && mEdgePos == o.mEdgePos && uEdgePos == o.uEdgePos && dEdgePos == o.dEdgePos;
  }

  @Override
  public int hashCode() {
    final int h = (((startSym * 113 + cubeSym) * 113 + twist) * 113 + flip) * 113 + cornerPos;
    return ((h * 113 + mEdgePos) * 113 + uEdgePos) * 113 + dEdgePos;
  }
}

final class StateB {
  final int startSym;
  private final int cubeSym;
  private final int cornerPos;
  private final int mEdgePos;
  private final int udEdgePos;
  public final Turn[] turns;

  public static StateB start(final int cubeSym, final TransformB t) {
    final int cp = t.cornerPosTable.start(0);
    final int mep = t.mEdgePosTable.start(0);
    final int udep = t.udEdgePosTable.start(0);
    return new StateB(cubeSym, cubeSym, cp, mep, udep, new Turn[0]);
  }

  private StateB(final int ss, final int cs, final int cp, final int mep, final int udep, final Turn[] seq) {
    startSym = ss;
    cubeSym = cs;
    cornerPos = cp;
    mEdgePos = mep;
    udEdgePos = udep;
    turns = seq;
  }

  private StateB(final int ss, final int cs, final int cp, final int mep, final int udep, final Turn[] seq, final Turn t) {
    this(ss, cs, cp, mep, udep, new Turn[seq.length + 1]);
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
    return new StateB(startSym, cs, cp, mep, udep, turns, userTurn);
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
    return startSym == o.startSym && cubeSym == o.cubeSym && cornerPos == o.cornerPos && mEdgePos == o.mEdgePos &&
        udEdgePos == o.udEdgePos;
  }

  @Override
  public int hashCode() {
    return (((startSym * 113 + cubeSym) * 113 + cornerPos) * 113 + mEdgePos) * 113 + udEdgePos;
  }
}
