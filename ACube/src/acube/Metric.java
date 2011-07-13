package acube;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum Metric {

  QTM, FTM, STM, SQTM, RQTM, RFTM;

  public static final String[] names = "QTM FTM STM SQTM RQTM RFTM".split(" ");

  public int length(Turn turn) {
    return turnLengths[ordinal()][turn.ordinal()];
  }

  public Turn[] filterForElementary(Turn[] turns) {
    List<Turn> list = new ArrayList<Turn>();
    for (Turn turn : turns)
      if (length(turn) == 1)
        list.add(turn);
    return list.toArray(new Turn[list.size()]);
  }
  private static final int[][] turnLengths = new int[values().length][Turn.size()];

  private static final List<Turn> faceTurns = Collections.unmodifiableList(Arrays.asList(new Turn[] {
    Turn.U2, Turn.D2, Turn.F2, Turn.B2, Turn.L2, Turn.R2,
  }));

  private static final List<Turn> doubleQuarterTurns = Collections.unmodifiableList(Arrays.asList(new Turn[] {
    Turn.u1, Turn.d1, Turn.f1, Turn.b1, Turn.l1, Turn.r1,
    Turn.u3, Turn.d3, Turn.f3, Turn.b3, Turn.l3, Turn.r3,
  }));

  private static final List<Turn> doubleFaceTurns = Collections.unmodifiableList(Arrays.asList(new Turn[] {
    Turn.u2, Turn.d2, Turn.f2, Turn.b2, Turn.l2, Turn.r2,
  }));

  private static final List<Turn> quarterSliceTurns = Collections.unmodifiableList(Arrays.asList(new Turn[] {
      Turn.E1, Turn.E3, Turn.S1, Turn.S3, Turn.M1, Turn.M3,
  }));

  private static final List<Turn> halfSliceTurns = Collections.unmodifiableList(Arrays.asList(new Turn[] {
    Turn.E2, Turn.S2, Turn.M2,
  }));

  static {
    for (int[] turnLengthMetric : turnLengths)
      for (int t = 0; t < turnLengthMetric.length; t++)
        turnLengthMetric[t] = 1;
    setLengths(turnLengths[QTM.ordinal()], faceTurns, 2);
    setLengths(turnLengths[SQTM.ordinal()], faceTurns, 2);
    setLengths(turnLengths[RQTM.ordinal()], faceTurns, 2);
    setLengths(turnLengths[RQTM.ordinal()], doubleQuarterTurns, 2);
    setLengths(turnLengths[RFTM.ordinal()], doubleQuarterTurns, 2);
    setLengths(turnLengths[QTM.ordinal()], doubleFaceTurns, 2);
    setLengths(turnLengths[SQTM.ordinal()], doubleFaceTurns, 2);
    setLengths(turnLengths[RQTM.ordinal()], doubleFaceTurns, 4);
    setLengths(turnLengths[RFTM.ordinal()], doubleFaceTurns, 2);
    setLengths(turnLengths[QTM.ordinal()], quarterSliceTurns, 2);
    setLengths(turnLengths[FTM.ordinal()], quarterSliceTurns, 2);
    setLengths(turnLengths[RQTM.ordinal()], quarterSliceTurns, 3);
    setLengths(turnLengths[RFTM.ordinal()], quarterSliceTurns, 3);
    setLengths(turnLengths[QTM.ordinal()], halfSliceTurns, 4);
    setLengths(turnLengths[FTM.ordinal()], halfSliceTurns, 2);
    setLengths(turnLengths[SQTM.ordinal()], halfSliceTurns, 2);
    setLengths(turnLengths[RQTM.ordinal()], halfSliceTurns, 6);
    setLengths(turnLengths[RFTM.ordinal()], halfSliceTurns, 3);
  }

  private static void setLengths(int[] turnLengths, List<Turn> turns, int length) {
    for (Turn turn : turns)
      turnLengths[turn.ordinal()] = length;
  }
}
