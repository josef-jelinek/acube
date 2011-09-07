package acube.transform;

import acube.Turn;

final class MoveTableShort extends MoveTable {
  private final short[][] table;

  public MoveTableShort(final TurnTable move, final Turn[][] base, final Turn[][] same) {
    super(move);
    table = new short[Turn.values.length][];
    fill(base, same);
  }

  @Override
  public int memorySize() {
    return table.length * stateSize() * 2;
  }

  @Override
  public int turn(final Turn turn, final int state) {
    return table[turn.ordinal()][state] & 0xFFFF;
  }

  @Override
  protected void set(final Turn turn, final int state, final int newstate) {
    if (table[turn.ordinal()] == null)
      table[turn.ordinal()] = new short[stateSize()];
    table[turn.ordinal()][state] = (short)newstate;
  }

  @Override
  protected void set(final Turn turn, final Turn turn2) {
    table[turn2.ordinal()] = table[turn.ordinal()];
  }
}
