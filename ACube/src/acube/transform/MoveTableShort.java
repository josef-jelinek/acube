package acube.transform;

import acube.Turn;

final class MoveTableShort extends MoveTable {
  private final short[][] turn;

  public MoveTableShort(final TurnTable move, final Turn[][][] base) {
    super(move);
    turn = new short[move.turnMask().size()][stateSize()];
    fill(base);
  }

  @Override
  public int memorySize() {
    return turn.length * stateSize() * 2;
  }

  @Override
  public int turn(final Turn turn, final int state) {
    return this.turn[turnIndices[turn.ordinal()]][state] & 0xFFFF;
  }

  @Override
  protected void set(final int turn, final int state, final int newstate) {
    this.turn[turn][state] = (short)newstate;
  }
}
