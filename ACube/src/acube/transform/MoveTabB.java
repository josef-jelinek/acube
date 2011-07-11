package acube.transform;

import acube.TurnB;

public final class MoveTabB implements ToTabMove {
  private final ToTabMove move;

  public MoveTabB(ToTabMove move) {
    this.move = move;
  }

  public int turnN() {
    return TurnB.N;
  }

  public int stateN() {
    return move.stateN();
  }

  public int startN() {
    return move.startN();
  }

  public int start(int i) {
    return move.start(i);
  }

  public int turn(int turn, int state) {
    return move.turn(TurnB.toA(turn), state);
  }
}
