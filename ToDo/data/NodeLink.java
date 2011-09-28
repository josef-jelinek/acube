package acube.data;

public final class NodeLink extends Node {
  private final Node head;
  private final NodeLink tail;

  private NodeLink(final Node head, final NodeLink tail) {
    this.head = head;
    this.tail = tail;
  }

  public static NodeLink link(final Node head, final NodeLink tail) {
    return new NodeLink(head, tail);
  }

  public static NodeLink list(final Node node) {
    return link(node, null);
  }

  public static NodeLink list(final Node node1, final Node node2) {
    return link(node1, list(node2));
  }

  public static NodeLink list(final Node[] nodes) {
    NodeLink list = null;
    for (int i = nodes.length - 1; i >= 0; i--)
      list = link(nodes[i], list);
    return list;
  }

  public static NodeLink add(final NodeLink list, final Node node) {
    return append(list, list(node));
  }

  public static NodeLink reverse(final NodeLink list) {
    return reverse(list, null);
  }

  private static NodeLink reverse(final NodeLink list, final NodeLink partialResultList) {
    NodeLink resultList = partialResultList;
    for (NodeLink link = list; link != null; link = link.tail())
      resultList = link(link.head(), resultList);
    return resultList;
  }

  public static NodeLink append(final NodeLink list, final NodeLink tail) {
    return reverse(reverse(list), tail);
  }

  public Node head() {
    return head;
  }

  public NodeLink tail() {
    return tail;
  }

  public int length() {
    NodeLink node = tail;
    int len = 1;
    while (node != null) {
      node = node.tail;
      len++;
    }
    return len;
  }

  public Node last() {
    NodeLink node = this;
    while (node.tail != null)
      node = node.tail;
    return node.head;
  }

  public Node nth(final int n) {
    NodeLink node = this;
    for (int i = n; i > 0; i--)
      if ((node = node.tail) == null)
        return null;
    return node.head;
  }

  @Override
  public String indent() {
    return pretty(this, "");
  }

  private static String pretty(final NodeLink list, final String prefix) {
    String s = "(";
    int mode = 0; // 0: new line, 1-3: simple list, 4: complex list
    for (NodeLink node = list; node != null; node = node.tail) {
      if (node.head instanceof NodeLink) {
        final NodeLink head = (NodeLink)node.head;
        if (constLength(head) < 0) {
          if (mode > 0)
            s += "\r\n" + prefix + "  ";
          s += pretty(head, prefix + "  ");
          mode = 10;
          continue;
        }
      }
      if (mode >= 4) {
        s += "\r\n" + prefix + "  ";
        mode = 0;
      } else if (mode > 0)
        s += " ";
      s += node.head == null ? "()" : node.head.toString();
      mode++;
    }
    return s + ")";
  }

  private static int constLength(final NodeLink list) {
    int len = 0;
    for (NodeLink node = list; node != null; node = node.tail) {
      if (node.head instanceof NodeLink)
        return -1;
      len++;
    }
    return len;
  }

  @Override
  public String toString() {
    String s = head == null ? "(()" : "(" + head;
    for (NodeLink node = tail; node != null; node = node.tail)
      s += node.head == null ? " ()" : " " + node.head;
    return s + ")";
  }
}
