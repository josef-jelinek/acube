package acube.pack;

public abstract class Coder {

  public static final Coder ordered = new CoderOrdered();

  public static final Coder unordered = new CoderUnordered();

  public abstract int size(int n, int k);

  public abstract int encode(int[] values);

  public abstract void decode(int[] arr, int k, int x);
}
