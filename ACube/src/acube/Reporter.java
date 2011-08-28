package acube;

public interface Reporter {
  public void solvingStarted(String s);

  public void tableCreationStarted(String s);

  public void sequenceFound(String s, int ql, int fl, int sl, int sql);

  public void problemOccured(String s);

  public void depthChanged(int depth);

  public void onePhaseStatistics(long pruneChecks, long pruneHits);

  public void twoPhaseStatistics(long pruneChecksA, long pruneHitsA, long pruneChecksB, long pruneHitsB);
}
