package acube.prune;

import static java.lang.Math.max;
import acube.transform.MoveTableComposed;
import acube.transform.Transform;

public final class Prune {
  private final PruneTable cornerTwistEdgeFlip;
  private final PruneTable cornerTwistMEdgePositionSet;
  private final PruneTable edgeFlipMEdgePositionSet;
  private final MoveTableComposed moveCornerTwistEdgeFlip;
  private final MoveTableComposed moveCornerTwistMEdgePositionSet;
  private final MoveTableComposed moveEdgeFlipMEdgePositionSet;

  public Prune(final Transform transform) {
    moveCornerTwistEdgeFlip = new MoveTableComposed(transform.cornerTwist, transform.edgeFlip);
    moveCornerTwistMEdgePositionSet = new MoveTableComposed(transform.cornerTwist, transform.mEdgePositionSet);
    moveEdgeFlipMEdgePositionSet = new MoveTableComposed(transform.edgeFlip, transform.mEdgePositionSet);
    cornerTwistEdgeFlip = new PruneTable(moveCornerTwistEdgeFlip);
    cornerTwistMEdgePositionSet = new PruneTable(moveCornerTwistMEdgePositionSet);
    edgeFlipMEdgePositionSet = new PruneTable(moveEdgeFlipMEdgePositionSet);
  }

  public int getCornerTwistEdgeFlipStartDistance(final int ct, final int ef) {
    return cornerTwistEdgeFlip.startDistance(moveCornerTwistEdgeFlip.state(ct, ef));
  }

  public int getCornerTwistMEdgePositionSetStartDistance(final int ct, final int meps) {
    return cornerTwistMEdgePositionSet.startDistance(moveCornerTwistMEdgePositionSet.state(ct, meps));
  }

  public int getEdgeFlipMEdgePositionSetStartDistance(final int ef, final int meps) {
    return edgeFlipMEdgePositionSet.startDistance(moveEdgeFlipMEdgePositionSet.state(ef, meps));
  }

  public int getCornerTwistEdgeFlipDistance(final int lastDistance, final int ct, final int ef) {
    return cornerTwistEdgeFlip.distance(lastDistance, moveCornerTwistEdgeFlip.state(ct, ef));
  }

  public int getCornerTwistMEdgePositionSetDistance(final int lastDistance, final int ct, final int meps) {
    return cornerTwistMEdgePositionSet.distance(lastDistance, moveCornerTwistMEdgePositionSet.state(ct, meps));
  }

  public int getEdgeFlipMEdgePositionSetDistance(final int lastDistance, final int ef, final int meps) {
    return edgeFlipMEdgePositionSet.distance(lastDistance, moveEdgeFlipMEdgePositionSet.state(ef, meps));
  }

  public int maxDistance() {
    return max(cornerTwistEdgeFlip.maxDistance(),
        max(cornerTwistMEdgePositionSet.maxDistance(), edgeFlipMEdgePositionSet.maxDistance()));
  }

  public int stateSize() {
    return cornerTwistEdgeFlip.stateSize() + cornerTwistMEdgePositionSet.stateSize() +
        edgeFlipMEdgePositionSet.stateSize();
  }

  public int memorySize() {
    return cornerTwistEdgeFlip.memorySize() + cornerTwistMEdgePositionSet.memorySize() +
        edgeFlipMEdgePositionSet.memorySize();
  }
}
