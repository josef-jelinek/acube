package acube.prune;

import acube.transform.Transform;
import acube.transform.MoveTableComposed;

public final class Prune {

  private final PruneTable cornerTwistEdgeFlip;
  private final PruneTable cornerTwistMEdgePositionSet;
  private final PruneTable edgeFlipMEdgePositionSet;
  private final MoveTableComposed moveCornerTwistEdgeFlip;
  private final MoveTableComposed moveCornerTwistMEdgePositionSet;
  private final MoveTableComposed moveEdgeFlipMEdgePositionSet;

  public Prune(Transform transform) {
    moveCornerTwistEdgeFlip = new MoveTableComposed(transform.cornerTwist, transform.edgeFlip);
    moveCornerTwistMEdgePositionSet = new MoveTableComposed(transform.cornerTwist, transform.mEdgePositionSet);
    moveEdgeFlipMEdgePositionSet = new MoveTableComposed(transform.edgeFlip, transform.mEdgePositionSet);
    cornerTwistEdgeFlip = new PruneTable(moveCornerTwistEdgeFlip, transform.turns());
    cornerTwistMEdgePositionSet = new PruneTable(moveCornerTwistMEdgePositionSet, transform.turns());
    edgeFlipMEdgePositionSet = new PruneTable(moveEdgeFlipMEdgePositionSet, transform.turns());
  }

  private static int max(int a, int b, int c) {
    return Math.max(Math.max(a, b), c);
  }

  public int distance(int ct, int ef, int ml) {
    return max(cornerTwistEdgeFlip.startDistance(moveCornerTwistEdgeFlip.state(ct, ef)),
               cornerTwistMEdgePositionSet.startDistance(moveCornerTwistMEdgePositionSet.state(ct, ml)),
               edgeFlipMEdgePositionSet.startDistance(moveEdgeFlipMEdgePositionSet.state(ef, ml)));
  }

  public boolean over(int d, int ct, int ef, int ml) {
    return cornerTwistEdgeFlip.distance(d, moveCornerTwistEdgeFlip.state(ct, ef)) > d
        || cornerTwistMEdgePositionSet.distance(d, moveCornerTwistMEdgePositionSet.state(ct, ml)) > d
        || edgeFlipMEdgePositionSet.distance(d, moveEdgeFlipMEdgePositionSet.state(ef, ml)) > d;
  }

  public int maxDistance() {
    return max(cornerTwistEdgeFlip.maxDistance(), cornerTwistMEdgePositionSet.maxDistance(), edgeFlipMEdgePositionSet.maxDistance());
  }

  public int size() {
    return cornerTwistEdgeFlip.stateSize() + cornerTwistMEdgePositionSet.stateSize() + edgeFlipMEdgePositionSet.stateSize();
  }
}