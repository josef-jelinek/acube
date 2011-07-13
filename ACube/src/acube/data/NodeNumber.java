package acube.data;

public final class NodeNumber extends Node {
  private final int n;

  private NodeNumber(final int i) {
    n = i;
  }

  public static NodeNumber create(final int i) {
    return new NodeNumber(i);
  }

  public int value() {
    return n;
  }

  @Override
  public String indent() {
    return "" + n;
  }

  @Override
  public String toString() {
    return "" + n;
  }

  @Override
  public boolean equals(final Object o) {
    return o instanceof NodeNumber ? n == ((NodeNumber)o).n : false;
  }

  @Override
  public int hashCode() {
    return n;
  }
}
