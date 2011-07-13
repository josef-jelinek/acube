package acube.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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

public final class PruneTest {
  private Corner[] cornerMask = { Corner.UFR, Corner.URB, Corner.UBL, Corner.ULF };
  private Edge[] edgeMask = {
      Edge.UF, Edge.UR, Edge.UB, Edge.UL, Edge.DF, Edge.DR, Edge.FR, Edge.FL };
  private Corner[] cornerTwistMask = { Corner.ULF, Corner.DRF, Corner.DFL, Corner.DLB, Corner.DBR };
  private Edge[] edgeFlipMask = { Edge.UL, Edge.DF, Edge.DR, Edge.DB, Edge.DL, Edge.FR, Edge.FL };

  @Test
  public void test_tables_A() {
    Transform transform =
        Transform.instance(cornerMask, edgeMask, cornerTwistMask, edgeFlipMask, Turn.values());
    Prune tab = new Prune(transform);
    assertEquals(4758486, tab.size());
    assertEquals(6, tab.maxDistance());
    for (int ct = 0; ct < transform.cornerTwist.stateSize(); ct +=
        transform.cornerTwist.stateSize() / 7) {
      for (int ef = 0; ef < transform.edgeFlip.stateSize(); ef +=
          transform.edgeFlip.stateSize() / 7) {
        for (int ml = 0; ml < transform.mEdgePositionSet.stateSize(); ml +=
            transform.mEdgePositionSet.stateSize() / 7) {
          int d = tab.distance(ct, ef, ml);
          String s = "(ct:" + ct + " ef:" + ef + " ml:" + ml + ") distance " + d;
          assertTrue(s, d >= 0 && d <= tab.maxDistance());
        }
      }
    }
  }

  @Test
  public void test_table_corners_face_metric() {
    Transform transform =
        Transform.instance(Corner.values(), Edge.values(), Corner.values(), Edge.values(),
            Metric.RFTM.filterForElementary(Turn.values()));
    PruneCorners tab = new PruneCorners(transform);
    assertEquals(88179840, tab.size());
    assertEquals(11, tab.maxDistance());
    for (int ct = 0; ct < transform.cornerTwist.stateSize(); ct +=
        transform.cornerTwist.stateSize() / 7) {
      for (int cp = 0; cp < transform.cornerPosition.stateSize(); cp +=
          transform.cornerPosition.stateSize() / 7) {
        int d = tab.distance(ct, cp);
        String s = "(ct:" + ct + " cp:" + cp + ") distance " + d;
        assertTrue(s, d >= 0 && d <= tab.maxDistance());
      }
    }
  }

  @Test
  public void test_table_corners_quarter_metric() {
    Transform transform =
        Transform.instance(Corner.values(), Edge.values(), Corner.values(), Edge.values(),
            Metric.QTM.filterForElementary(Turn.values()));
    PruneCorners tab = new PruneCorners(transform);
    assertEquals(88179840, tab.size());
    assertEquals(14, tab.maxDistance());
    for (int ct = 0; ct < transform.cornerTwist.stateSize(); ct +=
        transform.cornerTwist.stateSize() / 7) {
      for (int cp = 0; cp < transform.cornerPosition.stateSize(); cp +=
          transform.cornerPosition.stateSize() / 7) {
        int d = tab.distance(ct, cp);
        String s = "(ct:" + ct + " cp:" + cp + ") distance " + d;
        assertTrue(s, d >= 0 && d <= tab.maxDistance());
      }
    }
  }

  @Test
  public void test_tables_B() {
    TransformB transform = TransformB.instance(cornerMask, edgeMask, Turn.values());
    PruneB tab = new PruneB(transform);
    assertEquals(262080, tab.stateSize());
    assertEquals(9, tab.maxDistance());
    for (int cp = 0; cp < transform.cornerPosition.stateSize(); cp +=
        transform.cornerPosition.stateSize() / 7) {
      for (int ep = 0; ep < transform.oEdgePosition.stateSize(); ep +=
          transform.oEdgePosition.stateSize() / 7) {
        for (int mp = 0; mp < transform.mEdgePosition.stateSize(); mp +=
            transform.mEdgePosition.stateSize() / 7) {
          int d = tab.distance(cp, ep, mp);
          String s = "(cp:" + cp + " ep:" + ep + " mp:" + mp + ") distance " + d;
          assertTrue(s, d >= 0 && d <= tab.maxDistance());
        }
      }
    }
  }
}
