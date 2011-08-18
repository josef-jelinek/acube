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
import static acube.Turn.b1;
import static acube.Turn.b2;
import static acube.Turn.b3;
import static acube.Turn.d1;
import static acube.Turn.d2;
import static acube.Turn.d3;
import static acube.Turn.f1;
import static acube.Turn.f2;
import static acube.Turn.f3;
import static acube.Turn.l1;
import static acube.Turn.l2;
import static acube.Turn.l3;
import static acube.Turn.r1;
import static acube.Turn.r2;
import static acube.Turn.r3;
import static acube.Turn.u1;
import static acube.Turn.u2;
import static acube.Turn.u3;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum Metric {
  QUARTER, FACE, SLICE, SLICE_QUARTER, ROTATION_QUARTER, ROTATION_FACE;
  public static final String[] names = { "q", "f", "s", "sq", "rq", "rf" };

  public int length(final Turn turn) {
    return turnLengths[ordinal()][turn.ordinal()];
  }

  public Set<Turn> filterForElementary(final Set<Turn> turns) {
    final Set<Turn> elementary = new HashSet<Turn>();
    for (final Turn turn : turns)
      if (length(turn) == 1)
        elementary.add(turn);
    return elementary;
  }

  private static final int[][] turnLengths = new int[values().length][Turn.size];
  private static final List<Turn> faceTurns = Collections.unmodifiableList(Arrays.asList(new Turn[] {
      U2, D2, F2, B2, L2, R2 }));
  private static final List<Turn> doubleQuarterTurns = Collections.unmodifiableList(Arrays.asList(new Turn[] {
      u1, d1, f1, b1, l1, r1, u3, d3, f3, b3, l3, r3 }));
  private static final List<Turn> doubleFaceTurns = Collections.unmodifiableList(Arrays.asList(new Turn[] {
      u2, d2, f2, b2, l2, r2 }));
  private static final List<Turn> quarterSliceTurns = Collections.unmodifiableList(Arrays.asList(new Turn[] {
      E1, E3, S1, S3, M1, M3 }));
  private static final List<Turn> halfSliceTurns = Collections.unmodifiableList(Arrays
      .asList(new Turn[] { E2, S2, M2 }));
  static {
    for (final int[] turnLengthMetric : turnLengths)
      for (int t = 0; t < turnLengthMetric.length; t++)
        turnLengthMetric[t] = 1;
    setLengths(turnLengths[QUARTER.ordinal()], faceTurns, 2);
    setLengths(turnLengths[SLICE_QUARTER.ordinal()], faceTurns, 2);
    setLengths(turnLengths[ROTATION_QUARTER.ordinal()], faceTurns, 2);
    setLengths(turnLengths[ROTATION_QUARTER.ordinal()], doubleQuarterTurns, 2);
    setLengths(turnLengths[ROTATION_FACE.ordinal()], doubleQuarterTurns, 2);
    setLengths(turnLengths[QUARTER.ordinal()], doubleFaceTurns, 2);
    setLengths(turnLengths[SLICE_QUARTER.ordinal()], doubleFaceTurns, 2);
    setLengths(turnLengths[ROTATION_QUARTER.ordinal()], doubleFaceTurns, 4);
    setLengths(turnLengths[ROTATION_FACE.ordinal()], doubleFaceTurns, 2);
    setLengths(turnLengths[QUARTER.ordinal()], quarterSliceTurns, 2);
    setLengths(turnLengths[FACE.ordinal()], quarterSliceTurns, 2);
    setLengths(turnLengths[ROTATION_QUARTER.ordinal()], quarterSliceTurns, 3);
    setLengths(turnLengths[ROTATION_FACE.ordinal()], quarterSliceTurns, 3);
    setLengths(turnLengths[QUARTER.ordinal()], halfSliceTurns, 4);
    setLengths(turnLengths[FACE.ordinal()], halfSliceTurns, 2);
    setLengths(turnLengths[SLICE_QUARTER.ordinal()], halfSliceTurns, 2);
    setLengths(turnLengths[ROTATION_QUARTER.ordinal()], halfSliceTurns, 6);
    setLengths(turnLengths[ROTATION_FACE.ordinal()], halfSliceTurns, 3);
  }

  private static void setLengths(final int[] turnLengths, final List<Turn> turns, final int length) {
    for (final Turn turn : turns)
      turnLengths[turn.ordinal()] = length;
  }
}
