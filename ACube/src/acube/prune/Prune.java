package acube.prune;

import static java.lang.Math.max;
import acube.Reporter;
import acube.transform.MoveTableComposed;
import acube.transform.Transform;

public final class Prune {
  private final PruneTable cornerTwist_edgeFlip;
  private final PruneTable cornerTwist_mEdgePosSet;
  private final PruneTable edgeFlip_mEdgePosSet;
  private final MoveTableComposed move_cornerTwist_edgeFlip;
  private final MoveTableComposed move_cornerTwist_mEdgePosSet;
  private final MoveTableComposed move_edgeFlip_mEdgePosSet;

  public Prune(final Transform transform, final Reporter reporter) {
    move_cornerTwist_edgeFlip = new MoveTableComposed(transform.twist, transform.flip);
    move_cornerTwist_mEdgePosSet = new MoveTableComposed(transform.twist, transform.mEdgePosSet);
    move_edgeFlip_mEdgePosSet = new MoveTableComposed(transform.flip, transform.mEdgePosSet);
    reporter.tableCreationStarted("pruning table (corner twist + edge flip)");
    cornerTwist_edgeFlip = new PruneTable(move_cornerTwist_edgeFlip);
    reporter.tableCreationStarted("pruning table (corner twist + middle edge position set)");
    cornerTwist_mEdgePosSet = new PruneTable(move_cornerTwist_mEdgePosSet);
    reporter.tableCreationStarted("pruning table (edge flip + middle edge position set)");
    edgeFlip_mEdgePosSet = new PruneTable(move_edgeFlip_mEdgePosSet);
  }

  public int get_cornerTwist_edgeFlip_startDistance(final int ct, final int ef) {
    return cornerTwist_edgeFlip.startDist(move_cornerTwist_edgeFlip.state(ct, ef));
  }

  public int get_cornerTwist_mEdgePosSet_startDistance(final int ct, final int meps) {
    return cornerTwist_mEdgePosSet.startDist(move_cornerTwist_mEdgePosSet.state(ct, meps));
  }

  public int get_edgeFlip_mEdgePosSet_startDistance(final int ef, final int meps) {
    return edgeFlip_mEdgePosSet.startDist(move_edgeFlip_mEdgePosSet.state(ef, meps));
  }

  public int get_cornerTwist_edgeFlip_distance(final int lastDistance, final int ct, final int ef) {
    return cornerTwist_edgeFlip.dist(lastDistance, move_cornerTwist_edgeFlip.state(ct, ef));
  }

  public int get_twist_mEdgePosSet_dist(final int lastDistance, final int ct, final int meps) {
    return cornerTwist_mEdgePosSet.dist(lastDistance, move_cornerTwist_mEdgePosSet.state(ct, meps));
  }

  public int get_flip_mEdgePosSet_dist(final int lastDistance, final int ef, final int meps) {
    return edgeFlip_mEdgePosSet.dist(lastDistance, move_edgeFlip_mEdgePosSet.state(ef, meps));
  }

  public int maxDistance() {
    return max(cornerTwist_edgeFlip.maxDist(),
        max(cornerTwist_mEdgePosSet.maxDist(), edgeFlip_mEdgePosSet.maxDist()));
  }

  public int stateSize() {
    return cornerTwist_edgeFlip.stateSize() + cornerTwist_mEdgePosSet.stateSize() + edgeFlip_mEdgePosSet.stateSize();
  }

  public int memorySize() {
    return cornerTwist_edgeFlip.memorySize() + cornerTwist_mEdgePosSet.memorySize() + edgeFlip_mEdgePosSet.memorySize();
  }
}
