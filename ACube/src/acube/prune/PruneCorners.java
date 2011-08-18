package acube.prune;

import acube.transform.MoveTableComposed;
import acube.transform.Transform;

public class PruneCorners {
  private final PruneTable cornerTwistCornerPosition;
  private final MoveTableComposed moveCornerTwistCornerPosition;

  public PruneCorners(final Transform transform) {
    moveCornerTwistCornerPosition = new MoveTableComposed(transform.cornerTwist, transform.cornerPos);
    cornerTwistCornerPosition = new PruneTable(moveCornerTwistCornerPosition);
  }

  public int distance(final int ct, final int cp) {
    return cornerTwistCornerPosition.startDistance(moveCornerTwistCornerPosition.state(ct, cp));
  }

  public boolean over(final int d, final int ct, final int cp) {
    return cornerTwistCornerPosition.distance(d, moveCornerTwistCornerPosition.state(ct, cp)) > d;
  }

  public int maxDistance() {
    return cornerTwistCornerPosition.maxDistance();
  }

  public int stateSize() {
    return cornerTwistCornerPosition.stateSize();
  }

  public int memorySize() {
    return cornerTwistCornerPosition.memorySize();
  }
}
