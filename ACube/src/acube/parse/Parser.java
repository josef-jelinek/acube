package acube.parse;

import acube.data.Node;
import acube.data.NodeLink;
import acube.data.NodeNumber;
import acube.data.NodeSymbol;
import acube.io.ToRead;

public final class Parser {
  private final Scanner scanner;

  public Parser(ToRead input) {
    scanner = new Scanner(input);
  }

  public Node read() {
    return startRule();
  }

  public boolean eoi() {
    if (scanner.getToken() == Token.ERROR)
      throw new ParserError(scanner.error());
    return scanner.getToken() == Token.EOI;
  }

  public void recover() {
    scanner.nextToken();
  }

  private Node startRule() {
    if (scanner.getToken() == Token.ERROR)
      throw new ParserError(scanner.error());
    if (scanner.getToken() == Token.NUMBER) {
      Node node = NodeNumber.create(scanner.number());
      scanner.nextToken();
      return node;
    }
    if (scanner.getToken() == Token.SYMBOL) {
      Node node = NodeSymbol.create(scanner.symbol());
      scanner.nextToken();
      return node;
    }
    if (scanner.getToken() == Token.LPAR) {
      scanner.nextToken();
      NodeLink list = listRule();
      match(Token.RPAR, "Expected ')' instead of " + scanner.getToken());
      return list;
    }
    throw new ParserError(scanner.error("Unexpected " + scanner.getToken()));
  }

  private NodeLink listRule() {
    if (scanner.getToken() == Token.ERROR)
      throw new ParserError(scanner.error());
    return scanner.getToken() == Token.RPAR ? null : NodeLink.link(startRule(), listRule());
  }

  private void match(Token token, String message) {
    if (scanner.getToken() == Token.ERROR)
      throw new ParserError(scanner.error());
    if (token != scanner.getToken())
      throw new ParserError(scanner.error(message));
    scanner.nextToken();
  }
}
