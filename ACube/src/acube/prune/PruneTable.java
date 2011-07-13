package acube.prune;

import acube.Turn;
import acube.transform.IDoMove;
import acube.transform.ITableMove;

public final class PruneTable {
  private final byte[] table;
  private final Turn[] turns;
  private final int stateSize;
  private final IDoMove move;
  private final int maxDistance;

  public PruneTable(final ITableMove move, final Turn[] turns) {
    this.move = move;
    this.turns = turns.clone();
    table = new byte[(move.stateSize() + 3) / 4];
    for (int i = 0; i < table.length; i++)
      table[i] = -1;
    int currentDistanceSize = 0;
    while (currentDistanceSize < move.startSize())
      put(move.start(currentDistanceSize++), 0);
    int totalSize = 0;
    int distance = 0;
    final int switchState = move.stateSize() / 2;
    while (currentDistanceSize > 0) {
      logLine(distance, currentDistanceSize, totalSize + currentDistanceSize, move.stateSize(),
          totalSize < switchState);
      totalSize += currentDistanceSize;
      final int lastDistanceValue = distance++ % 3;
      currentDistanceSize = 0;
      if (totalSize < switchState) {
        for (int state = 0; state < move.stateSize(); state++)
          if (get(state) == lastDistanceValue)
            for (final Turn turn : turns) {
              final int newState = move.turn(turn, state);
              if (get(newState) == 3) { // free
                put(newState, distance % 3);
                currentDistanceSize++;
              }
            }
      } else
        for (int state = 0; state < move.stateSize(); state++)
          if (get(state) == 3)
            for (final Turn turn : turns) {
              final int newState = move.turn(turn, state);
              if (get(newState) == lastDistanceValue) {
                put(state, distance % 3);
                currentDistanceSize++;
                break;
              }
            }
    }
    stateSize = totalSize;
    maxDistance = distance - 1;
  }

  public int distance(final int depth, final int state) {
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
    return depth - (depth - get(state) + 4) % 3 + 1;
    // (depth - get(state) + 4) / 3 * 3 + get(state) - 3;
  }

  public int startDistance(final int state) {
    //    d get(state)
    // last 0 1 2 3
    //    0 1 0 2 1
    //    1 2 1 0 2
    //    2 0 2 1 0
    int distance = 0;
    int lastCode = get(state);
    int lastState = state;
    for (int turnIndex = 0; turnIndex < turns.length; turnIndex++) {
      final int newState = move.turn(turns[turnIndex], lastState);
      if ((lastCode - get(newState) + 4) % 3 == 2) { // 2->1 1->0 0->2
        distance++;
        lastCode = (lastCode + 2) % 3; // 0->2 1->0 2->1
        lastState = newState; // restart loop for the new state
        turnIndex = -1;
      }
    }
    return distance;
  }

  public int maxDistance() {
    return maxDistance;
  }

  public int stateSize() {
    return stateSize;
  }

  private void put(final int state, final int val) {
    final int i = state >> 2;
    final int sh = state << 1 & 7;
    table[i] = (byte)(table[i] & ~(3 << sh) | val << sh);
  }

  private int get(final int state) {
    return table[state >> 2] >> (state << 1 & 7) & 3;
  }

  private static void logLine(final int depth, final int currentSize, final int totalSize,
      final int stateSize, final boolean forward) {
    System.out.printf("%2d%s %8d %10d %3d%%%n", depth, forward ? ">" : "<", currentSize, totalSize,
        (long)totalSize * 100 / stateSize);
  }
}
