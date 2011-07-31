package acube.test;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import acube.Corner;
import acube.Edge;
import acube.Turn;
import acube.prune.Prune;
import acube.prune.PruneB;
import acube.transform.Transform;
import acube.transform.TransformB;

//@Ignore
public final class PruneTestFull {
  private final Corner[] cornerMask = Corner.values();
  private final Edge[] edgeMask = Edge.values();
  private final Corner[] cornerTwistMask = Corner.values();
  private final Edge[] edgeFlipMask = Edge.values();

  @Test
  public void sizes_of_tables_A() {
    final Transform transform = new Transform(cornerMask, edgeMask, cornerTwistMask, edgeFlipMask, Turn.values());
    final Prune tab = new Prune(transform);
    assertEquals(6575301, tab.stateSize());
    assertEquals(1643826, tab.memorySize());
  }

  @Test
  public void sizes_of_tables_B() {
    final TransformB transform = new TransformB(cornerMask, edgeMask, Turn.values());
    final PruneB tab = new PruneB(transform);
    assertEquals(1935360, tab.stateSize());
    assertEquals(483840, tab.memorySize());
  }
}
