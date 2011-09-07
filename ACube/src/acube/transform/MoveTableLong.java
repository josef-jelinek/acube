package acube.transform;

import acube.Turn;

final class MoveTableLong extends MoveTable {
  private final int[][] table;

  public MoveTableLong(final TurnTable move, final Turn[][] base, final Turn[][] same) {
    super(move);
    table = new int[Turn.values.length][];
    fill(base, same);
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

  @Override
  protected void set(final Turn turn, final Turn turn2) {
    table[turn2.ordinal()] = table[turn.ordinal()];
  }
}
