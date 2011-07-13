package acube.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Before;
import org.junit.Test;
import acube.SymTransform;
import acube.Turn;

public final class SymTest {
  private SymTransform t;

  @Before
  public void setUp() {
    t = SymTransform.obj;
  }

  private int[][] turnTransforms = {
      { SymTransform.I, Turn.R1.ordinal(), Turn.R1.ordinal() },
      { SymTransform.I, Turn.S1.ordinal(), Turn.S1.ordinal() },
      { SymTransform.I, Turn.u1.ordinal(), Turn.u1.ordinal() },
      { SymTransform.D, Turn.R1.ordinal(), Turn.F1.ordinal() },
      { SymTransform.D, Turn.S1.ordinal(), Turn.M1.ordinal() },
      { SymTransform.D, Turn.u1.ordinal(), Turn.u1.ordinal() },
      { SymTransform.B, Turn.R1.ordinal(), Turn.D1.ordinal() },
      { SymTransform.B, Turn.S1.ordinal(), Turn.S1.ordinal() },
      { SymTransform.B, Turn.u1.ordinal(), Turn.r1.ordinal() },
      { SymTransform.L, Turn.R1.ordinal(), Turn.R1.ordinal() },
      { SymTransform.L, Turn.S1.ordinal(), Turn.E3.ordinal() },
      { SymTransform.L, Turn.u1.ordinal(), Turn.b1.ordinal() },
  // ...
      };

  @Test
  public void testOutput() {
    for (int i = 0; i < turnTransforms.length; i++)
      assertEquals(turnTransforms[i][2],
          t.turn(Turn.values()[turnTransforms[i][1]], turnTransforms[i][0]).ordinal());
  }

  private int[][] turnEffects = {
      { SymTransform.I, Turn.R1.ordinal(), SymTransform.I },
      { SymTransform.U, Turn.R1.ordinal(), SymTransform.U },
      { SymTransform.I, Turn.M1.ordinal(), SymTransform.L },
      { SymTransform.U, Turn.M1.ordinal(), SymTransform.LF },
      { SymTransform.D, Turn.M1.ordinal(), SymTransform.DL },
      { SymTransform.D, Turn.l1.ordinal(), SymTransform.DL },
  // ...
      };

  @Test
  public void testTransition() {
    for (int i = 0; i < turnEffects.length; i++)
      assertEquals(turnEffects[i][2], t.sym(Turn.values()[turnEffects[i][1]], turnEffects[i][0]));
  }

  @Test
  public void testOutputUnique() {
    for (int s = 0; s < SymTransform.N; s++)
      for (int t1 = 0; t1 < Turn.size() - 1; t1++)
        for (int t2 = t1 + 1; t2 < Turn.size(); t2++)
          assertFalse(t.turn(Turn.values()[t1], s) == t.turn(Turn.values()[t2], s));
  }

  Turn[][] diffsyms = {
      { Turn.E1, Turn.E2, Turn.E3, Turn.M1, Turn.M2, Turn.M3, Turn.S1, Turn.S2, Turn.S3, Turn.U1 },
      { Turn.r1, Turn.r2, Turn.r3, Turn.u1, Turn.u2, Turn.u3, Turn.f1, Turn.f2, Turn.f3, Turn.B1 },
      { Turn.l1, Turn.l2, Turn.l3, Turn.d1, Turn.d2, Turn.d3, Turn.b1, Turn.b2, Turn.b3, Turn.R1 },
      { Turn.E1, Turn.r1, Turn.l1, Turn.S2, Turn.r2, Turn.u2, Turn.f3, Turn.b3, Turn.L1 },
      { Turn.d1, Turn.f1, Turn.l1, Turn.d2, Turn.f2, Turn.l2, Turn.d3, Turn.f3, Turn.l3, Turn.F1 },
      { Turn.d1, Turn.b3, Turn.M1, Turn.d2, Turn.b2, Turn.M2, Turn.d3, Turn.b1, Turn.M3, Turn.D1 },
  // ...
      };

  @Test
  public void testTransitionUnique() {
    for (int t1 = 0; t1 < Turn.size(); t1++)
      for (int s1 = 0; s1 < SymTransform.N - 1; s1++)
        for (int s2 = s1 + 1; s2 < SymTransform.N; s2++)
          assertFalse(t.sym(Turn.values()[t1], s1) == t.sym(Turn.values()[t1], s2));
    for (int s = 0; s < SymTransform.N; s++)
      for (int r = 0; r < diffsyms.length; r++)
        for (int t1 = 0; t1 < diffsyms[r].length - 1; t1++)
          for (int t2 = t1 + 1; t2 < diffsyms[r].length; t2++)
            assertFalse(t.sym(diffsyms[r][t1], s) == t.sym(diffsyms[r][t2], s));
  }
}
