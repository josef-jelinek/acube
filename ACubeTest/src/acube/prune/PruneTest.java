package acube.prune;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import acube.transform.Transform;
import acube.transform.TransformB;

public final class PruneTest {

  private final int[] cornMask = new int[] {1, 1, 1, 1, 0, 0, 0, 0};
  private final int[] edgeMask = new int[] {1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0};
  private final int[] cornOriMask = new int[] {0, 0, 0, 1, 1, 1, 1, 1};
  private final int[] edgeOriMask = new int[] {0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0};

  @Test
  public void testTables() {
    Transform trn = Transform.obj(cornMask, edgeMask, cornOriMask, edgeOriMask);
    Prune tab = new Prune(trn);
    assertEquals(6, tab.maxDistance());
    assertEquals(4758486, tab.stateN());
    for (int ct = 0; ct < trn.cornTwist.stateN(); ct += trn.cornTwist.stateN() / 7) {
      for (int ef = 0; ef < trn.edgeFlip.stateN(); ef += trn.edgeFlip.stateN() / 7) {
        for (int ml = 0; ml < trn.midgeLoc.stateN(); ml += trn.midgeLoc.stateN() / 7) {
          int d = tab.distance(ct, ef, ml);
          assertTrue(d >= 0 && d <= tab.maxDistance());
        }
      }
    }
  }

  @Test
  public void testTablesB() {
    TransformB trn = TransformB.obj(cornMask, edgeMask);
    PruneB tab = new PruneB(trn);
    assertEquals(9, tab.maxDistance());
    assertEquals(262080, tab.stateN());
    for (int cp = 0; cp < trn.cornPos.stateN(); cp += trn.cornPos.stateN() / 7) {
      for (int ep = 0; ep < trn.edgePos.stateN(); ep += trn.edgePos.stateN() / 7) {
        for (int mp = 0; mp < trn.midgePos.stateN(); mp += trn.midgePos.stateN() / 7) {
          int d = tab.distance(cp, ep, mp);
          assertTrue(d >= 0 && d <= tab.maxDistance());
        }
      }
    }
  }
}
