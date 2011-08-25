package acube.prune;

import acube.transform.MoveTableComposed;
import acube.transform.Transform;

public class PruneCorners {
  private final PruneTable twist_cornerPos;
  private final MoveTableComposed move_twist_cornerPos;

  public PruneCorners(final Transform transform) {
    move_twist_cornerPos = new MoveTableComposed(transform.twist, transform.cornerPos);
    twist_cornerPos = new PruneTable(move_twist_cornerPos);
  }

  public int distance(final int ct, final int cp) {
    return twist_cornerPos.startDistance(move_twist_cornerPos.state(ct, cp));
  }

  public boolean over(final int d, final int ct, final int cp) {
    return twist_cornerPos.distance(d, move_twist_cornerPos.state(ct, cp)) > d;
  }

  public int maxDistance() {
    return twist_cornerPos.maxDistance();
  }

  public int stateSize() {
    return twist_cornerPos.stateSize();
  }

  public int memorySize() {
    return twist_cornerPos.memorySize();
  }
}
