package acube.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import java.util.HashSet;
import org.junit.Test;
import acube.Metric;
import acube.Turn;

public final class TurnTest {
  @Test
  public void testMetric() {
    assertEquals(1, Metric.QUARTER.length(Turn.U1));
    assertEquals(1, Metric.FACE.length(Turn.U1));
    assertEquals(1, Metric.SLICE.length(Turn.U1));
    assertEquals(1, Metric.SLICE_QUARTER.length(Turn.U1));
    assertEquals(2, Metric.QUARTER.length(Turn.U2));
    assertEquals(1, Metric.FACE.length(Turn.U2));
    assertEquals(1, Metric.SLICE.length(Turn.U2));
    assertEquals(2, Metric.SLICE_QUARTER.length(Turn.U2));
    assertEquals(2, Metric.QUARTER.length(Turn.E1));
    assertEquals(2, Metric.FACE.length(Turn.E1));
    assertEquals(1, Metric.SLICE.length(Turn.E1));
    assertEquals(1, Metric.SLICE_QUARTER.length(Turn.E1));
    assertEquals(4, Metric.QUARTER.length(Turn.E2));
    assertEquals(2, Metric.FACE.length(Turn.E2));
    assertEquals(1, Metric.SLICE.length(Turn.E2));
    assertEquals(2, Metric.SLICE_QUARTER.length(Turn.E2));
    assertEquals(1, Metric.QUARTER.length(Turn.r1));
    assertEquals(1, Metric.FACE.length(Turn.r1));
    assertEquals(1, Metric.SLICE.length(Turn.r1));
    assertEquals(1, Metric.SLICE_QUARTER.length(Turn.r1));
    assertEquals(2, Metric.QUARTER.length(Turn.r2));
    assertEquals(1, Metric.FACE.length(Turn.r2));
    assertEquals(1, Metric.SLICE.length(Turn.r2));
    assertEquals(2, Metric.SLICE_QUARTER.length(Turn.r2));
    // rotation of cube centers counts as one move too
    assertEquals(1, Metric.ROTATION_QUARTER.length(Turn.U1));
    assertEquals(1, Metric.ROTATION_FACE.length(Turn.U1));
    assertEquals(2, Metric.ROTATION_QUARTER.length(Turn.U2));
    assertEquals(1, Metric.ROTATION_FACE.length(Turn.U2));
    assertEquals(3, Metric.ROTATION_QUARTER.length(Turn.E1));
    assertEquals(3, Metric.ROTATION_FACE.length(Turn.E1));
    assertEquals(6, Metric.ROTATION_QUARTER.length(Turn.E2));
    assertEquals(3, Metric.ROTATION_FACE.length(Turn.E2));
    assertEquals(2, Metric.ROTATION_QUARTER.length(Turn.r1));
    assertEquals(2, Metric.ROTATION_FACE.length(Turn.r1));
    assertEquals(4, Metric.ROTATION_QUARTER.length(Turn.r2));
    assertEquals(2, Metric.ROTATION_FACE.length(Turn.r2));
  }

  @Test
  public void testAB() {
    int countB = 0;
    for (final Turn t : Turn.values())
      if (t.isB())
        countB++;
    assertTrue(Turn.U1.isB());
    assertTrue(Turn.U2.isB());
    assertFalse(Turn.B1.isB());
    assertTrue(Turn.B2.isB());
    assertTrue(Turn.r2.isB());
    assertFalse(Turn.r3.isB());
    assertEquals(countB, Turn.getValidB(Turn.valueSet).size());
    assertEquals(countB, Turn.getValidB(Turn.getValidB(Turn.valueSet)).size());
    assertEquals(0, Turn.getValidB(new HashSet<Turn>(Arrays.asList(new Turn[] { Turn.R1 }))).size());
  }

  @Test
  public void testMetricFilter() {
    assertEquals(18 * 2, Metric.FACE.filterForElementary(Turn.valueSet).size());
    assertEquals(18, Metric.ROTATION_FACE.filterForElementary(Turn.valueSet).size());
  }

  @Test
  public void testIntConversions() {
    assertEquals(Turn.U1, Turn.turn(0));
    assertEquals(Turn.U2, Turn.turn(1));
    assertEquals(Turn.r3, Turn.turn(Turn.size - 1));
  }
}
