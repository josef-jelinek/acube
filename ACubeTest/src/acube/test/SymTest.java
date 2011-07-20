package acube.test;

import static acube.SymTransform.B;
import static acube.SymTransform.D;
import static acube.SymTransform.DL;
import static acube.SymTransform.I;
import static acube.SymTransform.L;
import static acube.SymTransform.LF;
import static acube.SymTransform.SymmetryCount;
import static acube.SymTransform.U;
import static acube.SymTransform.getSymmetry;
import static acube.SymTransform.getTurn;
import static acube.Turn.D1;
import static acube.Turn.E3;
import static acube.Turn.F1;
import static acube.Turn.M1;
import static acube.Turn.R1;
import static acube.Turn.S1;
import static acube.Turn.b1;
import static acube.Turn.l1;
import static acube.Turn.r1;
import static acube.Turn.u1;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import acube.Turn;

public final class SymTest {
  @Test
  public void output() {
    assertEquals(R1, getTurn(R1, I));
    assertEquals(S1, getTurn(S1, I));
    assertEquals(u1, getTurn(u1, I));
    assertEquals(F1, getTurn(R1, D));
    assertEquals(M1, getTurn(S1, D));
    assertEquals(u1, getTurn(u1, D));
    assertEquals(D1, getTurn(R1, B));
    assertEquals(S1, getTurn(S1, B));
    assertEquals(r1, getTurn(u1, B));
    assertEquals(R1, getTurn(R1, L));
    assertEquals(E3, getTurn(S1, L));
    assertEquals(b1, getTurn(u1, L));
  }

  @Test
  public void testTransition() {
    assertEquals(I, getSymmetry(R1, I));
    assertEquals(U, getSymmetry(R1, U));
    assertEquals(L, getSymmetry(M1, I));
    assertEquals(LF, getSymmetry(M1, U));
    assertEquals(DL, getSymmetry(M1, D));
    assertEquals(DL, getSymmetry(l1, D));
  }

  @Test
  public void outputs_are_unique() {
    for (int s = 0; s < SymmetryCount; s++)
      for (final Turn t1 : Turn.values())
        for (final Turn t2 : Turn.values())
          assertTrue(t1 == t2 ^ getTurn(t1, s) != getTurn(t2, s));
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
  public void transitions_are_unique() {
    for (final Turn t : Turn.values())
      for (int s1 = 0; s1 < SymmetryCount - 1; s1++)
        for (int s2 = s1 + 1; s2 < SymmetryCount; s2++)
          assertFalse(getSymmetry(t, s1) == getSymmetry(t, s2));
    for (int s = 0; s < SymmetryCount; s++)
      for (final Turn[] diffsym : diffsyms)
        for (int t1 = 0; t1 < diffsym.length - 1; t1++)
          for (int t2 = t1 + 1; t2 < diffsym.length; t2++)
            assertFalse(getSymmetry(diffsym[t1], s) == getSymmetry(diffsym[t2], s));
  }
}
