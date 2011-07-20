package acube.prune;

import acube.transform.MoveTableComposed;
import acube.transform.TransformB;

public final class PruneB {
  private final PruneTable mEdgePositionCornerPosition;
  private final PruneTable mEdgePositionOEdgePosition;
  private final MoveTableComposed moveMEdgePositionCornerPosition;
  private final MoveTableComposed moveMEdgePositionOEdgePosition;

  public PruneB(final TransformB transform) {
    moveMEdgePositionCornerPosition = new MoveTableComposed(transform.mEdgePosition, transform.cornerPosition);
    moveMEdgePositionOEdgePosition = new MoveTableComposed(transform.mEdgePosition, transform.oEdgePosition);
    mEdgePositionCornerPosition = new PruneTable(moveMEdgePositionCornerPosition);
    mEdgePositionOEdgePosition = new PruneTable(moveMEdgePositionOEdgePosition);
  }

  private static int max(final int a, final int b) {
    return Math.max(a, b);
  }

  public int distance(final int cp, final int ep, final int mp) {
    return max(mEdgePositionCornerPosition.startDistance(moveMEdgePositionCornerPosition.state(mp, cp)),
        mEdgePositionOEdgePosition.startDistance(moveMEdgePositionOEdgePosition.state(mp, ep)));
  }

  public boolean over(final int d, final int cp, final int ep, final int mp) {
    return mEdgePositionCornerPosition.distance(d, moveMEdgePositionCornerPosition.state(mp, cp)) > d ||
        mEdgePositionOEdgePosition.distance(d, moveMEdgePositionOEdgePosition.state(mp, ep)) > d;
  }

  public int maxDistance() {
    return max(mEdgePositionCornerPosition.maxDistance(), mEdgePositionOEdgePosition.maxDistance());
  }

  public int stateSize() {
    return mEdgePositionCornerPosition.stateSize() + mEdgePositionOEdgePosition.stateSize();
  }

  public int memorySize() {
    return mEdgePositionCornerPosition.memorySize() + mEdgePositionOEdgePosition.memorySize();
  }
}
