package acube.pack;

public abstract class CoderPart {

  public static final CoderPart ordered = new CoderPartOrdered();

  public static final CoderPart unordered = new CoderPartUnordered();

  public abstract int size(int n, int k);

  public abstract int encode(int[] values);

  public abstract void decode(int[] arr, int k, int x);
}
