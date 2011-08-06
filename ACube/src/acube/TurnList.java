package acube;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import acube.transform.Transform;

public final class TurnList {
  private static final int MAX_DEPTH = 3;
  // TODO: fill it
  private final Turn[][] turnLists;
  private final TurnB[][] turnListsB;
  private final int[][] next;

  public TurnList(final Transform transform) {
    final List<int[]> table = createStateTable(transform);
    final boolean[] activeStates = getActiveStates(table);
    final int[] ind = new int[table.size()];
    int n = 1;
    while (n > 0) {
      n = 0;
      for (int i = 0; i < ind.length; i++)
        ind[i] = i;
      int sel = 0;
      for (int i = 0; i < table.size() - 1; i++)
        if (activeStates[i]) {
          sel++;
          for (int j = i + 1; j < table.size(); j++)
            if (activeStates[j]) {
              boolean eq = true;
              for (int t = 0; t < Turn.size(); t++)
                if (table.get(i)[t] != table.get(j)[t]) {
                  eq = false;
                  break;
                }
              if (eq) { // equal state?
                ind[j] = i;
                if (activeStates[j])
                  activeStates[j] = false;
                n++;
              }
            }
        }
      // reduce equal states
      for (int i = 0; i < table.size(); i++)
        if (activeStates[i])
          for (int t = 0; t < Turn.size(); t++) {
            final int x = table.get(i)[t];
            if (x >= 0 && ind[x] != x)
              table.get(i)[t] = ind[x];
          }
    }
    int d = 0;
    int dup = 0;
    int tot = 0;
    for (int i = 0; i < table.size(); i++)
      if (activeStates[i]) {
        d++;
        for (int t = 0; t < Turn.size(); t++) {
          tot++;
          if (table.get(i)[t] == -1)
            dup++;
        }
      }
    int st = 0;
    // get new numbers for states
    for (int i = 0; i < ind.length; i++)
      ind[i] = activeStates[i] ? st++ : -1;
    next = new int[d][Turn.size()];
    for (int i = 0; i < Turn.size(); i++)
      next[0][i] = -1;
    int j = 0;
    // renumber states
    for (int i = 0; i < table.size(); i++)
      if (activeStates[i]) {
        for (int t = 0; t < Turn.size(); t++) {
          final int x = table.get(i)[t];
          if (x < 0)
            next[j][t] = -1;
          else {
            next[j][t] = (short)ind[x];
            assert ind[x] >= 0;
          }
        }
        j++;
      }
    assert j == d;
    turnLists = getAllowedTurnsTable(transform);
    turnListsB = getAllowedTurnsTableB(turnLists);
  }

  private boolean[] getActiveStates(final List<int[]> table) {
    final boolean[] activeStates = new boolean[table.size()];
    activeStates[0] = true;
    for (int i = 0; i < table.size(); i++)
      for (final Turn turn : Turn.values())
        if (table.get(i)[turn.ordinal()] != -1 && !activeStates[table.get(i)[turn.ordinal()]])
          activeStates[table.get(i)[turn.ordinal()]] = true;
    return activeStates;
  }

  private Turn[][] getAllowedTurnsTable(final Transform transform) {
    final Turn[][] tl = new Turn[next.length][];
    for (int state = 0; state < next.length; state++) {
      int turnCount = 0;
      for (final Turn t : transform.turns())
        if (next[state][t.ordinal()] >= 0)
          turnCount++;
      tl[state] = new Turn[turnCount];
      int freeTurnIndex = 0;
      for (final Turn t : transform.turns())
        if (next[state][t.ordinal()] >= 0)
          tl[state][freeTurnIndex++] = t;
    }
    return tl;
  }

  private TurnB[][] getAllowedTurnsTableB(final Turn[][] turns) {
    final TurnB[][] tl = new TurnB[turns.length][];
    for (int state = 0; state < next.length; state++) {
      int turnCount = 0;
      for (final Turn t : turns[state])
        if (t.isB())
          turnCount++;
      tl[state] = new TurnB[turnCount];
      int freeTurnIndex = 0;
      for (final Turn t : turns[state])
        if (t.isB())
          tl[state][freeTurnIndex++] = t.toB();
    }
    return tl;
  }

  public int getNextState(final int state, final Turn t) {
    return next[state][t.ordinal()];
  }

  public Turn[] getAvailableTurns(final int state) {
    return turnLists[state];
  }

  public TurnB[] getAvailableTurnsB(final int state) {
    return turnListsB[state];
  }

  private static List<int[]> createStateTable(final Transform transform) {
    final HashSet<Turn> turnSet = new HashSet<Turn>(Arrays.asList(transform.turns()));
    final List<State> states = new ArrayList<State>();
    final HashMap<State, Integer> stateIndices = new HashMap<State, Integer>();
    final List<int[]> table = new ArrayList<int[]>();
    final State state0 = State.init(transform);
    initializeStateTables(transform, turnSet, table, states, stateIndices, state0);
    linkStates(transform, turnSet, table, states, stateIndices, state0);
    return table;
  }

