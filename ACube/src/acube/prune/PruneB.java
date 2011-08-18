package acube.prune;

import static java.lang.Math.max;
import acube.transform.MoveTableComposed;
import acube.transform.TransformB;

public final class PruneB {
  private final PruneTable mEdgePos_cornerPos;
  private final PruneTable mEdgePos_oEdgePos;
  private final MoveTableComposed move_mEdgePos_cornerPos;
  private final MoveTableComposed move_mEdgePos_oEdgePos;

  public PruneB(final TransformB transform) {
    move_mEdgePos_cornerPos = new MoveTableComposed(transform.mEdgePos, transform.cornerPos);
    move_mEdgePos_oEdgePos = new MoveTableComposed(transform.mEdgePos, transform.oEdgePos);
    mEdgePos_cornerPos = new PruneTable(move_mEdgePos_cornerPos);
    mEdgePos_oEdgePos = new PruneTable(move_mEdgePos_oEdgePos);
  }

  public int get_mEdgePos_cornerPos_startDistance(final int mep, final int cp) {
    return mEdgePos_cornerPos.startDistance(move_mEdgePos_cornerPos.state(mep, cp));
  }

  public int get_mEdgePos_oEdgePos_startDistance(final int mep, final int oep) {
    return mEdgePos_oEdgePos.startDistance(move_mEdgePos_oEdgePos.state(mep, oep));
  }

  public int get_mEdgePos_cornerPos_distance(final int lastDistance, final int mep, final int cp) {
    return mEdgePos_cornerPos.distance(lastDistance, move_mEdgePos_cornerPos.state(mep, cp));
  }

  public int get_mEdgePos_oEdgePos_distance(final int lastDistance, final int mep, final int oep) {
    return mEdgePos_oEdgePos.distance(lastDistance, move_mEdgePos_oEdgePos.state(mep, oep));
  }

  public int maxDistance() {
    return max(mEdgePos_cornerPos.maxDistance(), mEdgePos_oEdgePos.maxDistance());
  }

  public int stateSize() {
    return mEdgePos_cornerPos.stateSize() + mEdgePos_oEdgePos.stateSize();
  }

  public int memorySize() {
    return mEdgePos_cornerPos.memorySize() + mEdgePos_oEdgePos.memorySize();
  }
}
