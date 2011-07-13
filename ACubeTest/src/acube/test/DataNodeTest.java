package acube.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import org.junit.Test;
import acube.data.Node;
import acube.data.NodeLink;
import acube.data.NodeNumber;
import acube.data.NodeSymbol;

public final class DataNodeTest {

  @Test
  public void test_link() {
    NodeLink node = NodeLink.link(null, null);
    assertEquals(null, node.head());
    assertEquals(null, node.tail());
    assertEquals(1, node.length());
    assertEquals(null, node.last());
    assertEquals("(())", node.toString());
    NodeLink list1 = NodeLink.list(node);
    assertEquals(node, list1.head());
    assertEquals(null, list1.tail());
    assertEquals(1, list1.length());
    assertEquals(node, list1.last());
    assertEquals("((()))", list1.toString());
    NodeLink list2 = NodeLink.list(node, list1);
    assertEquals(node, list2.head());
    assertEquals(list1, list2.tail().head());
    assertEquals(null, list2.tail().tail());
    assertEquals(2, list2.length());
    assertEquals(list1, list2.last());
    assertEquals("((()) ((())))", list2.toString());
    NodeLink list3 = NodeLink.add(list2, list2);
    assertEquals("((()) ((())) ((()) ((()))))", list3.toString());
    assertEquals("(((()) ((()))) ((())) (()))", NodeLink.reverse(list3).toString());
    assertEquals(node, list3.nth(0));
    assertEquals(list1, list3.nth(1));
    assertEquals(list2, list3.nth(2));
    assertEquals(null, list3.nth(3));
    assertEquals("((())\r\n  ((()))\r\n  ((())\r\n    ((()))))", list3.indent());
    assertEquals("(((())\r\n    ((()))\r\n    ((())\r\n      ((()))))\r\n  ((())\r\n    ((()))\r\n    ((())\r\n      ((())))))", NodeLink.list(list3, list3).indent());
    assertEquals("(() () () ()\r\n  ())", NodeLink.link(null, NodeLink.append(NodeLink.list(null, null), NodeLink.list(null, null))).indent());
    assertEquals(null, NodeLink.list(new Node[0]));
    assertEquals("(() (()))", NodeLink.list(new Node[] { null, node }).toString());
  }

  @Test
  public void test_symbol() {
    NodeSymbol node1 = NodeSymbol.create("test");
    NodeSymbol node2 = NodeSymbol.create("test");
    NodeSymbol node3 = NodeSymbol.create("TEST");
    assertEquals(node1, node2);
    assertEquals(node1.hashCode(), node2.hashCode());
    assertSame(node1.name(), node2.name());
    assertSame("test", node1.name());
    assertFalse(node1.equals(null));
    assertFalse(node1.equals(node3));
    assertNotSame(node1.name(), node3.name());
    assertEquals("test", node1.indent());
    assertEquals("test", node1.toString());
    assertEquals("TEST", node3.indent());
    assertEquals("TEST", node3.toString());
  }

  @Test
  public void test_number() {
    NodeNumber node = NodeNumber.create(0);
    assertEquals(0, node.value());
    assertEquals(NodeNumber.create(0), node);
    assertEquals(NodeNumber.create(0).hashCode(), node.hashCode());
    assertEquals("0", node.indent());
    assertEquals("0", node.toString());
    assertFalse(NodeNumber.create(1).equals(node));
  }

  @Test
  public void test_parse() {
    assertEquals(null, Node.parse("()"));
    assertEquals("s's", Node.asSymbol(Node.parse("s's")).name());
  }
}
