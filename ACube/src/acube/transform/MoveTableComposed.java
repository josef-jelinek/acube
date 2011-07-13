package acube.transform;

import java.util.Arrays;
import acube.Turn;

public final class MoveTableComposed implements ITableMove {
  private final ITableMove move1;
  private final ITableMove move2;
  private final int n1, n2;
  private final int s1, s2;

  public MoveTableComposed(final ITableMove move1, final ITableMove move2) {
    if (!Arrays.equals(move1.turns(), move2.turns()))
      throw new IllegalArgumentException("Incompatible tables");
    this.move1 = move1;
    this.move2 = move2;
    n1 = move1.stateSize();
    n2 = move2.stateSize();
    s1 = move1.startSize();
    s2 = move2.startSize();
  }

  @Override
  public int stateSize() {
    return n1 * n2;
  }

  @Override
  public int startSize() {
    return s1 * s2;
  }

  @Override
  public int start(final int i) {
    return state(move1.start(i / s2), move2.start(i % s2));
  }

  @Override
  public int turn(final Turn turn, final int state) {
    return state(move1.turn(turn, state / n2), move2.turn(turn, state % n2));
  }

  public int state(final int state1, final int state2) {
    return state1 * n2 + state2;
  }

  @Override
  public Turn[] turns() {
    return move1.turns();
  }
}
