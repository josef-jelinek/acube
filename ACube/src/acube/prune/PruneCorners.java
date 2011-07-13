package acube.prune;

import acube.transform.MoveTableComposed;
import acube.transform.Transform;

public class PruneCorners {

  private final PruneTable cornerTwistCornerPosition;
  private final MoveTableComposed moveCornerTwistCornerPosition;

  public PruneCorners(Transform transform) {
    moveCornerTwistCornerPosition = new MoveTableComposed(transform.cornerTwist, transform.cornerPosition);
    cornerTwistCornerPosition = new PruneTable(moveCornerTwistCornerPosition, transform.turns());
  }

  public int distance(int ct, int cp) {
    return cornerTwistCornerPosition.startDistance(moveCornerTwistCornerPosition.state(ct, cp));
  }

  public boolean over(int d, int ct, int cp) {
    return cornerTwistCornerPosition.distance(d, moveCornerTwistCornerPosition.state(ct, cp)) > d;
  }

  public int maxDistance() {
    return cornerTwistCornerPosition.maxDistance();
  }

  public int size() {
    return cornerTwistCornerPosition.stateSize();
  }
}
