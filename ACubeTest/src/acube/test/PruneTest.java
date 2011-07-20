package acube.test;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import acube.Corner;
import acube.Edge;
import acube.Metric;
import acube.Turn;
import acube.prune.Prune;
import acube.prune.PruneB;
import acube.prune.PruneCorners;
import acube.transform.Transform;
import acube.transform.TransformB;

//@Ignore
public final class PruneTest {
  private final Corner[] cornerMask = { Corner.UFR, Corner.URB, Corner.UBL, Corner.ULF };
  private final Edge[] edgeMask = { Edge.UF, Edge.UR, Edge.UB, Edge.UL, Edge.DF, Edge.DR, Edge.FR, Edge.FL };
  private final Corner[] cornerTwistMask = { Corner.ULF, Corner.DRF, Corner.DFL, Corner.DLB, Corner.DBR };
  private final Edge[] edgeFlipMask = { Edge.UL, Edge.DF, Edge.DR, Edge.DB, Edge.DL, Edge.FR, Edge.FL };

  @Test
  public void test_tables_A() {
    final Transform transform = new Transform(cornerMask, edgeMask, cornerTwistMask, edgeFlipMask, Turn.values());
    final Prune tab = new Prune(transform);
    assertEquals(4758486, tab.stateSize());
    assertEquals(6, tab.maxDistance());
  }

  @Test
  public void test_table_corners_face_metric() {
    final Transform transform =
        new Transform(Corner.values(), Edge.values(), Corner.values(), Edge.values(),
            Metric.ROTATION_FACE.filterForElementary(Turn.values()));
    final PruneCorners tab = new PruneCorners(transform);
    assertEquals(88179840, tab.stateSize());
    assertEquals(11, tab.maxDistance());
  }

  @Test
  public void test_table_corners_quarter_metric() {
    final Transform transform =
        new Transform(Corner.values(), Edge.values(), Corner.values(), Edge.values(),
            Metric.QUARTER.filterForElementary(Turn.values()));
    final PruneCorners tab = new PruneCorners(transform);
    assertEquals(88179840, tab.stateSize());
    assertEquals(14, tab.maxDistance());
  }

  @Test
  public void test_tables_B() {
    final TransformB transform = TransformB.instance(cornerMask, edgeMask, Turn.values());
    final PruneB tab = new PruneB(transform);
    assertEquals(262080, tab.stateSize());
    assertEquals(9, tab.maxDistance());
  }
}
