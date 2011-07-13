package acube.parse;

public final class ParserError extends RuntimeException {
  private static final long serialVersionUID = 0; // just to disable warning
  private final String message;

  public ParserError(final String message) {
    super(message);
    this.message = message;
  }

  @Override
  public String toString() {
    return "Syntax error: " + message + ".";
  }
}
