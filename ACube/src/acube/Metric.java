package acube;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum Metric {
  QUARTER, FACE, SLICE, SLICE_QUARTER, ROTATION_QUARTER, ROTATION_FACE;
  public static final String[] names = { "q", "f", "s", "sq", "rq", "rf" };

  public int length(final Turn turn) {
    return turnLengths[ordinal()][turn.ordinal()];
  }

  public Turn[] filterForElementary(final Turn[] turns) {
    final List<Turn> list = new ArrayList<Turn>();
    for (final Turn turn : turns)
      if (length(turn) == 1)
        list.add(turn);
    return list.toArray(new Turn[list.size()]);
  }

  private static final int[][] turnLengths = new int[values().length][Turn.size()];
  private static final List<Turn> faceTurns = Collections.unmodifiableList(Arrays.asList(new Turn[] {
      Turn.U2, Turn.D2, Turn.F2, Turn.B2, Turn.L2, Turn.R2, }));
  private static final List<Turn> doubleQuarterTurns = Collections.unmodifiableList(Arrays.asList(new Turn[] {
      Turn.u1, Turn.d1, Turn.f1, Turn.b1, Turn.l1, Turn.r1, Turn.u3, Turn.d3, Turn.f3, Turn.b3, Turn.l3, Turn.r3, }));
  private static final List<Turn> doubleFaceTurns = Collections.unmodifiableList(Arrays.asList(new Turn[] {
      Turn.u2, Turn.d2, Turn.f2, Turn.b2, Turn.l2, Turn.r2, }));
  private static final List<Turn> quarterSliceTurns = Collections.unmodifiableList(Arrays.asList(new Turn[] {
      Turn.E1, Turn.E3, Turn.S1, Turn.S3, Turn.M1, Turn.M3, }));
  private static final List<Turn> halfSliceTurns = Collections.unmodifiableList(Arrays.asList(new Turn[] {
      Turn.E2, Turn.S2, Turn.M2, }));
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
