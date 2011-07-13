package acube.io;

import java.io.IOException;

public final class FileIn implements ToRead {
  private int lineNumber = 1;
  private int inputChar;
  private boolean inputCharValid = false;

  public FileIn(final String filename) {}

  @Override
  public void token() {}

  @Override
  public String error(final String message) {
    return message + " (line " + lineNumber + ")";
  }

  @Override
  public int getChar() {
    if (!inputCharValid) {
      try {
        inputChar = System.in.read();
        if (inputChar == '\n')
          lineNumber++;
      } catch (final IOException e) {
        inputChar = -1;
      }
      inputCharValid = true;
    }
    return inputChar;
  }

  @Override
  public void nextChar() {
    if (!inputCharValid)
      try {
        inputChar = System.in.read();
        if (inputChar == '\n')
          lineNumber++;
      } catch (final IOException e) {
        inputChar = -1;
      }
    inputCharValid = false;
  }
}
