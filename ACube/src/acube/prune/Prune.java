package acube.prune;

import static java.lang.Math.max;
import acube.transform.MoveTab2;
import acube.transform.Transform;

public final class Prune {
  private final PruneTab cornTwistEdgeFlip;
  private final PruneTab cornTwistMidgeLoc;
  private final PruneTab edgeFlipMidgeLoc;
  private final MoveTab2 moveCornTwistEdgeFlip;
  private final MoveTab2 moveCornTwistMidgeLoc;
  private final MoveTab2 moveEdgeFlipMidgeLoc;

  public Prune(Transform transform) {
    moveCornTwistEdgeFlip = new MoveTab2(transform.cornTwist, transform.edgeFlip);
    moveCornTwistMidgeLoc = new MoveTab2(transform.cornTwist, transform.midgeLoc);
    moveEdgeFlipMidgeLoc = new MoveTab2(transform.edgeFlip, transform.midgeLoc);
    cornTwistEdgeFlip = new PruneTab(moveCornTwistEdgeFlip);
    cornTwistMidgeLoc = new PruneTab(moveCornTwistMidgeLoc);
    edgeFlipMidgeLoc = new PruneTab(moveEdgeFlipMidgeLoc);
  }

  private static int max3(int a, int b, int c) {
    return max(max(a, b), c);
  }

  public int distance(int ct, int ef, int ml) {
    return max3(cornTwistEdgeFlip.startDistance(moveCornTwistEdgeFlip.state(ct, ef)),
               cornTwistMidgeLoc.startDistance(moveCornTwistMidgeLoc.state(ct, ml)),
               edgeFlipMidgeLoc.startDistance(moveEdgeFlipMidgeLoc.state(ef, ml)));
  }

  public boolean over(int d, int ct, int ef, int ml) {
    return cornTwistEdgeFlip.distance(d, moveCornTwistEdgeFlip.state(ct, ef)) > d
        || cornTwistMidgeLoc.distance(d, moveCornTwistMidgeLoc.state(ct, ml)) > d
        || edgeFlipMidgeLoc.distance(d, moveEdgeFlipMidgeLoc.state(ef, ml)) > d;
  }

  public int maxDistance() {
    return max3(cornTwistEdgeFlip.maxDistance(), cornTwistMidgeLoc.maxDistance(), edgeFlipMidgeLoc.maxDistance());
  }

  public int stateN() {
    return cornTwistEdgeFlip.stateN() + cornTwistMidgeLoc.stateN() + edgeFlipMidgeLoc.stateN();
  }
}
