package acube.prune;

import acube.transform.TransformB;
import acube.transform.MoveTab2;

public final class PruneB {
  private final PruneTab midgePosCornPos;
  private final PruneTab midgePosEdgePos;
  private final MoveTab2 moveMidgePosCornPos;
  private final MoveTab2 moveMidgePosEdgePos;

  public PruneB(TransformB transform) {
    moveMidgePosCornPos = new MoveTab2(transform.midgePos, transform.cornPos);
    moveMidgePosEdgePos = new MoveTab2(transform.midgePos, transform.edgePos);
    midgePosCornPos = new PruneTab(moveMidgePosCornPos);
    midgePosEdgePos = new PruneTab(moveMidgePosEdgePos);
  }

  private static int max(int a, int b) {
    return Math.max(a, b);
  }

  public int distance(int cp, int ep, int mp) {
    return max(midgePosCornPos.startDistance(moveMidgePosCornPos.state(mp, cp)), midgePosEdgePos.startDistance(moveMidgePosEdgePos.state(mp, ep)));
  }

  public boolean over(int d, int cp, int ep, int mp) {
    return midgePosCornPos.distance(d, moveMidgePosCornPos.state(mp, cp)) > d || midgePosEdgePos.distance(d, moveMidgePosEdgePos.state(mp, ep)) > d;
  }

  public int maxDistance() {
    return max(midgePosCornPos.maxDistance(), midgePosEdgePos.maxDistance());
  }

  public int stateN() {
    return midgePosCornPos.stateN() + midgePosEdgePos.stateN();
  }
}
