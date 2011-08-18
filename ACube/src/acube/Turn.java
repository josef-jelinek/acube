package acube;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public enum Turn {
  U1, U2, U3, D1, D2, D3, F1, F2, F3, B1, B2, B3, L1, L2, L3, R1, R2, R3, E1, E2, E3, S1, S2, S3, M1, M2, M3, u1, u2,
  u3, d1, d2, d3, f1, f2, f3, b1, b2, b3, l1, l2, l3, r1, r2, r3;
  private static String[] names =
      "U U2 U' D D2 D' F F2 F' B B2 B' L L2 L' R R2 R' E E2 E' S S2 S' M M2 M' u u2 u' d d2 d' f f2 f' b b2 b' l l2 l' r r2 r'"
          .split(" ");
  public static final int size = values().length;
  public static final SortedSet<Turn> valueSet = Collections.unmodifiableSortedSet(new TreeSet<Turn>(Arrays
      .asList(values())));

  public static Turn turn(final int turn) {
    return values()[turn];
  }

  private static int[] tob;

  public TurnB toB() {
    return TurnB.turn(toB(ordinal()));
  }

  private static int toB(final int turn) {
    if (tob == null) {
      final int[] b = new int[size];
      Arrays.fill(b, -1);
      for (final TurnB turnB : TurnB.values())
        b[Turn.valueOf(turnB.name()).ordinal()] = turnB.ordinal();
      tob = b;
    }
    return tob[turn];
  }

  public static Set<Turn> getValidB(final Set<Turn> turns) {
    final Set<Turn> turnsB = new HashSet<Turn>();
    for (final Turn turn : turns)
      if (turn.isB())
        turnsB.add(turn);
    return turnsB;
  }

  public boolean isB() {
    return toB(ordinal()) >= 0;
  }

  @Override
  public String toString() {
    return names[ordinal()];
  }
}
