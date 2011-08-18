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

public final class PruneTestFull {
  @Test
  public void sizes_of_tables_A() {
    final Transform transform = new Transform(Turn.valueSet);
    final Prune tab = new Prune(transform);
    assertEquals(6575301, tab.stateSize());
    assertEquals(1643826, tab.memorySize());
  }

  @Test
  public void sizes_of_tables_B() {
    final TransformB transform = new TransformB(Corner.valueSet, Edge.valueSet, Turn.valueSet);
    final PruneB tab = new PruneB(transform);
    assertEquals(1935360, tab.stateSize());
    assertEquals(483840, tab.memorySize());
  }
}
