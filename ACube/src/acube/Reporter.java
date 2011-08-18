package acube;

public interface Reporter {
  public void sequenceFound(String s);

  public void problemOccured(String s);

  public void depthChanged(int depth);

  public void onePhaseStatistics(long pruneChecks, long pruneHits);

  public void twoPhaseStatistics(long pruneChecksA, long pruneHitsA, long pruneChecksB, long pruneHitsB);
}
