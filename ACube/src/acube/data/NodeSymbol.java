package acube.data;

public final class NodeSymbol extends Node {
  private final String name;

  //static final NodeSymbol DIV = create("DIV");
  private NodeSymbol(final String name) {
    this.name = name.intern();
  }

  public static NodeSymbol create(final String s) {
    return new NodeSymbol(s);
  }

  public String name() {
    return name;
  }

  @Override
  public String indent() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public boolean equals(final Object o) {
    return o instanceof NodeSymbol ? name == ((NodeSymbol)o).name : false;
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }
}
