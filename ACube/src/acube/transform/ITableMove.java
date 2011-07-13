package acube.transform;

import acube.Turn;

public interface ITableMove extends IDoMove {

  public int startSize();
  public int start(int i);
  public int stateSize();
  public Turn[] turns();
}
