package acube.prune;

import acube.transform.TransformB;
import acube.transform.MoveTableComposed;

public final class PruneB {

  private final PruneTable mEdgePositionCornerPosition;
  private final PruneTable mEdgePositionOEdgePosition;
  private final MoveTableComposed moveMEdgePositionCornerPosition;
  private final MoveTableComposed moveMEdgePositionOEdgePosition;

  public PruneB(TransformB transform) {
    moveMEdgePositionCornerPosition = new MoveTableComposed(transform.mEdgePosition, transform.cornerPosition);
    moveMEdgePositionOEdgePosition = new MoveTableComposed(transform.mEdgePosition, transform.oEdgePosition);
    mEdgePositionCornerPosition = new PruneTable(moveMEdgePositionCornerPosition, transform.turns());
    mEdgePositionOEdgePosition = new PruneTable(moveMEdgePositionOEdgePosition, transform.turns());
  }

  private static int max(int a, int b) {
    return Math.max(a, b);
  }

  public int distance(int cp, int ep, int mp) {
    return max(mEdgePositionCornerPosition.startDistance(moveMEdgePositionCornerPosition.state(mp, cp)),
      mEdgePositionOEdgePosition.startDistance(moveMEdgePositionOEdgePosition.state(mp, ep)));
  }

  public boolean over(int d, int cp, int ep, int mp) {
    return mEdgePositionCornerPosition.distance(d, moveMEdgePositionCornerPosition.state(mp, cp)) > d
      || mEdgePositionOEdgePosition.distance(d, moveMEdgePositionOEdgePosition.state(mp, ep)) > d;
  }

  public int maxDistance() {
    return max(mEdgePositionCornerPosition.maxDistance(), mEdgePositionOEdgePosition.maxDistance());
  }

  public int stateSize() {
    return mEdgePositionCornerPosition.stateSize() + mEdgePositionOEdgePosition.stateSize();
  }
}
