package acube.console;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import acube.Reporter;

public final class ConsoleReporter implements Reporter {
  private final List<String> foundSeqs = new ArrayList<String>();

  public ConsoleReporter() {}

  public List<String> getSequences() {
    return Collections.unmodifiableList(foundSeqs);
  }

  public void reset() {
    foundSeqs.clear();
  }

  @Override
  public void solvingStarted(final String s) {
    System.out.printf("|i| solving %s\n", s);
  }

  @Override
  public void tableCreationStarted(final String s) {
    System.out.printf("|i| creating %s\n", s);
  }

  @Override
  public void sequenceFound(final String s, final int ql, final int fl, final int sl, final int sql) {
    foundSeqs.add(s);
    System.out.printf("|S| %s (%dq, %df, %ds, %dsq)\n", s, ql, fl, sl, sql);
  }

  @Override
  public void problemOccured(final String s) {
    System.out.printf("|E| %s", s);
  }

  @Override
  public void depthChanged(final int depth) {
    System.out.printf("|i| depth %d\n", depth);
  }

  @Override
  public void onePhaseStatistics(final long checks, final long hits) {
    final double n = checks > 0 ? hits * 100.0 / checks : 0.0;
    System.out.printf("|i| %.2f%% save, pruned %d of %d entries\n", n, hits, checks);
  }

  @Override
  public void twoPhaseStatistics(final long checksA, final long hitsA, final long checksB, final long hitsB) {
    final double nA = checksA > 0 ? hitsA * 100.0 / checksA : 0.0;
    System.out.printf("|i| %.2f%% save in phase A, pruned %d of %d entries\n", nA, hitsA, checksA);
    final double nB = checksB > 0 ? hitsB * 100.0 / checksB : 0.0;
    System.out.printf("|i| %.2f%% save in phase B, pruned %d of %d entries\n", nB, hitsB, checksB);
  }
}
