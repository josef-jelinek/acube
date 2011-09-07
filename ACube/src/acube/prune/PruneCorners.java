package acube.prune;

import java.util.EnumSet;
import acube.Reporter;
import acube.Turn;
import acube.transform.MoveTableComposed;
import acube.transform.Transform;

public class PruneCorners {
  private final PruneTable twist_cornerPos;
  private final MoveTableComposed moveTable_twist_cornerPos;

  public PruneCorners(final Transform transform, final Reporter reporter) {
    final EnumSet<Turn> turns = Turn.essentialValueSet;
    moveTable_twist_cornerPos = new MoveTableComposed(transform.twistTable, transform.cornerPosTable);
    reporter.tableCreationStarted("pruning table corner orientation + corner position)");
    twist_cornerPos = new PruneTable(moveTable_twist_cornerPos, turns);
  }

  public int get_twist_cornerPos_startDist(final int ct, final int cp) {
    return twist_cornerPos.startDist(moveTable_twist_cornerPos.state(ct, cp));
  }

  public int get_twist_cornerPos_dist(final int lastDist, final int ct, final int cp) {
    return twist_cornerPos.dist(lastDist, moveTable_twist_cornerPos.state(ct, cp));
  }

  public int maxDist() {
    return twist_cornerPos.maxDist();
  }

  public int stateSize() {
    return twist_cornerPos.stateSize();
  }

  public int memorySize() {
    return twist_cornerPos.memorySize();
  }
}
