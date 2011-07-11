package acube;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

public final class TurnTest {

  private Metric metric;

  @Before
  public void setUp() {
    metric = Metric.obj(Metric.FTM);
  }

  private int[][] lens = {
    {Turn.U1, Metric.QTM, 1},
    {Turn.U1, Metric.FTM, 1},
    {Turn.U1, Metric.STM, 1},
    {Turn.U1, Metric.SQTM, 1},
    {Turn.U2, Metric.QTM, 2},
    {Turn.U2, Metric.FTM, 1},
    {Turn.U2, Metric.STM, 1},
    {Turn.U2, Metric.SQTM, 2},
    {Turn.E1, Metric.QTM, 2},
    {Turn.E1, Metric.FTM, 2},
    {Turn.E1, Metric.STM, 1},
    {Turn.E1, Metric.SQTM, 1},
    {Turn.E2, Metric.QTM, 4},
    {Turn.E2, Metric.FTM, 2},
    {Turn.E2, Metric.STM, 1},
    {Turn.E2, Metric.SQTM, 2},
  };

  @Test
  public void testLength() {
    for (int i = 0; i < lens.length; i++)
      assertEquals("row=" + i, lens[i][2], metric.length(lens[i][0], lens[i][1]));
    for (int t = 0; t < Turn.N; t++)
      assertEquals(metric.length(t), metric.length(t, Metric.FTM));
  }

  @Test
  public void testAB() {
    for (int i = 0; i < TurnB.N; i++)
      assertEquals(i, TurnB.fromA(TurnB.toA(i)));
    for (int i = 0; i < TurnB.N - 1; i++)
      for (int j = i + 1; j < TurnB.N; j++)
        assertFalse(TurnB.toA(i) == TurnB.toA(j));
  }
}
