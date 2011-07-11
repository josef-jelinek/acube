package acube.transform;

import acube.pack.Pack;
import acube.pack.PackTwist;

abstract class Move implements ToFormMove {
  private final Pack pack;
  private final int stateN;
  private final int turnN;
  private final int[][] turnBase;

  Move(Pack pack, int turnN, int[][] turnBase) {
    this.pack = pack;
    this.stateN = pack.len();
    this.turnN = turnN;
    this.turnBase = turnBase;
  }

  public int stateN() {
    return stateN;
  }

  public int turnN() {
    return turnN;
  }

  public int[][] turnBase() {
    return turnBase;
  }

  final void unpack(int i) {
    pack.unpack(i);
  }

  final int pack() {
    return pack.pack();
  }

  abstract void turn(int t);

  public int turn(int t, int i) { // template method (uses pack(), unpack(), turn())
    unpack(i);
    turn(t);
    return pack();
  }

  public int startN() {
    return pack.startLen();
  }

  public int start(int i) {
    return pack.start(i);
  }
}

abstract class MovePos extends Move {
  final Pack pack;

  MovePos(Pack pack, int turnN, int[][] turnBase) {
    super(pack, turnN, turnBase);
    this.pack = pack;
  }
}

abstract class MoveTwist extends Move {
  final PackTwist pack;

  MoveTwist(PackTwist pack, int turnN, int[][] turnBase) {
    super(pack, turnN, turnBase);
    this.pack = pack;
  }
}
