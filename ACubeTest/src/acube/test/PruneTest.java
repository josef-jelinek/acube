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
import java.util.EnumSet;
import org.junit.Test;
import acube.Corner;
import acube.Edge;
import acube.console.ConsoleReporter;
import acube.prune.Prune;
import acube.prune.PruneB;
import acube.prune.PruneCorners;
import acube.transform.Transform;
import acube.transform.TransformB;

public final class PruneTest {
  private final EnumSet<Corner> cornerMask = EnumSet.of(UFR, URB, UBL, ULF);
  private final EnumSet<Edge> edgeMask = EnumSet.of(UF, UR, UB, UL, DF, DR, FR, FL);
  private final EnumSet<Corner> cornerTwistMask = EnumSet.of(ULF, DRF, DFL, DLB, DBR);
  private final EnumSet<Edge> edgeFlipMask = EnumSet.of(UL, DF, DR, DB, DL, FR, FL);

  @Test
  public void tables_A() {
    final Transform transform =
        new Transform(cornerMask, edgeMask, cornerTwistMask, edgeFlipMask, new ConsoleReporter());
    final Prune tab = new Prune(transform, new ConsoleReporter());
    assertEquals(4758486, tab.stateSize());
    assertEquals(6, tab.maxDist());
  }

  @Test
  public void table_corners_face_metric() {
    final Transform transform = new Transform(new ConsoleReporter());
    final PruneCorners tab = new PruneCorners(transform, new ConsoleReporter());
    assertEquals(88179840, tab.stateSize());
    assertEquals(11, tab.maxDist());
  }

  @Test
  public void table_corners_quarter_metric() {
    final Transform transform = new Transform(new ConsoleReporter());
    final PruneCorners tab = new PruneCorners(transform, new ConsoleReporter());
    assertEquals(88179840, tab.stateSize());
    assertEquals(11, tab.maxDist());
  }

  @Test
  public void tables_B() {
    final TransformB transform = new TransformB(cornerMask, edgeMask, new ConsoleReporter());
    final PruneB tab = new PruneB(transform, new ConsoleReporter());
    assertEquals(262080, tab.stateSize());
    assertEquals(9, tab.maxDist());
  }
}
