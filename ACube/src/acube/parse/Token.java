package acube.parse;

public final class Token {
  public static final Token SYMBOL = new Token("symbol");
  public static final Token NUMBER = new Token("number");
  public static final Token LPAR = new Token("'('");
  public static final Token RPAR = new Token("')'");
  public static final Token EOI = new Token("end of input");
  public static final Token ERROR = new Token("input error");
  private final String name;

  private Token(final String s) {
    name = s;
  }

  @Override
  public String toString() {
    return name;
  }
}
