package acube.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import acube.Metric;
import acube.Turn;
import acube.TurnB;

public final class TurnTest {
  @Test
  public void testMetric() {
    assertEquals(1, Metric.QTM.length(Turn.U1));
    assertEquals(1, Metric.FTM.length(Turn.U1));
    assertEquals(1, Metric.STM.length(Turn.U1));
    assertEquals(1, Metric.SQTM.length(Turn.U1));
    assertEquals(2, Metric.QTM.length(Turn.U2));
    assertEquals(1, Metric.FTM.length(Turn.U2));
    assertEquals(1, Metric.STM.length(Turn.U2));
    assertEquals(2, Metric.SQTM.length(Turn.U2));
    assertEquals(2, Metric.QTM.length(Turn.E1));
    assertEquals(2, Metric.FTM.length(Turn.E1));
    assertEquals(1, Metric.STM.length(Turn.E1));
    assertEquals(1, Metric.SQTM.length(Turn.E1));
    assertEquals(4, Metric.QTM.length(Turn.E2));
    assertEquals(2, Metric.FTM.length(Turn.E2));
    assertEquals(1, Metric.STM.length(Turn.E2));
    assertEquals(2, Metric.SQTM.length(Turn.E2));
    assertEquals(1, Metric.QTM.length(Turn.r1));
    assertEquals(1, Metric.FTM.length(Turn.r1));
    assertEquals(1, Metric.STM.length(Turn.r1));
    assertEquals(1, Metric.SQTM.length(Turn.r1));
    assertEquals(2, Metric.QTM.length(Turn.r2));
    assertEquals(1, Metric.FTM.length(Turn.r2));
    assertEquals(1, Metric.STM.length(Turn.r2));
    assertEquals(2, Metric.SQTM.length(Turn.r2));
    // rotation of cube centers counts as one move too
    assertEquals(1, Metric.RQTM.length(Turn.U1));
    assertEquals(1, Metric.RFTM.length(Turn.U1));
    assertEquals(2, Metric.RQTM.length(Turn.U2));
    assertEquals(1, Metric.RFTM.length(Turn.U2));
    assertEquals(3, Metric.RQTM.length(Turn.E1));
    assertEquals(3, Metric.RFTM.length(Turn.E1));
    assertEquals(6, Metric.RQTM.length(Turn.E2));
    assertEquals(3, Metric.RFTM.length(Turn.E2));
    assertEquals(2, Metric.RQTM.length(Turn.r1));
    assertEquals(2, Metric.RFTM.length(Turn.r1));
    assertEquals(4, Metric.RQTM.length(Turn.r2));
    assertEquals(2, Metric.RFTM.length(Turn.r2));
  }

  @Test
  public void testAB() {
    for (TurnB t : TurnB.values())
      assertEquals(t, t.toA().toB());
    for (TurnB t1 : TurnB.values())
      for (TurnB t2 : TurnB.values())
        if (t1 != t2)
          assertFalse(t1.toA() == t2.toA());
    assertEquals(TurnB.U1, Turn.U1.toB());
    assertEquals(Turn.U1, TurnB.U1.toA());
    assertEquals(TurnB.M2, Turn.M2.toB());
    assertEquals(Turn.M2, TurnB.M2.toA());
    assertEquals(TurnB.r2, Turn.r2.toB());
    assertEquals(Turn.r2, TurnB.r2.toA());
    assertEquals(TurnB.U1.ordinal(), Turn.toB(Turn.U1.ordinal()));
    assertEquals(Turn.U1.ordinal(), TurnB.toA(TurnB.U1.ordinal()));
    assertEquals(TurnB.M2.ordinal(), Turn.toB(Turn.M2.ordinal()));
    assertEquals(Turn.M2.ordinal(), TurnB.toA(TurnB.M2.ordinal()));
    assertEquals(TurnB.r2.ordinal(), Turn.toB(Turn.r2.ordinal()));
    assertEquals(Turn.r2.ordinal(), TurnB.toA(TurnB.r2.ordinal()));
    assertTrue(Turn.U1.isB());
    assertTrue(Turn.U2.isB());
    assertFalse(Turn.B1.isB());
    assertTrue(Turn.B2.isB());
    assertTrue(Turn.r2.isB());
    assertFalse(Turn.r3.isB());
    assertEquals(TurnB.size(), Turn.getValidB(Turn.values()).length);
    assertEquals(TurnB.size(), Turn.getValidB(Turn.getValidB(Turn.values())).length);
    assertEquals(0, Turn.getValidB(new Turn[] { Turn.R1 }).length);
  }

  @Test
  public void testMetricFilter() {
    assertEquals(18 * 2, Metric.FTM.filterForElementary(Turn.values()).length);
    assertEquals(18, Metric.RFTM.filterForElementary(Turn.values()).length);
  }

  @Test
  public void testIntConversions() {
    assertEquals(Turn.U1, Turn.turn(0));
    assertEquals(Turn.U2, Turn.turn(1));
    assertEquals(Turn.r3, Turn.turn(Turn.size() - 1));
    assertEquals(TurnB.U1, TurnB.turn(0));
    assertEquals(TurnB.U2, TurnB.turn(1));
    assertEquals(TurnB.r2, TurnB.turn(TurnB.size() - 1));
  }
}
