package acube.data;

import acube.io.StrIn;
import acube.parse.Parser;
import acube.parse.ParserError;

public abstract class Node {

  public abstract String indent();

  public static NodeLink asLink(Node node) {
    return node instanceof NodeLink ? (NodeLink)node : null;
  }

  public static NodeNumber asNumber(Node node) {
    return node instanceof NodeNumber ? (NodeNumber)node : null;
  }

  public static NodeSymbol asSymbol(Node node) {
    return node instanceof NodeSymbol ? (NodeSymbol)node : null;
  }

  public static String asString(Node node) {
    return node == null ? "()" : node.toString();
  }

  public static Node parse(String s) {
    Parser parser = new Parser(new StrIn(s));
    Node node = parser.read();
    if (parser.eoi())
      return node;
    throw new ParserError("More than one expressions in the string");
  }
}
