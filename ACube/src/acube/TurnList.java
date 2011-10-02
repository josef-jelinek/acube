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
import acube.transform.CubeSpaceProvider;
import acube.transform.CubeSpaceState;
import acube.transform.Transform;
import acube.transform.TransformB;

public final class TurnList {
  public static enum Phase {
    A, B
  }

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

  public TurnList(final EnumSet<Turn> turns, final Metric metric, final Phase phase) {
    final Turn[] sortedTurns = getSortedTurns(turns, metric);
    turnIndices = getTurnIndices(sortedTurns);
    final String prefix = "tt" + phase.name() + maxDepth(sortedTurns) + "_";
    final int[][] table = load(prefix, sortedTurns);
    if (table != null)
      nextStateTable = table;
    else {
      final CubeSpaceProvider csp = phase == Phase.A ? new Transform() : new TransformB();
      nextStateTable = compactStateTable(createStateTable(csp, sortedTurns));
      save(prefix, nextStateTable, sortedTurns);
    }
    turnListTable = getAllowedTurnsTable(nextStateTable, sortedTurns);
  }

  private static int maxDepth(final Turn[] turns) {
    return turns.length > Turn.size / 2 ? MAX_DEPTH - 1 : MAX_DEPTH;
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
        for (final int element2 : element)
          line += element2 < 0 ? "-\t" : element2 + "\t";
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

  private static List<int[]> createStateTable(final CubeSpaceProvider csp, final Turn[] turns) {
    final List<State> states = new ArrayList<State>();
    final Map<State, Integer> stateIndices = new HashMap<State, Integer>();
    final List<int[]> table = new ArrayList<int[]>();
    for (int cubeSym = 0; cubeSym < SymTransform.SYM_COUNT; cubeSym++) {
      final State state0 = State.start(cubeSym, csp);
      stateIndices.put(state0, states.size());
      states.add(state0);
    }
    for (int i = 0; i < states.size(); i++)
      table.add(expandStateRow(states, states.get(i), stateIndices, turns));
    for (int i = 0; i < table.size(); i++)
      linkStateRow(states.get(i), table.get(i), states, stateIndices, turns);
    return table;
  }

  private static int[] expandStateRow(final List<State> states, final State state,
      final Map<State, Integer> stateIndices, final Turn[] turns) {
    final int[] row = new int[turns.length];
    int ti = 0;
    for (final Turn turn : turns) {
      final State newState = state.turn(turn);
      if (newState == null || stateIndices.containsKey(newState))
        row[ti++] = -1;
      else if (state.turns.length >= maxDepth(turns))
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
      final Map<State, Integer> stateIndices, final Turn[] turns) {
    final State state0 = states.get(state.startSym);
    int ti = 0;
    for (final Turn turn : turns)
      if (row[ti++] == Integer.MAX_VALUE)
        row[ti - 1] = getTailStateIndex(turn, state, state0, states, stateIndices);
  }

  private static int getTailStateIndex(final Turn turn, final State state, final State state0,
      final List<State> states, final Map<State, Integer> stateIndices) {
    for (int from = 1; from <= state.turns.length; from++) {
      final State sTail = getTailState(state0, state.turns, from);
      if (sTail != null) {
        final State s = sTail.turn(turn);
        if (s != null && stateIndices.containsKey(s)) {
          final int index = stateIndices.get(s);
          return s.sameMove(states.get(index)) ? index : -1;
        }
      }
    }
    return stateIndices.get(state0);
  }

  private static State getTailState(State state, final Turn[] turns, final int from) {
    for (int i = from; state != null && i < turns.length; i++)
      state = state.turn(turns[i]);
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
  private final CubeSpaceState state;
  final Turn[] turns;

  public static State start(final int cubeSym, final CubeSpaceProvider t) {
    return new State(cubeSym, t.startState(cubeSym), new Turn[0]);
  }

  protected State(final int ss, final CubeSpaceState s, final Turn[] seq) {
    startSym = ss;
    state = s;
    turns = seq;
  }

  private State(final int ss, final CubeSpaceState s, final Turn[] seq, final Turn t) {
    this(ss, s, new Turn[seq.length + 1]);
    for (int i = 0; i < seq.length; i++)
      turns[i] = seq[i];
    turns[seq.length] = t;
  }

  public State turn(final Turn userTurn) {
    final CubeSpaceState s = state.turn(userTurn);
    return s == null ? null : new State(startSym, s, turns, userTurn);
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
    return startSym == o.startSym && state.equals(o.state);
  }

  @Override
  public int hashCode() {
    return startSym * 113 + state.hashCode();
  }
}
