package acube.transform;

import acube.Turn;

public abstract class CubeSpaceState {
    protected final int cubeSym;

  protected CubeSpaceState(final int cubeSym) {
    this.cubeSym = cubeSym;
  }

  public abstract CubeSpaceState turn(Turn t);
}
