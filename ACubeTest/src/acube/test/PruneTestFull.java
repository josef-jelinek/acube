package acube.test;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import acube.Corner;
import acube.Edge;
import acube.console.ConsoleReporter;
import acube.prune.Prune;
import acube.prune.PruneB;
import acube.transform.Transform;
import acube.transform.TransformB;

public final class PruneTestFull {
  @Test
  public void sizes_of_tables_A() {
    final Transform transform = new Transform(new ConsoleReporter());
    final Prune tab = new Prune(transform, new ConsoleReporter());
    assertEquals(6575301, tab.stateSize());
    assertEquals(1643826, tab.memorySize());
  }

  @Test
  public void sizes_of_tables_B() {
    final TransformB transform = new TransformB(Corner.valueSet, Edge.valueSet, new ConsoleReporter());
    final PruneB tab = new PruneB(transform, new ConsoleReporter());
    assertEquals(1935360, tab.stateSize());
    assertEquals(483840, tab.memorySize());
  }
}
