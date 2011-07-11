package acube.transform;

public abstract class MoveTab implements ToTabMove {
  private final ToFormMove move;

  static final MoveTab obj(ToFormMove move) {
    if (move.stateN() <= 65536) return new Tab16(move);
    return new Tab32(move);
  }

  MoveTab(ToFormMove move) {
    this.move = move;
  }

  public int turnN() {
    return move.turnN();
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

  abstract void set(int turn, int state, int newstate);

  final void fill() { // template method (uses turn(), set())
    int n = move.stateN();
    int[][] turnBase = move.turnBase();
    for (int k = 0; k < turnBase.length; k++) {
      for (int i = 0; i < n; i++) {
        int t = (turnBase[k].length == 1) ? move.turn(turnBase[k][0], i) : i;
        for (int j = 1; j < turnBase[k].length; j++)
          t = turn(turnBase[k][j], t);
        set(turnBase[k][0], i, t);
      }
    }
  }

  private static final class Tab16 extends MoveTab {
    private final short[][] turn;

    Tab16(ToFormMove move) {
      super(move);
      turn = new short[turnN()][stateN()];
      fill();
    }

    public int turn(int turn, int state) {
      return this.turn[turn][state] & 0xFFFF;
    }

    void set(int turn, int state, int newstate) {
      this.turn[turn][state] = (short)newstate;
    }
  }

  private static final class Tab32 extends MoveTab {
    private int[][] turn;

    Tab32(ToFormMove move) {
      super(move);
      turn = new int[turnN()][stateN()];
      fill();
    }

    public int turn(int turn, int state) {
      return this.turn[turn][state];
    }

    void set(int turn, int state, int newstate) {
      this.turn[turn][state] = newstate;
    }
  }
}
