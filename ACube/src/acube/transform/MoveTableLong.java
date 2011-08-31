package acube.transform;

import acube.Turn;

final class MoveTableLong extends MoveTable {
  private final int[][] table;

  public MoveTableLong(final TurnTable move, final Turn[][] base) {
    super(move);
    table = new int[Turn.values.length][];
    fill(base);
  }

  @Override
  public int memorySize() {
    return table.length * stateSize() * 4;
  }

  @Override
  public int turn(final Turn turn, final int stateIndex) {
    return table[turn.ordinal()][stateIndex];
  }

  @Override
  protected void set(final Turn turn, final int stateIndex, final int newStateIndex) {
    if (table[turn.ordinal()] == null)
      table[turn.ordinal()] = new int[stateSize()];
    table[turn.ordinal()][stateIndex] = newStateIndex;
  }
}
