package acube.format;

import acube.Tools;

public final class State {
  private final int[] position;
  private final int[] orientation;

  State(int[] position, int[] orientation) {
    this.position = position;
    this.orientation = orientation;
  }

  public String toReid() {
    StringBuilder s = new StringBuilder();
    for (int i = 0; i < 20; i++)
      s.append(" " + Tools.rotate(Tools.ReidOrder.get(position[i]), orientation[i]));
    return s.substring(1);
  }
}
