package acube.prune;

import static java.lang.Math.max;
import java.util.EnumSet;
import acube.Metric;
import acube.Reporter;
import acube.Turn;
import acube.transform.MoveTableComposed;
import acube.transform.TransformB;

public final class PruneB {
  private final PruneTable mEdgePos_cornerPos;
  private final PruneTable mEdgePos_oEdgePos;
  private final MoveTableComposed move_mEdgePos_cornerPos;
  private final MoveTableComposed move_mEdgePos_oEdgePos;

  public PruneB(final TransformB transform, final Metric metric, final Reporter reporter) {
    final EnumSet<Turn> turns = Turn.getValidB(Turn.valueSet); // metric.essentialTurnsB();
    move_mEdgePos_cornerPos = new MoveTableComposed(transform.mEdgePosTable, transform.cornerPosTable);
    move_mEdgePos_oEdgePos = new MoveTableComposed(transform.mEdgePosTable, transform.udEdgePosTable);
    reporter.tableCreationStarted("pruning table (middle edge position + corner position)");
    mEdgePos_cornerPos = new PruneTable(move_mEdgePos_cornerPos, turns);
    reporter.tableCreationStarted("pruning table (middle edge position + U/D edge position)");
    mEdgePos_oEdgePos = new PruneTable(move_mEdgePos_oEdgePos, turns);
  }

  public int get_mEdgePos_cornerPos_startDist(final int mep, final int cp) {
    return mEdgePos_cornerPos.startDist(move_mEdgePos_cornerPos.state(mep, cp));
  }

  public int get_mEdgePos_oEdgePos_startDist(final int mep, final int oep) {
    return mEdgePos_oEdgePos.startDist(move_mEdgePos_oEdgePos.state(mep, oep));
  }

  public int get_mEdgePos_cornerPos_dist(final int lastDistance, final int mep, final int cp) {
    return mEdgePos_cornerPos.dist(lastDistance, move_mEdgePos_cornerPos.state(mep, cp));
  }

  public int get_mEdgePos_oEdgePos_dist(final int lastDistance, final int mep, final int oep) {
    return mEdgePos_oEdgePos.dist(lastDistance, move_mEdgePos_oEdgePos.state(mep, oep));
  }

  public int maxDistance() {
    return max(mEdgePos_cornerPos.maxDist(), mEdgePos_oEdgePos.maxDist());
  }

  public int stateSize() {
    return mEdgePos_cornerPos.stateSize() + mEdgePos_oEdgePos.stateSize();
  }

  public int memorySize() {
    return mEdgePos_cornerPos.memorySize() + mEdgePos_oEdgePos.memorySize();
  }
}
