package acube.transform;

import java.util.Arrays;
import java.util.Set;
import acube.Turn;

public final class MoveTableComposed implements TurnTable {
  private final TurnTable move1;
  private final TurnTable move2;
  private final int stateSize1;
  private final int stateSize2;
  private final int startSize1;
  private final int startSize2;

  public MoveTableComposed(final TurnTable move1, final TurnTable move2) {
    if (!Arrays.equals(move1.turnMaskArray(), move2.turnMaskArray()))
      throw new IllegalArgumentException("Incompatible tables");
    this.move1 = move1;
    this.move2 = move2;
    stateSize1 = move1.stateSize();
    stateSize2 = move2.stateSize();
    startSize1 = move1.startSize();
    startSize2 = move2.startSize();
  }

  @Override
  public int stateSize() {
    return stateSize1 * stateSize2;
  }

  @Override
  public int memorySize() {
    return move1.memorySize() + move2.memorySize();
  }

  @Override
  public int startSize() {
    return startSize1 * startSize2;
  }

  @Override
  public int start(final int i) {
    return state(move1.start(i / startSize2), move2.start(i % startSize2));
  }

  // TODO: this is a hotspot - figure out whether it can be optimized / inlined
  @Override
  public int turn(final Turn turn, final int state) {
    return state(move1.turn(turn, state / stateSize2), move2.turn(turn, state % stateSize2));
  }

  public int state(final int state1, final int state2) {
    return state1 * stateSize2 + state2;
  }

  @Override
  public Set<Turn> turnMask() {
    return move1.turnMask();
  }

  @Override
  public Turn[] turnMaskArray() {
    return move1.turnMaskArray();
  }
}
