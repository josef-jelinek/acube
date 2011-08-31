package acube.transform;

import acube.Turn;

abstract class MoveTable implements TurnTable {
  private final TurnTable move;

  static final MoveTable instance(final TurnTable move, final Turn[][] base) {
    return move.stateSize() <= 65536 ? new MoveTableShort(move, base) : new MoveTableLong(move, base);
  }

  protected MoveTable(final TurnTable move) {
    this.move = move;
  }

  @Override
  public final int stateSize() {
    return move.stateSize();
  }

  @Override
  public final int startSize() {
    return move.startSize();
  }

  @Override
  public final int start(final int i) {
    return move.start(i);
  }

  protected abstract void set(Turn turn, int state, int newstate);

  protected final void fill(final Turn[][] base) { // template method
    for (final Turn turn : Turn.values) {
      final Turn[] genTurns = getGenTurns(turn, base);
      if (genTurns != null)
        for (int stateIndex = 0; stateIndex < move.stateSize(); stateIndex++) {
          int newStateIndex = stateIndex;
          for (int i = 1; i < genTurns.length; i++)
            newStateIndex = move.turn(genTurns[i], newStateIndex);
          set(turn, stateIndex, newStateIndex);
        }
    }
  }

  private Turn[] getGenTurns(final Turn turn, final Turn[][] base) {
    for (final Turn[] row : base)
      if (turn.equals(row[0]))
        return row;
    return null;
  }
}
