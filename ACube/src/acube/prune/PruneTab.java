package acube.prune;

import acube.transform.ToMove;
import acube.transform.ToTabMove;

public final class PruneTab {
  private final byte[] tab;
  private final int turnN;
  private final int stateN;
  private final ToMove move;
  private final int maxDist;

  public int distance(int depth, int state) {
    //  dist tab[state]
    // depth 0 1 2 3
    //     0 0 1 1x3x
    //     1 0 1 2 2x
    //     2 3 1 2 3x
    //     3 3 4 2 3x
    //     4 3 4 5 3x
    //     5 6 4 5 6x
    //     6 6 7 5 6x
    //        ...      x - invalid combination
    return depth - (depth - get(state) + 4) % 3 + 1;
    // (depth - get(state) + 4) / 3 * 3 + get(state) - 3;
  }

  public int startDistance(int state) {
    //    d tab[state]
    // last 0 1 2 3
    //    0 1 0 2 1
    //    1 2 1 0 2
    //    2 0 2 1 0
    int dist = 0;
    for (int turn = 0, last = get(state), tstate = state; turn < turnN; turn++) {
      int nstate = move.turn(turn, tstate);
      if ((last - get(nstate) + 4) % 3 == 2) { // 2->1 | 1->0 | 0->2
        dist++;
        last = (last + 2) % 3; // ... 2 1 0 2 1 0 ... sequence
        tstate = nstate; turn = -1; // restart loop for the new state
      }
    }
    return dist;
  }

  public int maxDistance() {
    return maxDist;
  }

  public int stateN() {
    return stateN;
  }

  private void put(int state, int val) {
    int i = state >> 2;
    int sh = state << 1 & 7;
    tab[i] = (byte)(tab[i] & ~(3 << sh) | val << sh);
  }

  private int get(int state) {
    return tab[state >> 2] >> (state << 1 & 7) & 3;
  }

  PruneTab(ToTabMove move) {
    this.move = move;
    turnN = move.turnN();
    tab = new byte[(move.stateN() + 3) / 4];
    for (int i = 0; i < tab.length; i++) tab[i] = -1;
    int stateN = 0;
    while (stateN < move.startN())
      put(move.start(stateN++), 0);
    log("  " + stateN + " (" + move.startN() + ")");
    int totalN = 0;
    int depth = 0;
    while (stateN > 0) {
      totalN += stateN;
      log(pad(depth, "   0") + pad(stateN, "|________") + pad(totalN, "|__________"));
      depth++;
      stateN = 0;
      for (int state = 0, last = (depth + 2) % 3; state < move.stateN(); state++) {
        if (get(state) == last) { // dist = -1 % 3
          for (int turn = 0; turn < turnN; turn++) {
            int nstate = move.turn(turn, state);
            if (get(nstate) == 3) { // free
              put(nstate, depth % 3);
              stateN++;
            }
          }
        }
      }
    }
    this.stateN = totalN;
    maxDist = depth - 1;
    log("  -> " + totalN + " / " + maxDist);
  }

  private static String pad(int num, String pad) {
    String s = "" + num;
    if (pad.length() < s.length()) return s;
    return pad.substring(0, pad.length() - s.length()) + s;
  }

  private static void log(String s) {
    // System.err.println(s);
  }
}
