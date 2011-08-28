package acube.prune;

import acube.Reporter;
import acube.transform.MoveTableComposed;
import acube.transform.Transform;

public class PruneCorners {
  private final PruneTable twist_cornerPos;
  private final MoveTableComposed move_twist_cornerPos;

  public PruneCorners(final Transform transform, final Reporter reporter) {
    move_twist_cornerPos = new MoveTableComposed(transform.twist, transform.cornerPos);
    reporter.tableCreationStarted("pruning table corner orientation + corner position)");
    twist_cornerPos = new PruneTable(move_twist_cornerPos);
  }

  public int distance(final int ct, final int cp) {
    return twist_cornerPos.startDist(move_twist_cornerPos.state(ct, cp));
  }

  public boolean over(final int d, final int ct, final int cp) {
    return twist_cornerPos.dist(d, move_twist_cornerPos.state(ct, cp)) > d;
  }

  public int maxDistance() {
    return twist_cornerPos.maxDist();
  }

  public int stateSize() {
    return twist_cornerPos.stateSize();
  }

  public int memorySize() {
    return twist_cornerPos.memorySize();
  }
}
