package acube;

import java.util.Arrays;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

public enum TurnB {
  U1, U2, U3, D1, D2, D3, F2, B2, L2, R2, E1, E2, E3, S2, M2, u1, u2, u3, d1, d2, d3, f2, b2, l2, r2;
  public static final int size = values().length;
  public static final SortedSet<TurnB> valueSet = Collections.unmodifiableSortedSet(new TreeSet<TurnB>(Arrays
      .asList(values())));

  public static TurnB turn(final int turnB) {
    return values()[turnB];
  }

  private static int[] toa;

  public Turn toA() {
    return Turn.turn(toA(ordinal()));
  }

  private static int toA(final int turn) {
    if (toa == null) {
      final int[] a = new int[size];
      for (final TurnB turnB : values())
        a[turnB.ordinal()] = Turn.valueOf(turnB.name()).ordinal();
      toa = a;
    }
    return toa[turn];
  }

  @Override
  public String toString() {
    return toA().toString();
  }
}
