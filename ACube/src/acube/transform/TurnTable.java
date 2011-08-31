package acube.transform;

import acube.Turn;

public interface TurnTable {
  public int startSize();

  public int start(int i);

  public int stateSize();

  public int memorySize();

  public int turn(Turn turn, int state);
}