  private static void
      initializeStateTables(final Transform transform, final HashSet<Turn> turnSet, final List<int[]> table,
          final List<State> states, final HashMap<State, Integer> stateIndices, final State state0) {
    states.add(state0);
    stateIndices.put(state0, 0);
    for (int i = 0; i < states.size(); i++) {
      final int[] row = new int[Turn.size()];
      for (final Turn turn : Turn.values())
        if (!turnSet.contains(turn))
          row[turn.ordinal()] = -1;
        else {
          final State state = states.get(i).turn(turn, transform);
          if (stateIndices.containsKey(state))
            row[turn.ordinal()] = -1;
          else if (state.turns.length <= MAX_DEPTH) {
            stateIndices.put(state, states.size());
            row[turn.ordinal()] = states.size();
            states.add(state);
          }
        }
      table.add(row);
    }
  }

  private static void linkStates(final Transform transform, final HashSet<Turn> turnSet, final List<int[]> table,
      final List<State> states, final HashMap<State, Integer> stateIndices, final State state0) {
    for (int i = 0; i < table.size(); i++) {
      final int[] row = table.get(i);
      final State s = states.get(i);
      for (final Turn t : Turn.values())
        if (!turnSet.contains(t))
          row[t.ordinal()] = -1;
        else if (row[t.ordinal()] == 0)
          for (int m = 1; m < s.turns.length; m++) {
            State state = state0;
            for (int mt = m; mt < s.turns.length; mt++)
              state = state.turn(s.turns[mt], transform);
            state = state.turn(t, transform);
            if (stateIndices.containsKey(state)) {
              final int val = stateIndices.get(state);
              row[t.ordinal()] = state.sameMove(states.get(val)) ? val : -1;
              break;
            }
          }
    }
  }
}

final class State {
  private static final Turn[] empty = new Turn[0];
  private final int cornerTwist;
  private final int cornerPosition;
  private final int edgeFlip;
  private final int mEdgePosition;
  private final int uEdgePosition;
  private final int dEdgePosition;
  private final int symmetry;
  public final Turn[] turns;

  private State(final int ct, final int cp, final int ef, final int mep, final int uep, final int dep, final int sym,
      final Turn[] m) {
    cornerTwist = ct;
    cornerPosition = cp;
    edgeFlip = ef;
    mEdgePosition = mep;
    uEdgePosition = uep;
    dEdgePosition = dep;
    symmetry = sym;
    turns = m;
  }

  private State(final int ct, final int cp, final int ef, final int mep, final int uep, final int dep, final int sym) {
    this(ct, cp, ef, mep, uep, dep, sym, empty);
  }

  private State(final int ct, final int cp, final int ef, final int mep, final int uep, final int dep, final int sym,
      final Turn[] m, final Turn t) {
    this(ct, cp, ef, mep, uep, dep, sym, new Turn[m.length + 1]);
    for (int i = 0; i < m.length; i++)
      turns[i] = m[i];
    turns[m.length] = t;
  }

  public boolean sameMove(final State s) {
    if (turns.length != s.turns.length)
      return false;
    for (int i = 0; i < turns.length; i++)
      if (turns[i] != s.turns[i])
        return false;
    return true;
  }

  public State turn(final Turn turn, final Transform t) {
    final Turn rotatedTurn = SymTransform.getTurn(turn, symmetry);
    final int ct = t.cornerTwist.turn(rotatedTurn, cornerTwist);
    final int cp = t.cornerPosition.turn(rotatedTurn, cornerPosition);
    final int ef = t.edgeFlip.turn(rotatedTurn, edgeFlip);
    final int mep = t.mEdgePosition.turn(rotatedTurn, mEdgePosition);
    final int uep = t.uEdgePosition.turn(rotatedTurn, uEdgePosition);
    final int dep = t.dEdgePosition.turn(rotatedTurn, dEdgePosition);
    final int sym = SymTransform.getSymmetry(rotatedTurn, symmetry);
    return new State(ct, cp, ef, mep, uep, dep, sym, turns, turn);
  }

  public static State init(final Transform t) {
    final int ct = t.cornerTwist.start(0);
    final int cp = t.cornerPosition.start(0);
    final int ef = t.edgeFlip.start(0);
    final int mep = t.mEdgePosition.start(0);
    final int uep = t.uEdgePosition.start(0);
    final int dep = t.dEdgePosition.start(0);
    return new State(ct, cp, ef, mep, uep, dep, SymTransform.I);
  }

  @Override
  public boolean equals(final Object o) {
    if (!(o instanceof State))
      return false;
    final State s = (State)o;
    return cornerTwist == s.cornerTwist && cornerPosition == s.cornerPosition && edgeFlip == s.edgeFlip &&
        mEdgePosition == s.mEdgePosition && uEdgePosition == s.uEdgePosition && dEdgePosition == s.dEdgePosition &&
        symmetry == s.symmetry;
  }

  @Override
  public int hashCode() {
    int h = 31;
    h = h * 113 + cornerTwist;
    h = h * 113 + cornerPosition;
    h = h * 113 + edgeFlip;
    h = h * 113 + mEdgePosition;
    h = h * 113 + uEdgePosition;
    h = h * 113 + dEdgePosition;
    h = h * 113 + symmetry;
    return h;
  }
}
