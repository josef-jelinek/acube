package acube.test;

import static acube.SymTransform.B;
import static acube.SymTransform.D;
import static acube.SymTransform.DL;
import static acube.SymTransform.DR;
import static acube.SymTransform.F;
import static acube.SymTransform.I;
import static acube.SymTransform.L;
import static acube.SymTransform.LD;
import static acube.SymTransform.LF;
import static acube.SymTransform.SYM_COUNT;
import static acube.SymTransform.U;
import static acube.SymTransform.getSymmetry;
import static acube.SymTransform.getTurn;
import static acube.Turn.B1;
import static acube.Turn.B2;
import static acube.Turn.D1;
import static acube.Turn.D3;
import static acube.Turn.E1;
import static acube.Turn.E3;
import static acube.Turn.F1;
import static acube.Turn.F2;
import static acube.Turn.L1;
import static acube.Turn.L2;
import static acube.Turn.M1;
import static acube.Turn.M2;
import static acube.Turn.M3;
import static acube.Turn.R1;
import static acube.Turn.R2;
import static acube.Turn.S1;
import static acube.Turn.S2;
import static acube.Turn.S3;
import static acube.Turn.U1;
import static acube.Turn.U2;
import static acube.Turn.U3;
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
  public void turn_to_turn_for_symmetry() {
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
    assertEquals(U1, getTurn(R1, LD));
    assertEquals(B1, getTurn(U1, LD));
    assertEquals(F1, getTurn(R1, DR));
  }

  @Test
  public void symmetry_to_symmetry_for_turn() {
    assertEquals(I, getSymmetry(I, R1));
    assertEquals(U, getSymmetry(U, R1));
    assertEquals(L, getSymmetry(I, M1));
    assertEquals(LF, getSymmetry(U, M1));
    assertEquals(DL, getSymmetry(D, M1));
    assertEquals(DL, getSymmetry(D, l1));
    assertEquals(LD, getSymmetry(F, M1));
  }

  @Test
  public void super_flip_solution() {
    int s = check_transform(I, S1, S1);
    s = check_transform(s, U1, L1);
    s = check_transform(s, F2, F2);
    s = check_transform(s, M1, E1);
    s = check_transform(s, F2, L2);
    s = check_transform(s, D1, F1);
    s = check_transform(s, S3, M3);
    s = check_transform(s, S2, M2);
    s = check_transform(s, D1, U1);
    s = check_transform(s, R2, B2);
    s = check_transform(s, E3, E1);
    s = check_transform(s, S2, S2);
    s = check_transform(s, U2, U2);
    s = check_transform(s, R2, L2);
    s = check_transform(s, D1, D1);
    s = check_transform(s, S2, S2);
    s = check_transform(s, U1, D1);
    s = check_transform(s, F2, B2);
    s = check_transform(s, U3, D3);
    //S L F2 E L2 F M' M2 U B2 E S2 U2 L2 D S2 D B2 D'
  }

  private int check_transform(final int symmetry, final Turn userTurn, final Turn cubeTurn) {
    assertEquals(cubeTurn, getTurn(userTurn, symmetry));
    return getSymmetry(symmetry, userTurn);
  }

  @Test
  public void outputs_are_unique() {
    for (int s = 0; s < SYM_COUNT; s++)
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
      for (int s1 = 0; s1 < SYM_COUNT - 1; s1++)
        for (int s2 = s1 + 1; s2 < SYM_COUNT; s2++)
          assertFalse(getSymmetry(s1, t) == getSymmetry(s2, t));
    for (int s = 0; s < SYM_COUNT; s++)
      for (final Turn[] diffsym : diffsyms)
        for (int t1 = 0; t1 < diffsym.length - 1; t1++)
          for (int t2 = t1 + 1; t2 < diffsym.length; t2++)
            assertFalse(getSymmetry(s, diffsym[t1]) == getSymmetry(s, diffsym[t2]));
  }
}
