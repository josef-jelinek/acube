package acube.prune;

import static java.lang.Math.max;
import acube.transform.MoveTableComposed;
import acube.transform.Transform;

public final class Prune {
  private final PruneTable cornerTwist_edgeFlip;
  private final PruneTable cornerTwist_mEdgePosSet;
  private final PruneTable edgeFlip_mEdgePosSet;
  private final MoveTableComposed move_cornerTwist_edgeFlip;
  private final MoveTableComposed move_cornerTwist_mEdgePosSet;
  private final MoveTableComposed move_edgeFlip_mEdgePosSet;

  public Prune(final Transform transform) {
    move_cornerTwist_edgeFlip = new MoveTableComposed(transform.twist, transform.edgeFlip);
    move_cornerTwist_mEdgePosSet = new MoveTableComposed(transform.twist, transform.mEdgePosSet);
    move_edgeFlip_mEdgePosSet = new MoveTableComposed(transform.edgeFlip, transform.mEdgePosSet);
    cornerTwist_edgeFlip = new PruneTable(move_cornerTwist_edgeFlip);
    cornerTwist_mEdgePosSet = new PruneTable(move_cornerTwist_mEdgePosSet);
    edgeFlip_mEdgePosSet = new PruneTable(move_edgeFlip_mEdgePosSet);
  }

  public int get_cornerTwist_edgeFlip_startDistance(final int ct, final int ef) {
    return cornerTwist_edgeFlip.startDistance(move_cornerTwist_edgeFlip.state(ct, ef));
  }

  public int get_cornerTwist_mEdgePosSet_startDistance(final int ct, final int meps) {
    return cornerTwist_mEdgePosSet.startDistance(move_cornerTwist_mEdgePosSet.state(ct, meps));
  }

  public int get_edgeFlip_mEdgePosSet_startDistance(final int ef, final int meps) {
    return edgeFlip_mEdgePosSet.startDistance(move_edgeFlip_mEdgePosSet.state(ef, meps));
  }

  public int get_cornerTwist_edgeFlip_distance(final int lastDistance, final int ct, final int ef) {
    return cornerTwist_edgeFlip.distance(lastDistance, move_cornerTwist_edgeFlip.state(ct, ef));
  }

  public int get_cornerTwist_mEdgePosSet_distance(final int lastDistance, final int ct, final int meps) {
    return cornerTwist_mEdgePosSet.distance(lastDistance, move_cornerTwist_mEdgePosSet.state(ct, meps));
  }

  public int get_edgeFlip_mEdgePosSet_distance(final int lastDistance, final int ef, final int meps) {
    return edgeFlip_mEdgePosSet.distance(lastDistance, move_edgeFlip_mEdgePosSet.state(ef, meps));
  }

  public int maxDistance() {
    return max(cornerTwist_edgeFlip.maxDistance(),
        max(cornerTwist_mEdgePosSet.maxDistance(), edgeFlip_mEdgePosSet.maxDistance()));
  }

  public int stateSize() {
    return cornerTwist_edgeFlip.stateSize() + cornerTwist_mEdgePosSet.stateSize() + edgeFlip_mEdgePosSet.stateSize();
  }

  public int memorySize() {
    return cornerTwist_edgeFlip.memorySize() + cornerTwist_mEdgePosSet.memorySize() + edgeFlip_mEdgePosSet.memorySize();
  }
}
