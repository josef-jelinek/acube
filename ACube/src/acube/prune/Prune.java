package acube.prune;

import static java.lang.Math.max;
import java.util.EnumSet;
import acube.Reporter;
import acube.Turn;
import acube.transform.MoveTableComposed;
import acube.transform.Transform;

public final class Prune {
  private final PruneTable twist_flip;
  private final PruneTable twist_mEdgePosSet;
  private final PruneTable flip_mEdgePosSet;
  private final MoveTableComposed moveTable_twist_flip;
  private final MoveTableComposed moveTable_twist_mEdgePosSet;
  private final MoveTableComposed moveTable_flip_mEdgePosSet;

  public Prune(final Transform transform, final Reporter reporter) {
    final EnumSet<Turn> turns = Turn.essentialValueSet;
    moveTable_twist_flip = new MoveTableComposed(transform.twistTable, transform.flipTable);
    moveTable_twist_mEdgePosSet = new MoveTableComposed(transform.twistTable, transform.mEdgePosSetTable);
    moveTable_flip_mEdgePosSet = new MoveTableComposed(transform.flipTable, transform.mEdgePosSetTable);
    reporter.tableCreationStarted("pruning table (corner twist + edge flip)");
    twist_flip = new PruneTable(moveTable_twist_flip, turns);
    reporter.tableCreationStarted("pruning table (corner twist + middle edge position set)");
    twist_mEdgePosSet = new PruneTable(moveTable_twist_mEdgePosSet, turns);
    reporter.tableCreationStarted("pruning table (edge flip + middle edge position set)");
    flip_mEdgePosSet = new PruneTable(moveTable_flip_mEdgePosSet, turns);
  }

  public int get_twist_flip_startDist(final int ct, final int ef) {
    return twist_flip.startDist(moveTable_twist_flip.state(ct, ef));
  }

  public int get_twist_mEdgePosSet_startDist(final int ct, final int meps) {
    return twist_mEdgePosSet.startDist(moveTable_twist_mEdgePosSet.state(ct, meps));
  }

  public int get_flip_mEdgePosSet_startDist(final int ef, final int meps) {
    return flip_mEdgePosSet.startDist(moveTable_flip_mEdgePosSet.state(ef, meps));
  }

  public int get_twist_flip_dist(final int lastDist, final int ct, final int ef) {
    return twist_flip.dist(lastDist, moveTable_twist_flip.state(ct, ef));
  }

  public int get_twist_mEdgePosSet_dist(final int lastDist, final int ct, final int meps) {
    return twist_mEdgePosSet.dist(lastDist, moveTable_twist_mEdgePosSet.state(ct, meps));
  }

  public int get_flip_mEdgePosSet_dist(final int lastDist, final int ef, final int meps) {
    return flip_mEdgePosSet.dist(lastDist, moveTable_flip_mEdgePosSet.state(ef, meps));
  }

  public int maxDist() {
    return max(twist_flip.maxDist(), max(twist_mEdgePosSet.maxDist(), flip_mEdgePosSet.maxDist()));
  }

  public int stateSize() {
    return twist_flip.stateSize() + twist_mEdgePosSet.stateSize() + flip_mEdgePosSet.stateSize();
  }

  public int memorySize() {
    return twist_flip.memorySize() + twist_mEdgePosSet.memorySize() + flip_mEdgePosSet.memorySize();
  }
}
