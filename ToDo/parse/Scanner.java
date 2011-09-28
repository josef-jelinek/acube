package acube.parse;

import acube.io.ToRead;

public final class Scanner {

  private final ToRead input;
  private Token token = null;

  private int intValue;
  private String stringValue;
  private String errorText;

  public Scanner(ToRead input) {
    this.input = input;
  }

  public String error() {
    if (token == Token.ERROR)
      return input.error(errorText);
    throw new IllegalStateException("Reading already invalidated error");
  }

  public String error(String message) {
    return input.error(message);
  }

  public int number() {
    if (token == Token.NUMBER)
      return intValue;
    throw new IllegalStateException("Reading already invalidated number");
  }

  public String symbol() {
    if (token == Token.SYMBOL)
      return stringValue;
    throw new IllegalStateException("Reading already invalidated symbol");
  }

  public Token getToken() {
    if (token == null)
      token = readToken();
    return token;
  }

  public void nextToken() {
    if (token == null)
      readToken();
    token = null;
  }

  private Token readToken() {
    skipWS();
    input.token();
    if (input.getChar() == -1)
      return Token.EOI;
    if (input.getChar() == '(') {
      input.nextChar();
      return Token.LPAR;
    }
    if (input.getChar() == ')') {
      input.nextChar();
      return Token.RPAR;
    }
    if (input.getChar() >= '0' && input.getChar() <= '9') {
      intValue = getNumber();
      if (intValue >= 0)
        return Token.NUMBER;
      errorText = "Number too big";
      return Token.ERROR;
    }
    if (input.getChar() == '-') {
      input.nextChar();
      if (input.getChar() >= '0' && input.getChar() <= '9') {
        intValue = -getNumber();
        if (intValue <= 0)
          return Token.NUMBER;
        errorText = "Number too small";
        return Token.ERROR;
      }
      stringValue = "-" + getSymbol();
      return Token.SYMBOL;
    }
    if (input.getChar() > 32) {
      stringValue = getSymbol();
      return Token.SYMBOL;
    }
    errorText = "Input error";
    return Token.ERROR;
  }

  private int getNumber() {
    int i = 0;
    while (input.getChar() >= '0' && input.getChar() <= '9') {
      int j = input.getChar() - '0';
      i = i >= 0 && (i < 0xCCCCCCC || i == 0xCCCCCCC && j <= 8) ? i * 10 + j : -1;
      input.nextChar();
    }
    return i;
  }

  private String getSymbol() {
    StringBuilder s = new StringBuilder();
    while (input.getChar() > 32 && "();".indexOf(input.getChar()) < 0) {
      s.append((char)input.getChar());
      input.nextChar();
    }
    return s.toString();
  }

  private void skipWS() {
    for (;;) {
      while (input.getChar() >= 0 && input.getChar() <= 32)
        input.nextChar();
      if (input.getChar() != ';')
        return;
      while (input.getChar() != -1 && input.getChar() != 10)
        input.nextChar();
    }
  }
}
