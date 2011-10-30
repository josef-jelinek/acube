package acube.prune;

import static java.lang.Math.max;
import acube.Reporter;
import acube.Turn;
import acube.transform.MoveTable2in1;
import acube.transform.TransformB;
import acube.transform.TurnTable;

public final class PruneB {
  private final PruneTable mEdgePos_cornerPos;
  private final PruneTable mEdgePos_oEdgePos;
  private final MoveTable2in1 moveTable_mEdgePos_cornerPos;
  private final MoveTable2in1 moveTable_mEdgePos_udEdgePos;

  public PruneB(final TransformB transform, final Reporter reporter) {
    moveTable_mEdgePos_cornerPos = new MoveTable2in1(transform.mEdgePosTable, transform.cornerPosTable);
    moveTable_mEdgePos_udEdgePos = new MoveTable2in1(transform.mEdgePosTable, transform.udEdgePosTable);
    reporter.tableCreationStarted("pruning table (middle edge position + corner position)");
    mEdgePos_cornerPos = createPruneTable(moveTable_mEdgePos_cornerPos);
    reporter.tableCreationStarted("pruning table (middle edge position + U/D edge position)");
    mEdgePos_oEdgePos = createPruneTable(moveTable_mEdgePos_udEdgePos);
  }

  private static PruneTable createPruneTable(final TurnTable moveTable) {
    return moveTable == null ? null : new PruneTable(moveTable, Turn.essentialValueSetB);
  }

  public int get_mEdgePos_cornerPos_startDist(final int mep, final int cp) {
    return mEdgePos_cornerPos.startDist(moveTable_mEdgePos_cornerPos.state(mep, cp));
  }

  public int get_mEdgePos_udEdgePos_startDist(final int mep, final int oep) {
    return mEdgePos_oEdgePos.startDist(moveTable_mEdgePos_udEdgePos.state(mep, oep));
  }

  public int get_mEdgePos_cornerPos_dist(final int lastDist, final int mep, final int cp) {
    return mEdgePos_cornerPos.dist(lastDist, moveTable_mEdgePos_cornerPos.state(mep, cp));
  }

  public int get_mEdgePos_udEdgePos_dist(final int lastDist, final int mep, final int oep) {
    return mEdgePos_oEdgePos.dist(lastDist, moveTable_mEdgePos_udEdgePos.state(mep, oep));
  }

  public int maxDist() {
    return max(mEdgePos_cornerPos.maxDist(), mEdgePos_oEdgePos.maxDist());
  }

  public int stateSize() {
    return mEdgePos_cornerPos.stateSize() + mEdgePos_oEdgePos.stateSize();
  }

  public int memorySize() {
    return mEdgePos_cornerPos.memorySize() + mEdgePos_oEdgePos.memorySize();
  }
}
