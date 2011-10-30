package acube.transform;

import acube.Turn;

public abstract class MoveTable implements TurnTable {
  private final TurnTable move;

  static final MoveTable instance(final TurnTable move, final Turn[][] base, final Turn[][] same) {
    return move.stateSize() <= 65536 ? new MoveTableShort(move, base, same) : new MoveTableLong(move, base, same);
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

  @Override
  public String key() {
    return move.key();
  }

  protected abstract void set(Turn turn, int state, int newstate);

  protected abstract void set(Turn turn, Turn turn2);

  protected final void fill(final Turn[][] base, final Turn[][] same) { // template method
    for (final Turn turn : Turn.values) {
      final Turn[] genTurns = getGenTurns(turn, base);
      if (genTurns != null)
        for (int stateIndex = 0; stateIndex < move.stateSize(); stateIndex++) {
          int newStateIndex = stateIndex;
          for (int i = 1; i < genTurns.length; i++)
            newStateIndex = move.turn(genTurns[i], newStateIndex);
          set(turn, stateIndex, newStateIndex);
        }
      final Turn[] sameTurns = getSameTurns(turn, same);
      if (sameTurns != null)
        for (final Turn sameTurn : sameTurns)
          set(turn, sameTurn);
    }
  }

  private Turn[] getGenTurns(final Turn turn, final Turn[][] base) {
    for (final Turn[] row : base)
      if (turn.equals(row[0]))
        return row;
    return null;
  }

  private Turn[] getSameTurns(final Turn turn, final Turn[][] same) {
    for (final Turn[] row : same)
      if (turn.equals(row[0]))
        return row;
    return null;
  }
}
