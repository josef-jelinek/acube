package acube.transform;

import acube.Turn;

final class MoveTableLong extends MoveTable {
  private final int[][] turn;

  public MoveTableLong(final TurnTable move, final Turn[][][] base) {
    super(move);
    turn = new int[move.turns().length][stateSize()];
    fill(base);
  }

  @Override
  public int memorySize() {
    return turn.length * stateSize() * 4;
  }

  @Override
  public int turn(final Turn turn, final int stateIndex) {
    return this.turn[turnIndices[turn.ordinal()]][stateIndex];
  }

  @Override
  protected void set(final int turnIndex, final int stateIndex, final int newStateIndex) {
    turn[turnIndex][stateIndex] = newStateIndex;
  }
}
