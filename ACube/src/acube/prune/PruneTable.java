package acube.prune;

import acube.Turn;
import acube.transform.TurnTable;

public final class PruneTable {
  private final byte[] table;
  private final int stateSize;
  private final TurnTable move;
  private final int maxDistance;

  public PruneTable(final TurnTable move) {
    this.move = move;
    table = new byte[(move.stateSize() + 3) / 4];
    int lastFilledSize = startDistanceFill();
    int totalSize = 0;
    int distance = 0;
    final int switchSize = move.stateSize() / 2;
    while (lastFilledSize > 0) {
      logLine(distance, lastFilledSize, totalSize + lastFilledSize, move.stateSize(), totalSize < switchSize);
      totalSize += lastFilledSize;
      final int lastDistance = distance++ % 3;
      lastFilledSize =
          totalSize < switchSize ? forwardDistanceFill(distance, lastDistance) : backwardDistanceFill(distance,
              lastDistance);
    }
    stateSize = totalSize;
    maxDistance = distance - 1;
  }

  private int startDistanceFill() {
    for (int i = 0; i < table.length; i++)
      table[i] = -1;
    int filled = 0;
    while (filled < move.startSize())
      put(move.start(filled++), 0);
    return filled;
  }

  private int forwardDistanceFill(final int distance, final int lastDistance) {
    int filled = 0;
    for (int state = 0; state < move.stateSize(); state++)
      if (get(state) == lastDistance)
        for (final Turn turn : move.turns()) {
          final int newState = move.turn(turn, state);
          if (isNotInitialized(newState)) { // free
            put(newState, distance % 3);
            filled++;
          }
        }
    return filled;
  }

  private int backwardDistanceFill(final int distance, final int lastDistanceValue) {
    int filled = 0;
    for (int state = 0; state < move.stateSize(); state++)
      if (isNotInitialized(state))
        for (final Turn turn : move.turns())
          if (get(move.turn(turn, state)) == lastDistanceValue) {
            put(state, distance % 3);
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

  public int distance(final int lastDepth, final int newState) {
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

  public int startDistance(final int state) {
    //    d get(state)
    // last 0 1 2 3
    //    0 1 0 2 1
    //    1 2 1 0 2
    //    2 0 2 1 0
    int distance = 0;
    int lastCode = get(state);
    int lastState = state;
    for (int turnIndex = 0; turnIndex < move.turns().length; turnIndex++) {
      final int newState = move.turn(move.turns()[turnIndex], lastState);
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

  public int memorySize() {
    return table.length;
  }

  private static void logLine(final int depth, final int current, final int soFar, final int total,
      final boolean forward) {
    System.out.printf("%2d%s %8d %10d %3d%%%n", depth, forward ? ">" : "<", current, soFar, (long)soFar * 100 / total);
  }
}
