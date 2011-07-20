package acube.transform;

import acube.Corner;
import acube.Edge;
import acube.Turn;
import acube.pack.Pack;

abstract class Move implements TurnTable {
  protected final Pack pack;
  private final int stateSize;
  private final Turn[] turns;

  protected Move(final Pack pack, final Turn[] turns) {
    this.pack = pack;
    stateSize = pack.size();
    this.turns = turns;
  }

  @Override
  public Turn[] turns() {
    return turns;
  }

  @Override
  public int turn(final Turn turn, final int state) { // template method (uses pack(), unpack(), turn())
    unpack(state);
    turn(turn);
    return pack();
  }

  @Override
  public int stateSize() {
    return stateSize;
  }

  @Override
  public int memorySize() {
    throw new RuntimeException("Auxiliary object");
  }

  @Override
  public int startSize() {
    return pack.startSize();
  }

  @Override
  public int start(final int i) {
    return pack.start(i);
  }

  protected final void unpack(final int i) {
    pack.unpack(i);
  }

  protected final int pack() {
    return pack.pack();
  }

  abstract void turn(Turn turn);

  protected final void cycleEdges(final Turn turn) {
    switch (turn) {
    case U1:
      cycle(Edge.UF, Edge.UR, Edge.UB, Edge.UL);
      break;
    case D1:
      cycle(Edge.DF, Edge.DL, Edge.DB, Edge.DR);
      break;
    case F1:
      cycle(Edge.FL, Edge.DF, Edge.FR, Edge.UF);
      break;
    case B1:
      cycle(Edge.BR, Edge.DB, Edge.BL, Edge.UB);
      break;
    case L1:
      cycle(Edge.BL, Edge.DL, Edge.FL, Edge.UL);
      break;
    case R1:
      cycle(Edge.FR, Edge.DR, Edge.BR, Edge.UR);
      break;
    default:
      throw new IllegalArgumentException("Unsupported or non primitive turn");
    }
  }

  protected final void cycleCorners(final Turn turn) {
    switch (turn) {
    case U1:
      cycle(Corner.URB, Corner.UBL, Corner.ULF, Corner.UFR);
      break;
    case D1:
      cycle(Corner.DFL, Corner.DLB, Corner.DBR, Corner.DRF);
      break;
    case F1:
      cycle(Corner.DFL, Corner.DRF, Corner.UFR, Corner.ULF);
      break;
    case B1:
      cycle(Corner.DBR, Corner.DLB, Corner.UBL, Corner.URB);
      break;
    case L1:
      cycle(Corner.DLB, Corner.DFL, Corner.ULF, Corner.UBL);
      break;
    case R1:
      cycle(Corner.DRF, Corner.DBR, Corner.URB, Corner.UFR);
      break;
    default:
      throw new IllegalArgumentException("Unsupported or non primitive turn");
    }
  }

  protected final void swap(final Edge e1, final Edge e2) {
    pack.swap(getIndex(e1), getIndex(e2));
  }

  protected final void cycle(final Edge e1, final Edge e2, final Edge e3, final Edge e4) {
    pack.cycle(getIndex(e1), getIndex(e2), getIndex(e3), getIndex(e4));
  }

  protected final void cycle(final Corner c1, final Corner c2, final Corner c3, final Corner c4) {
    pack.cycle(getIndex(c1), getIndex(c2), getIndex(c3), getIndex(c4));
  }

  protected int getIndex(final Corner c) {
    return c.ordinal();
  }

  protected int getIndex(final Edge e) {
    return e.ordinal();
  }
}
