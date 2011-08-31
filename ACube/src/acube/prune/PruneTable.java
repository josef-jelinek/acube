package acube.prune;

import java.util.EnumSet;
import acube.Turn;
import acube.transform.TurnTable;

public final class PruneTable {
  private final byte[] table;
  private final int stateSize;
  private final TurnTable turnTable;
  private final int maxDist;
  private final Turn[] turns;

  public PruneTable(final TurnTable turnTable, final EnumSet<Turn> turns) {
    this.turnTable = turnTable;
    this.turns = turns.toArray(new Turn[turns.size()]);
    table = new byte[(turnTable.stateSize() + 3) / 4];
    int lastFilledSize = startDistFill();
    int totalSize = 0;
    int dist = 0;
    final int switchSize = turnTable.stateSize() / 2;
    while (lastFilledSize > 0) {
      final boolean isForward = totalSize < switchSize;
      logLine(dist, lastFilledSize, totalSize + lastFilledSize, turnTable.stateSize(), isForward);
      totalSize += lastFilledSize;
      final int lastDist = dist++ % 3;
      lastFilledSize = isForward ? forwardDistFill(dist, lastDist) : backwardDistFill(dist, lastDist);
    }
    stateSize = totalSize;
    maxDist = dist - 1;
  }

  private int startDistFill() {
    for (int i = 0; i < table.length; i++)
      table[i] = -1;
    int filled = 0;
    while (filled < turnTable.startSize())
      put(turnTable.start(filled++), 0);
    return filled;
  }

  private int forwardDistFill(final int dist, final int lastDist) {
    int filled = 0;
    for (int state = 0; state < turnTable.stateSize(); state++)
      if (get(state) == lastDist)
        for (final Turn turn : turns) {
          final int newState = turnTable.turn(turn, state);
          if (isNotInitialized(newState)) {
            put(newState, dist % 3);
            filled++;
          }
        }
    return filled;
  }

  private int backwardDistFill(final int dist, final int lastDistValue) {
    int filled = 0;
    for (int state = 0; state < turnTable.stateSize(); state++)
      if (isNotInitialized(state))
        for (final Turn turn : turns)
          if (get(turnTable.turn(turn, state)) == lastDistValue) {
            put(state, dist % 3);
            filled++;
            break;
          }
    return filled;
  }

  private boolean isNotInitialized(final int state) {
    return get(state) == 3;
  }

  private void put(final int state, final int val) {
    final int i = state >> 2;
    final int sh = state << 1 & 7;
    table[i] = (byte)(table[i] & ~(3 << sh) | val << sh);
  }

  private int get(final int state) {
    return table[state >> 2] >> (state << 1 & 7) & 3;
  }

  public int dist(final int lastDepth, final int newState) {
    //  dist get(state)
    // depth 0 1 2 3
    //     0 0 1 1x3x
    //     1 0 1 2 2x
    //     2 3 1 2 3x
    //     3 3 4 2 3x
    //     4 3 4 5 3x
    //     5 6 4 5 6x
    //     6 6 7 5 6x
    //        ...      x - invalid combination
    return lastDepth - (lastDepth - get(newState) + 4) % 3 + 1;
    // (depth - get(state) + 4) / 3 * 3 + get(state) - 3;
  }

  public int startDist(final int state) {
    //    d get(state)
    // last 0 1 2 3
    //    0 1 0 2 1
    //    1 2 1 0 2
    //    2 0 2 1 0
    int dist = 0;
    int lastCode = get(state);
    int lastState = state;
    for (int turnIndex = 0; turnIndex < turns.length; turnIndex++) {
      final int newState = turnTable.turn(turns[turnIndex], lastState);
      if ((lastCode - get(newState) + 4) % 3 == 2) { // 2->1 1->0 0->2
        dist++;
        lastCode = (lastCode + 2) % 3; // 0->2 1->0 2->1
        lastState = newState; // restart loop for the new state
        turnIndex = -1;
      }
    }
    return dist;
  }

  public int maxDist() {
    return maxDist;
  }

  public int stateSize() {
    return stateSize;
  }

  public int memorySize() {
    return table.length;
  }

  private static void logLine(final int depth, final int current, final int soFar, final int total,
      final boolean isForward) {
    //System.out.printf("%2d%s %8d %10d %3d%%%n", depth, forward ? ">" : "<", current, soFar, (long)soFar * 100 / total);
  }
}
