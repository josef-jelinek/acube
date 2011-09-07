package acube.prune;

import static java.lang.Math.max;
import java.util.EnumSet;
import acube.Reporter;
import acube.Turn;
import acube.transform.MoveTableComposed;
import acube.transform.TransformB;

public final class PruneB {
  private final PruneTable mEdgePos_cornerPos;
  private final PruneTable mEdgePos_oEdgePos;
  private final MoveTableComposed moveTable_mEdgePos_cornerPos;
  private final MoveTableComposed moveTable_mEdgePos_udEdgePos;

  public PruneB(final TransformB transform, final Reporter reporter) {
    final EnumSet<Turn> turns = Turn.essentialValueSetB;
    moveTable_mEdgePos_cornerPos = new MoveTableComposed(transform.mEdgePosTable, transform.cornerPosTable);
    moveTable_mEdgePos_udEdgePos = new MoveTableComposed(transform.mEdgePosTable, transform.udEdgePosTable);
    reporter.tableCreationStarted("pruning table (middle edge position + corner position)");
    mEdgePos_cornerPos = new PruneTable(moveTable_mEdgePos_cornerPos, turns);
    reporter.tableCreationStarted("pruning table (middle edge position + U/D edge position)");
    mEdgePos_oEdgePos = new PruneTable(moveTable_mEdgePos_udEdgePos, turns);
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
