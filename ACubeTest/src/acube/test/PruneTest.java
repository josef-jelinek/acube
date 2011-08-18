package acube.test;

import static acube.Corner.DBR;
import static acube.Corner.DFL;
import static acube.Corner.DLB;
import static acube.Corner.DRF;
import static acube.Corner.UBL;
import static acube.Corner.UFR;
import static acube.Corner.ULF;
import static acube.Corner.URB;
import static acube.Edge.DB;
import static acube.Edge.DF;
import static acube.Edge.DL;
import static acube.Edge.DR;
import static acube.Edge.FL;
import static acube.Edge.FR;
import static acube.Edge.UB;
import static acube.Edge.UF;
import static acube.Edge.UL;
import static acube.Edge.UR;
import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
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
  private final Set<Corner> cornerMask = asSet(new Corner[] { UFR, URB, UBL, ULF });
  private final Set<Edge> edgeMask = asSet(new Edge[] { UF, UR, UB, UL, DF, DR, FR, FL });
  private final Set<Corner> cornerTwistMask = asSet(new Corner[] { ULF, DRF, DFL, DLB, DBR });
  private final Set<Edge> edgeFlipMask = asSet(new Edge[] { UL, DF, DR, DB, DL, FR, FL });

  private <T> Set<T> asSet(final T[] a) {
    return new HashSet<T>(Arrays.asList(a));
  }

  @Test
  public void tables_A() {
    final Transform transform = new Transform(cornerMask, edgeMask, cornerTwistMask, edgeFlipMask, Turn.valueSet);
    final Prune tab = new Prune(transform);
    assertEquals(4758486, tab.stateSize());
    assertEquals(6, tab.maxDistance());
  }

  @Test
  public void table_corners_face_metric() {
    final Transform transform = new Transform(Metric.ROTATION_FACE.filterForElementary(Turn.valueSet));
    final PruneCorners tab = new PruneCorners(transform);
    assertEquals(88179840, tab.stateSize());
    assertEquals(11, tab.maxDistance());
  }

  @Test
  public void table_corners_quarter_metric() {
    final Transform transform = new Transform(Metric.QUARTER.filterForElementary(Turn.valueSet));
    final PruneCorners tab = new PruneCorners(transform);
    assertEquals(88179840, tab.stateSize());
    assertEquals(14, tab.maxDistance());
  }

  @Test
  public void tables_B() {
    final TransformB transform = new TransformB(cornerMask, edgeMask, Turn.valueSet);
    final PruneB tab = new PruneB(transform);
    assertEquals(262080, tab.stateSize());
    assertEquals(9, tab.maxDistance());
  }
}
