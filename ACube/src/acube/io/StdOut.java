package acube.io;

public final class StdOut implements ToWrite {
  @Override
  public void error(final String message) {
    System.err.println(message);
  }

  @Override
  public void say(final String message) {
    System.out.print(message);
  }

  @Override
  public void line(final String message) {
    System.out.println(message);
  }

  @Override
  public void line() {
    System.out.println();
  }
}
