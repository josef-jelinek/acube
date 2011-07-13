package acube;

import java.util.Arrays;

import acube.data.NodeSymbol;
import java.util.ArrayList;
import java.util.List;

public enum Turn {

  U1, U2, U3, D1, D2, D3, F1, F2, F3, B1, B2, B3, L1, L2, L3, R1, R2, R3,
  E1, E2, E3, S1, S2, S3, M1, M2, M3,
  u1, u2, u3, d1, d2, d3, f1, f2, f3, b1, b2, b3, l1, l2, l3, r1, r2, r3;

  private static String[] names = "U U2 U' D D2 D' F F2 F' B B2 B' L L2 L' R R2 R' E E2 E' S S2 S' M M2 M' u u2 u' d d2 d' f f2 f' b b2 b' l l2 l' r r2 r'".split(" ");

  public static Turn turn(int turn) {
    return values()[turn];
  }

  private static int[] tob;

  public static int toB(int turn) {
    if (tob == null) {
      int[] b = new int[size()];
      Arrays.fill(b, -1);
      for (TurnB turnB : TurnB.values())
        b[Turn.valueOf(turnB.name()).ordinal()] = turnB.ordinal();
      tob = b;
    }
    return tob[turn];
  }

  public static Turn[] getValidB(Turn[] turns) {
    List<Turn> listB = new ArrayList<Turn>();
    for (Turn turn : turns)
      if (turn.isB())
        listB.add(turn);
    return listB.toArray(new Turn[listB.size()]);
  }

  public TurnB toB() {
    return TurnB.turn(toB(ordinal()));
  }

  public boolean isB() {
    return toB(ordinal()) >= 0;
  }
  
  public static int size() {
    return values().length;
  }

  public NodeSymbol symbol() {
    return NodeSymbol.create(names[ordinal()]);
  }

  @Override
  public String toString() {
    return names[ordinal()];
  }
}
