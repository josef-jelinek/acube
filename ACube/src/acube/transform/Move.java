package acube.transform;

import static acube.Corner.DBR;
import static acube.Corner.DFL;
import static acube.Corner.DLB;
import static acube.Corner.DRF;
import static acube.Corner.UBL;
import static acube.Corner.UFR;
import static acube.Corner.ULF;
import static acube.Corner.URB;
import static acube.Edge.BL;
import static acube.Edge.BR;
import static acube.Edge.DB;
import static acube.Edge.DF;
import static acube.Edge.DL;
import static acube.Edge.DR;
import static acube.Edge.FL;
import static acube.Edge.FR;
import static acube.Edge.UB;
import static acube.Edge.UF;
import static acube.Edge.UL;
import static acube.Edge.UR;
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
  public int turn(final Turn turn, final int state) { // template method
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

  protected final boolean areUsedIn(final boolean[] allowedMask) {
    return pack.areUsedIn(allowedMask);
  }

  public final int convertFrom(final Move move) {
    pack.convert(move.pack);
    return pack();
  }

  public final int convertFrom(final Move move1, final Move move2) {
    pack.convert(move1.pack);
    pack.combine(move2.pack);
    return pack();
  }

  protected final void cycleEdges(final Turn turn) {
    switch (turn) {
    case U1:
      cycle(UF, UR, UB, UL);
      break;
    case D1:
      cycle(DF, DL, DB, DR);
      break;
    case F1:
      cycle(FL, DF, FR, UF);
      break;
    case B1:
      cycle(BR, DB, BL, UB);
      break;
    case L1:
      cycle(BL, DL, FL, UL);
      break;
    case R1:
      cycle(FR, DR, BR, UR);
      break;
    default:
      throw new IllegalArgumentException("Unsupported or non primitive turn");
    }
  }

  protected final void cycleCorners(final Turn turn) {
    switch (turn) {
    case U1:
      cycle(URB, UBL, ULF, UFR);
      break;
    case D1:
      cycle(DFL, DLB, DBR, DRF);
      break;
    case F1:
      cycle(DFL, DRF, UFR, ULF);
      break;
    case B1:
      cycle(DBR, DLB, UBL, URB);
      break;
    case L1:
      cycle(DLB, DFL, ULF, UBL);
      break;
    case R1:
      cycle(DRF, DBR, URB, UFR);
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

  @Override
  public String toString() {
    return pack.toString();
  }
}
