package acube.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import acube.Turn;
import acube.TurnB;

public final class TransformTest {

  private Transform t;
  private TransformB tB;
  private final int[] cornMask = new int[] {1, 1, 1, 1, 0, 0, 0, 0};
  private final int[] edgeMask = new int[] {1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0};
  private final int[] cornOriMask = new int[] {0, 0, 0, 1, 1, 1, 1, 1};
  private final int[] edgeOriMask = new int[] {0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0};

  @Before
  public void setUp() {
    t = Transform.obj(cornMask, edgeMask, cornOriMask, edgeOriMask);
    tB = TransformB.obj(cornMask, edgeMask);
  }

  private void checkTab(ToMove t, int states, int turns) {
    assertEquals(t.stateN(), states);
    assertEquals(t.turnN(), turns);
    // reachability test (depth-first maze walkthrough)
    boolean[] used = new boolean[states];
    int[] prev = new int[states];
    int[] nextturn = new int[states];
    int cur = gofresh(0, -1, prev, nextturn, used);
    while (cur >= 0) {
      while (nextturn[cur] < turns) {
        int x = t.turn(nextturn[cur]++, cur);
        if (!used[x])
          cur = gofresh(x, cur, prev, nextturn, used);
      }
      cur = prev[cur];
    }
    for (int i = 0; i < states; i++)
      assertTrue("unreachable states", used[i]);
  }

  private int gofresh(int x, int cur, int[] prev, int[] nextturn, boolean[] used) {
    prev[x] = cur;
    used[x] = true;
    nextturn[x] = 0;
    return x;
  }

  @Test
  public void testTables() {
    checkTab(t.cornPos, 8*7*6*5, Turn.N);
    checkTab(t.cornTwist, 2187, Turn.N);
    checkTab(t.edgeFlip, 2048, Turn.N);
    checkTab(t.midgeLoc, 12*11/2, Turn.N);
    checkTab(t.midgePos, 12*11, Turn.N);
    checkTab(t.udgePos, 12*11*10*9, Turn.N);
    checkTab(t.dedgePos, 12*11, Turn.N);
    checkTab(tB.midgePos, 4*3, TurnB.N);
    checkTab(tB.edgePos, 8*7*6*5*4*3, TurnB.N);
  }

  @Test
  public void testTable2() {
    try {
      new MoveTab2(t.dedgePos, tB.midgePos);
      fail("IllegalArgumentException should have been thrown");
    } catch (IllegalArgumentException ex) { /*OK*/ }
    MoveTab2 move = new MoveTab2(t.midgeLoc, t.dedgePos);
    assertEquals(move.stateN(), t.midgeLoc.stateN() * t.dedgePos.stateN());
    assertEquals(move.turnN(), t.midgeLoc.turnN());
    assertEquals(move.turnN(), t.dedgePos.turnN());
    assertEquals(move.startN(), t.midgeLoc.startN() * t.dedgePos.startN());
    for (int i = 0; i < move.stateN(); i++) {
      for (int j = 0; j < move.turnN(); j++) {
        assertEquals(move.turn(j, i) / t.dedgePos.stateN(), t.midgeLoc.turn(j, i / t.dedgePos.stateN()));
        assertEquals(move.turn(j, i) % t.dedgePos.stateN(), t.dedgePos.turn(j, i % t.dedgePos.stateN()));
      }
    }
    for (int i = 0; i < move.startN(); i++) {
      assertEquals(move.start(i) / t.dedgePos.stateN(), t.midgeLoc.start(i / t.dedgePos.startN()));
      assertEquals(move.start(i) % t.dedgePos.stateN(), t.dedgePos.start(i % t.dedgePos.startN()));
    }
  }

  @Test
  public void testTableB() {
    assertEquals(tB.cornPos.stateN(), t.cornPos.stateN());
    assertEquals(t.cornPos.turnN(), Turn.N);
    assertEquals(tB.cornPos.turnN(), TurnB.N);
    assertEquals(tB.cornPos.startN(), t.cornPos.startN());
    for (int i = 0; i < tB.cornPos.stateN(); i++)
      for (int j = 0; j < TurnB.N; j++)
        assertEquals(t.cornPos.turn(TurnB.toA(j), i), tB.cornPos.turn(j, i));
    for (int i = 0; i < tB.cornPos.startN(); i++)
      assertEquals(tB.cornPos.start(i), t.cornPos.start(i));
  }

  private static final int[][] revB = {
    {TurnB.U1, TurnB.U3},
    {TurnB.U2, TurnB.U2},
    {TurnB.D1, TurnB.D3},
    {TurnB.D2, TurnB.D2},
    {TurnB.F2, TurnB.F2},
    {TurnB.B2, TurnB.B2},
    {TurnB.L2, TurnB.L2},
    {TurnB.R2, TurnB.R2},
    {TurnB.E1, TurnB.E3},
    {TurnB.E2, TurnB.E2},
    {TurnB.S2, TurnB.S2},
    {TurnB.M2, TurnB.M2},
    {TurnB.u1, TurnB.u3},
    {TurnB.u2, TurnB.u2},
    {TurnB.d1, TurnB.d3},
    {TurnB.d2, TurnB.d2},
    {TurnB.f2, TurnB.f2},
    {TurnB.b2, TurnB.b2},
    {TurnB.l2, TurnB.l2},
    {TurnB.r2, TurnB.r2},
  };

  @Test
  public void testReverseB() {
    for (int i = 0; i < tB.midgePos.stateN(); i++) {
      for (int j = 0; j < revB.length; j++) {
        assertEquals(i, tB.midgePos.turn(revB[j][0], tB.midgePos.turn(revB[j][1], i)));
        assertEquals(i, tB.midgePos.turn(revB[j][1], tB.midgePos.turn(revB[j][0], i)));
      }
    }
    for (int i = 0; i < tB.edgePos.stateN(); i++) {
      for (int j = 0; j < revB.length; j++) {
        assertEquals(i, tB.edgePos.turn(revB[j][0], tB.edgePos.turn(revB[j][1], i)));
        assertEquals(i, tB.edgePos.turn(revB[j][1], tB.edgePos.turn(revB[j][0], i)));
      }
    }
    for (int i = 0; i < tB.cornPos.stateN(); i++) {
      for (int j = 0; j < revB.length; j++) {
        assertEquals(i, tB.cornPos.turn(revB[j][0], tB.cornPos.turn(revB[j][1], i)));
        assertEquals(i, tB.cornPos.turn(revB[j][1], tB.cornPos.turn(revB[j][0], i)));
      }
    }
  }
}
