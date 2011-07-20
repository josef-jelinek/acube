package acube.io;

public final class StrIn implements ToRead {
  private final String text;
  private int pos = 0;
  private int lineNumber = 1;

  public StrIn(final String text) {
    this.text = text;
    pos = 0;
    lineNumber = 1;
  }

  @Override
  public void token() {}

  @Override
  public String error(final String message) {
    return message + " (line " + lineNumber + ")";
  }

  @Override
  public int getChar() {
    return text == null || pos >= text.length() ? -1 : text.charAt(pos);
  }

  @Override
  public void nextChar() {
    if (pos == 0 && getChar() == '\n')
      lineNumber++;
    pos++;
    if (getChar() == '\n')
      lineNumber++;
  }
}
