package acube.transform;

import acube.Corner;
import acube.Edge;
import acube.Turn;
import acube.pack.Pack;

abstract class Move implements ITableMove {
  protected final Pack pack;
  private final int stateSize;
  private final Turn[] turns;

  protected Move(final Pack pack, final Turn[] turns) {
    this.pack = pack;
    stateSize = pack.size();
    this.turns = turns.clone();
  }

  @Override
  public Turn[] turns() {
    return turns.clone();
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

  protected final void swap(final Edge edge1, final Edge edge2) {
    pack.swap(edgeIndex(edge1), edgeIndex(edge2));
  }

  protected final void
      cycle(final Edge edge1, final Edge edge2, final Edge edge3, final Edge edge4) {
    pack.cycle(edgeIndex(edge1), edgeIndex(edge2), edgeIndex(edge3), edgeIndex(edge4));
  }

  protected final void cycle(final Corner corner1, final Corner corner2, final Corner corner3,
      final Corner corner4) {
    pack.cycle(cornerIndex(corner1), cornerIndex(corner2), cornerIndex(corner3),
        cornerIndex(corner4));
  }

  protected int edgeIndex(final Edge edge) {
    return edge.ordinal();
  }

  protected int cornerIndex(final Corner corner) {
    return corner.ordinal();
  }
}
