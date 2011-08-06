package acube.prune;

import static java.lang.Math.max;
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

  public int getMEdgePositionCornerCornerPositionStartDistance(final int mep, final int cp) {
    return mEdgePositionCornerPosition.startDistance(moveMEdgePositionCornerPosition.state(mep, cp));
  }

  public int getMEdgePositionOEdgePositionStartDistance(final int mep, final int oep) {
    return mEdgePositionOEdgePosition.startDistance(moveMEdgePositionOEdgePosition.state(mep, oep));
  }

  public int getMEdgePositionCornerCornerPositionDistance(final int lastDistance, final int mep, final int cp) {
    return mEdgePositionCornerPosition.distance(lastDistance, moveMEdgePositionCornerPosition.state(mep, cp));
  }

  public int getMEdgePositionOEdgePositionDistance(final int lastDistance, final int mep, final int oep) {
    return mEdgePositionOEdgePosition.distance(lastDistance, moveMEdgePositionOEdgePosition.state(mep, oep));
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
