package acube.prune;

import static java.lang.Math.max;
import java.util.EnumSet;
import acube.Metric;
import acube.Reporter;
import acube.Turn;
import acube.transform.MoveTableComposed;
import acube.transform.Transform;

public final class Prune {
  private final PruneTable cornerTwist_edgeFlip;
  private final PruneTable cornerTwist_mEdgePosSet;
  private final PruneTable edgeFlip_mEdgePosSet;
  private final MoveTableComposed move_cornerTwist_edgeFlip;
  private final MoveTableComposed move_cornerTwist_mEdgePosSet;
  private final MoveTableComposed move_edgeFlip_mEdgePosSet;

  public Prune(final Transform transform, final Metric metric, final Reporter reporter) {
    final EnumSet<Turn> turns = Turn.valueSet; //metric.essentialTurns();
    move_cornerTwist_edgeFlip = new MoveTableComposed(transform.twistTable, transform.flipTable);
    move_cornerTwist_mEdgePosSet = new MoveTableComposed(transform.twistTable, transform.mEdgePosSetTable);
    move_edgeFlip_mEdgePosSet = new MoveTableComposed(transform.flipTable, transform.mEdgePosSetTable);
    reporter.tableCreationStarted("pruning table (corner twist + edge flip)");
    cornerTwist_edgeFlip = new PruneTable(move_cornerTwist_edgeFlip, turns);
    reporter.tableCreationStarted("pruning table (corner twist + middle edge position set)");
    cornerTwist_mEdgePosSet = new PruneTable(move_cornerTwist_mEdgePosSet, turns);
    reporter.tableCreationStarted("pruning table (edge flip + middle edge position set)");
    edgeFlip_mEdgePosSet = new PruneTable(move_edgeFlip_mEdgePosSet, turns);
  }

  public int get_twist_flip_startDist(final int ct, final int ef) {
    return cornerTwist_edgeFlip.startDist(move_cornerTwist_edgeFlip.state(ct, ef));
  }

  public int get_twist_mEdgePosSet_startDist(final int ct, final int meps) {
    return cornerTwist_mEdgePosSet.startDist(move_cornerTwist_mEdgePosSet.state(ct, meps));
  }

  public int get_flip_mEdgePosSet_startDist(final int ef, final int meps) {
    return edgeFlip_mEdgePosSet.startDist(move_edgeFlip_mEdgePosSet.state(ef, meps));
  }

  public int get_twist_flip_dist(final int lastDistance, final int ct, final int ef) {
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
