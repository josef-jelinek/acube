package acube;

import static acube.Turn.B2;
import static acube.Turn.D2;
import static acube.Turn.E1;
import static acube.Turn.E2;
import static acube.Turn.E3;
import static acube.Turn.F2;
import static acube.Turn.L2;
import static acube.Turn.M1;
import static acube.Turn.M2;
import static acube.Turn.M3;
import static acube.Turn.R2;
import static acube.Turn.S1;
import static acube.Turn.S2;
import static acube.Turn.S3;
import static acube.Turn.U2;
import static acube.Turn.b2;
import static acube.Turn.d2;
import static acube.Turn.e2;
import static acube.Turn.f2;
import static acube.Turn.l2;
import static acube.Turn.m2;
import static acube.Turn.r2;
import static acube.Turn.s2;
import static acube.Turn.u2;
import java.util.EnumSet;

public enum Metric {
  QUARTER, FACE, SLICE, SLICE_QUARTER;
  private static final int[][] turnLengths = new int[values().length][Turn.size];
  private static final String[] names = { "qtm", "ftm", "stm", "sqtm" };
  public static final String nameString = "qtm, ftm, stm, sqtm";
  private static final EnumSet<Turn> halfFaceTurns = EnumSet.of(U2, D2, F2, B2, L2, R2);
  private static final EnumSet<Turn> halfWideTurns = EnumSet.of(u2, d2, f2, b2, l2, r2);
  private static final EnumSet<Turn> quarterSliceTurns = EnumSet.of(E1, E3, S1, S3, M1, M3);
  private static final EnumSet<Turn> halfSliceTurns = EnumSet.of(E2, S2, M2);
  private static final EnumSet<Turn> halfCubeTurns = EnumSet.of(e2, s2, m2);
  static {
    for (final int[] turnLengthMetric : turnLengths)
      for (int t = 0; t < turnLengthMetric.length; t++)
        turnLengthMetric[t] = 1;
    setLengths(turnLengths[QUARTER.ordinal()], halfFaceTurns, 2);
    setLengths(turnLengths[SLICE_QUARTER.ordinal()], halfFaceTurns, 2);
    setLengths(turnLengths[QUARTER.ordinal()], halfWideTurns, 2);
    setLengths(turnLengths[SLICE_QUARTER.ordinal()], halfWideTurns, 2);
    setLengths(turnLengths[QUARTER.ordinal()], quarterSliceTurns, 2);
    setLengths(turnLengths[FACE.ordinal()], quarterSliceTurns, 2);
    setLengths(turnLengths[QUARTER.ordinal()], halfSliceTurns, 4);
    setLengths(turnLengths[FACE.ordinal()], halfSliceTurns, 2);
    setLengths(turnLengths[SLICE_QUARTER.ordinal()], halfSliceTurns, 2);
    setLengths(turnLengths[QUARTER.ordinal()], halfCubeTurns, 2);
    setLengths(turnLengths[SLICE_QUARTER.ordinal()], halfCubeTurns, 2);
  }

  private static void setLengths(final int[] turnLengths, final EnumSet<Turn> turns, final int length) {
    for (final Turn turn : turns)
      turnLengths[turn.ordinal()] = length;
  }

  public int length(final Turn turn) {
    return turnLengths[ordinal()][turn.ordinal()];
  }

  @Override
  public String toString() {
    return names[ordinal()];
  }
}
