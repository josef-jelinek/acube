package acube;

public enum TurnB {

  U1, U2, U3, D1, D2, D3, F2, B2, L2, R2,
  E1, E2, E3, S2, M2,
  u1, u2, u3, d1, d2, d3, f2, b2, l2, r2;

  public static TurnB turn(int turnB) {
    return values()[turnB];
  }

  private static int[] toa;

  public static int toA(int turn) {
    if (toa == null) {
      int[] a = new int[size()];
      for (TurnB turnB : values())
        a[turnB.ordinal()] = Turn.valueOf(turnB.name()).ordinal();
      toa = a;
    }
    return toa[turn];
  }

  public Turn toA() {
    return Turn.turn(toA(ordinal()));
  }

  public static int size() {
    return values().length;
  }

  @Override
  public String toString() {
    return toA().toString();
  }
}
