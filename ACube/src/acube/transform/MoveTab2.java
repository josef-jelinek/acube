package acube.transform;

public final class MoveTab2 implements ToTabMove {
  private final ToTabMove move1;
  private final ToTabMove move2;
  private final int n1, n2;
  private final int s1, s2;

  public MoveTab2(ToTabMove move1, ToTabMove move2) {
    if (move1.turnN() != move2.turnN()) throw new IllegalArgumentException("Turn numbers do not match");
    this.move1 = move1;
    this.move2 = move2;
    n1 = move1.stateN();
    n2 = move2.stateN();
    s1 = move1.startN();
    s2 = move2.startN();
  }

  public int turnN() {
    return move1.turnN();
  }

  public int stateN() {
    return n1 * n2;
  }

  public int startN() {
    return s1 * s2;
  }

  public int start(int i) {
    return state(move1.start(i / s2), move2.start(i % s2));
  }

  public int turn(int turn, int state) {
    return state(move1.turn(turn, state / n2), move2.turn(turn, state % n2));
  }

  public int state(int state1, int state2) {
    return state1 * n2 + state2;
  }
}
