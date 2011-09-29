package acube;

import java.util.List;

public final class NullReporter implements Reporter {
  public NullReporter() {}

  public List<String> getSequences() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void solvingStarted(final String s) {}

  @Override
  public void tableCreationStarted(final String s) {}

  @Override
  public void sequenceFound(final String s, final int ql, final int fl, final int sl, final int sql) {}

  @Override
  public void problemOccured(final String s) {}

  @Override
  public void depthChanged(final int depth) {}

  @Override
  public void onePhaseStatistics(final long checks, final long hits) {}

  @Override
  public void twoPhaseStatistics(final long checksA, final long hitsA, final long checksB, final long hitsB) {}

  @Override
  public boolean shouldStop() {
    return false;
  }
}
