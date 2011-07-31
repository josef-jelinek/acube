package acube;

import static java.lang.Math.max;
import acube.transform.Transform;
import acube.transform.TransformB;

public final class Solver {
  private static final int MAX_LENGTH = 50;

  static final class ANode {
    public int restDepth;
    public Turn[] turns;
    public int turnIndex;
    public Turn turn;
    public int allowedTurnsState;
    public int symmetry;
    public int cornerTwist;
    public int edgeFlip;
    public int mEdgePositionSet;
    public int cornerPosition;
    public int uEdgePosition;
    public int dEdgePosition;
    public int mEdgePosition;
    public int cornerTwistEdgeFlipDistance;
    public int cornerTwistMEdgePositionSetDistance;
    public int edgeFlipMEdgePositionSetDistance;
  }

  static final class BNode {
    public int restDepth;
    public TurnB[] turns;
    public int turn;
    public int allowedTurnsState;
    public int symmetry;
    public int cornerPosition;
    public int edgePosition;
    public int midgePerm;
  }

  private final CubeState state;
  private final Options options;
  private int minFoundLength;
  private final ANode[] stackA = new ANode[MAX_LENGTH];
  private final BNode[] stackB = new BNode[MAX_LENGTH];
  private int stackASize; // current size of the stack for Phase A
  private int stackBSize; // current size of the stack for Phase B
  private int maxBLength; // maximum allowed depth for Phase B
  // statistics
  private long sol2, sol2x;
  private long aprn, bprn, apry, bpry;

  public Solver(final CubeState state, final Options options) {
    this.state = state;
    this.options = options;
    for (int i = 0; i < stackA.length; i++)
      stackA[i] = new ANode();
    for (int i = 0; i < stackB.length; i++)
      stackB[i] = new BNode();
  }

  public void solve() {
    solveA();
  }

  private void solveA() {
    minFoundLength = MAX_LENGTH; // TODO: set from user
    initStatistics();
    final ANode node = stackA[0];
    node.cornerTwist = state.cornTwist;
    node.edgeFlip = state.edgeFlip;
    node.mEdgePositionSet = state.edgeLoc;
    node.cornerPosition = state.cornPerm;
    node.uEdgePosition = state.uEdgePos;
    node.dEdgePosition = state.dEdgePos;
    node.mEdgePosition = state.midgePos;
    final int ct_ef_d = state.prune.getCornerTwistEdgeFlipStartDistance(node.cornerTwist, node.edgeFlip);
    node.cornerTwistEdgeFlipDistance = ct_ef_d;
    final int ct_meps_d =
        state.prune.getCornerTwistMEdgePositionSetStartDistance(node.cornerTwist, node.mEdgePositionSet);
    node.cornerTwistMEdgePositionSetDistance = ct_meps_d;
    final int ef_meps_d = state.prune.getEdgeFlipMEdgePositionSetStartDistance(node.edgeFlip, node.mEdgePositionSet);
    node.edgeFlipMEdgePositionSetDistance = ef_meps_d;
    node.allowedTurnsState = 0;
    node.turns = state.turnList.getAvailableTurns(node.allowedTurnsState);
    node.turnIndex = 0;
    node.symmetry = state.symmetry;
    final int initialDepth = max(ct_ef_d, max(ct_meps_d, ef_meps_d));
    for (int d = initialDepth; options.findAll ? d <= minFoundLength : d < minFoundLength; d++) {
      node.restDepth = d;
      System.err.println("depth " + d + "...");
      if (options.findOptimal)
        searchAOptimal();
      else {
        maxBLength = options.findAll ? minFoundLength - d : minFoundLength - d - 1;
        searchA(0);
      }
    }
    reportStatistics();
  }

  private void initStatistics() {
    aprn = bprn = 0;
    apry = bpry = 0;
    sol2 = sol2x = 0;
  }

  private void reportStatistics() {
    if (options.findOptimal) {
      System.err.println("Done. (" + sol2 + " solutions found)");
      if (aprn > 0) {
        final long n = aprn < 10000 ? apry * 100 / aprn : apry / (aprn / 100);
        System.err.println(" (" + n + "% save - " + apry + " of " + aprn + " entries)");
      }
    } else {
      System.err.println("Done. (" + sol2 + " entries to Phase B of " + sol2x + " tries)");
      if (aprn > 0) {
        final long n = aprn < 10000 ? apry * 100 / aprn : apry / (aprn / 100);
        System.err.println(" (" + n + "% save in Phase A - " + apry + " of " + aprn + " entries)");
      }
      if (bprn > 0) {
        final long n = bprn < 10000 ? bpry * 100 / bprn : bpry / (bprn / 100);
        System.err.println(" (" + n + "% save in Phase B - " + bpry + " of " + bprn + " entries)");
      }
    }
  }

  private void searchAOptimal() {
//    final SymTransform symTransform = new SymTransform();
//    stackAn = 0;
//    stackA[0].restDepth = i; // the rest depth is the largest possible
//    stackA[1].turn = null;
//    while (stackAn >= 0)
//      if (stackA[stackAn].restDepth == 0) { // solution found ?
//        if (testA(stackA[stackAn])) {
//          display();
//          minLength = stackA[0].restDepth; // new minimum
//          sol2++;
//          if (!options.findAll)
//            return;
//        }
//        stackAn--;
//      } else {
//        final ANode s = stackA[stackAn];
//        final ANode ns = stackA[stackAn + 1];
//        Turn t = ns.turn + 1;
//        while (t < Turn.size()) {
//          final int tl = turnList.nextA[symTransform.turn(t, s.symmetry)][s.allowedTurnsState];
//          if (tl < 0) { // illegal turn
//            t++;
//            continue;
//          }
//          ns.restDepth = s.restDepth - metric.length(t); // decrease the rest depth
//          if (ns.restDepth < 0) {
//            t++;
//            continue;
//          }
//          ns.cornerTwist = transform.cornerTwist.turn(t, s.cornerTwist);
//          ns.edgeFlip = transform.edgeFlip.turn(t, s.edgeFlip);
//          ns.mEdgePositionSet = transform.mEdgePositionSet.turn(t, s.mEdgePositionSet);
//          ns.cornPerm = transform.cornerPosition.turn(t, s.cornPerm);
//          ns.uEdgePos = transform.uEdgePosition.turn(t, s.uEdgePos);
//          ns.dEdgePos = transform.dEdgePosition.turn(t, s.dEdgePos);
//          ns.midgePos = transform.mEdgePosition.turn(t, s.midgePos);
//          aprn++;
//          if (prune.over(ns.cornerTwist, ns.edgeFlip, ns.mEdgePositionSet, ns.cornPerm, ns.uEdgePos, ns.dEdgePos,
//              ns.midgePos, ns.restDepth)) {
//            apry++;
//            t++;
//            continue;
//          }
//          ns.turn = t; // save the last turn
//          ns.allowedTurnsState = tl;
//          ns.symmetry = symTransform.sym(t, s.symmetry);
//          break;
//        }
//        if (t == null)
//          stackAn--; // return to the previous depth
//        else {
//          stackAn++; // go to the next depth
//          stackA[stackAn + 1].turn = null;
//        }
//      }
  }

  void searchA(final int d) {
    if (d >= stackA.length)
      return;
    final ANode node = stackA[d];
    if (node.restDepth == 0) {
      stackASize = d;
      solveB();
      return;
    }
    final ANode nextNode = stackA[d + 1];
    for (node.turnIndex = 0; node.turnIndex < node.turns.length; node.turnIndex++) {
      node.turn = node.turns[node.turnIndex];
      final Turn cubeTurn = SymTransform.getTurn(node.turn, node.symmetry);
      nextNode.restDepth = node.restDepth - state.metric.length(cubeTurn);
      if (nextNode.restDepth >= 0) {
        final int ct = state.transform.cornerTwist.turn(cubeTurn, node.cornerTwist);
        final int ef = state.transform.edgeFlip.turn(cubeTurn, node.edgeFlip);
        final int ct_ef_d = state.prune.getCornerTwistEdgeFlipDistance(node.cornerTwistEdgeFlipDistance, ct, ef);
        if (ct_ef_d <= nextNode.restDepth) {
          final int meps = state.transform.mEdgePositionSet.turn(cubeTurn, node.mEdgePositionSet);
          final int ct_meps_d =
              state.prune.getCornerTwistMEdgePositionSetDistance(node.cornerTwistMEdgePositionSetDistance, ct, meps);
          if (ct_meps_d <= nextNode.restDepth) {
            final int ef_meps_d =
                state.prune.getEdgeFlipMEdgePositionSetDistance(node.edgeFlipMEdgePositionSetDistance, ef, meps);
            nextNode.cornerTwist = ct;
            nextNode.edgeFlip = ef;
            nextNode.mEdgePositionSet = meps;
            nextNode.cornerTwistEdgeFlipDistance = ct_ef_d;
            nextNode.cornerTwistMEdgePositionSetDistance = ct_meps_d;
            nextNode.edgeFlipMEdgePositionSetDistance = ef_meps_d;
            nextNode.symmetry = SymTransform.getSymmetry(cubeTurn, node.symmetry);
            nextNode.allowedTurnsState = state.turnList.getNextState(node.allowedTurnsState, node.turn);
            searchA(d + 1);
          }
        }
      }
    }
  }

  private void solveB() {
    sol2x++;
    int cp = stackA[0].cornerPosition;
    int mep = stackA[0].mEdgePosition;
    int uep = stackA[0].uEdgePosition;
    int dep = stackA[0].dEdgePosition;
    final Transform transform = state.transform;
    for (int i = 0; i < stackASize; i++) {
      final Turn turn = stackA[i].turn;
      cp = transform.cornerPosition.turn(turn, cp);
      mep = transform.mEdgePosition.turn(turn, mep);
      uep = transform.uEdgePosition.turn(turn, uep);
      dep = transform.dEdgePosition.turn(turn, dep);
    }
    final TransformB transformB = state.transformB;
    /* entry to the phase B need not be correct if midges are not completely
     * defined and some U or D edges are in the ring */
    if (!transformB.isMEdgePositionInB(mep) || !transformB.isUEdgePositionInB(uep) ||
        !transformB.isDEdgePositionInB(dep))
      return;
    sol2++;
    stackB[0].cornerPosition = cp;
//    stackB[0].edgePerm = transform.edgePos.udSToPerm(uEdgePos, dEdgePos);
//    stackB[0].midgePerm = transform.edgePos.midgeToPerm(midgePos);
//    final int maxBDepth = prune.distanceB(cornPerm, stackB[0].edgePerm, stackB[0].midgePerm);
//    bprn++;
//    if (maxBDepth > maxBLength) {
//      bpry++;
//      return;
//    }
//    stackB[0].turn = -2; // unused
//    stackB[0].allowedTurnsState = stackA[stackAn].allowedTurnsState;
//    stackB[0].symmetry = stackA[stackAn].symmetry;
//    // iterative deepening depth-first search for Phase B
//    for (int i = maxBDepth; i <= maxBLength; i++)
//      searchB(i);
  }

  private void searchB(final int i) {
//    stackBn = 0;
//    stackB[0].restDepth = i;
//    stackB[1].turn = null;
//    while (stackBn >= 0)
//      if (stackB[stackBn].restDepth == 0) {
//        if (testB(stackB[stackBn])) {
//          display();
//          minLength = stackA[0].restDepth + stackB[0].restDepth;
//          if (!options.findAll) {
//            maxBLength = stackB[0].restDepth - 1;
//            return;
//          }
//          maxBLength = stackB[0].restDepth;
//        }
//        stackBn--;
//      } else {
//        final BNode s = stackB[stackBn];
//        final BNode ns = stackB[stackBn + 1];
//        TurnB t = ns.turn + 1;
//        while (t < TurnB.N) {
//          final int tl = turnList.nextA[symTransform.turn(t.toA(), s.symmetry)][s.allowedTurnsState];
//          if (tl < 0) {
//            t++;
//            continue;
//          }
//          ns.restDepth = s.restDepth - metric.length(t.toA());
//          if (ns.restDepth < 0) {
//            t++;
//            continue;
//          }
//          ns.midgePerm = transform.midgePerm.turnB(t, s.midgePerm);
//          ns.cornPerm = transform.cornPerm.turnA(t.toA(), s.cornPerm);
//          ns.edgePerm = transform.udEdgePerm.turnB(t, s.edgePerm);
//          bprn++;
//          if (prune.overB(ns.cornPerm, ns.edgePerm, ns.midgePerm, ns.restDepth)) {
//            bpry++;
//            t++;
//            continue;
//          }
//          ns.turn = t;
//          ns.allowedTurnsState = tl;
//          ns.symmetry = symTransform.sym(t.toA(), s.symmetry);
//          break;
//        }
//        if (t == TurnB.N)
//          stackBn--;
//        else {
//          stackBn++;
//          stackB[stackBn + 1].turn = null;
//        }
//      }
  }

  private boolean testA(final ANode node) {
//    for (int i = 0, n = initMoves(state.freeMove); i < n; i++) {
//      int cornTwist = node.cornerTwist;
//      int edgeFlip = node.edgeFlip;
//      int edgeLoc = node.mEdgePositionSet;
//      int cornPerm = node.cornPerm;
//      int uEdgePos = node.uEdgePos;
//      int dEdgePos = node.dEdgePos;
//      int midgePos = node.midgePos;
//      for (int j = i, m = state.freeMove; m != 0; j /= 4, m /= 10)
//        if (j % 4 > 0) {
//          final int turn = initMoveGroup[m % 10][j % 4 - 1];
//          cornTwist = transform.cornTwist.turnA(turn, cornTwist);
//          edgeFlip = transform.edgeFlip.turnA(turn, edgeFlip);
//          edgeLoc = transform.edgeLoc.turnA(turn, edgeLoc);
//          cornPerm = transform.cornPerm.turnA(turn, cornPerm);
//          uEdgePos = transform.edgePos.turnAU(turn, uEdgePos);
//          dEdgePos = transform.edgePos.turnAD(turn, dEdgePos);
//          midgePos = transform.edgePos.turnAM(turn, midgePos);
//        }
//      if (prune.solvedA(cornTwist, edgeFlip, edgeLoc, cornPerm, uEdgePos, dEdgePos, midgePos))
//        return true;
//    }
    return false;
  }

  private boolean testB(final BNode node) {
//    for (int i = 0, n = initMoves(state.freeMove); i < n; i++) {
//      int midgePerm = node.midgePerm;
//      int cornPerm = node.cornPerm;
//      int edgePerm = node.edgePerm;
//      int m = state.freeMove;
//      for (int j = i; m != 0; j /= 4, m /= 10)
//        if (j % 4 > 0) {
//          final int turn = TurnB.fromA(initMoveGroup[m % 10][j % 4 - 1]);
//          if (turn < 0)
//            break;
//          cornPerm = transform.cornPerm.turnA(TurnB.toA(turn), cornPerm);
//          edgePerm = transform.udEdgePerm.turnB(turn, edgePerm);
//          midgePerm = transform.midgePerm.turnB(turn, midgePerm);
//        }
//      if (m == 0 && prune.solvedB(cornPerm, edgePerm, midgePerm))
//        return true;
//    }
    return false;
  }

  private void display() {
//    final SymTransform symTransform = new SymTransform();
//    int ql = 0;
//    int fl = 0;
//    int sl = 0;
//    int sql = 0;
//    for (int i = 1; i <= stackAn; i++) {
//      Turn t = stackA[i].turn;
//      final int s = stackA[i - 1].symmetry;
//      ql += Metric.QUARTER.length(t);
//      fl += Metric.FACE.length(t);
//      sl += Metric.SLICE.length(t);
//      sql += Metric.SLICE_QUARTER.length(t);
//      t = symTransform.turn(t, s);
//      System.out.print(t + " ");
//    }
//    // print out the lengths in different metrics
//    if (options.findOptimal)
//      switch (metric) {
//      case QUARTER:
//        System.out.println("(" + ql + "q*, " + fl + "f, " + sl + "s, " + sql + "sq)");
//        break;
//      case FACE:
//        System.out.println("(" + ql + "q, " + fl + "f*, " + sl + "s, " + sql + "sq)");
//        break;
//      case SLICE:
//        System.out.println("(" + ql + "q, " + fl + "f, " + sl + "s*, " + sql + "sq)");
//        break;
//      case SLICE_QUARTER:
//        System.out.println("(" + ql + "q, " + fl + "f, " + sl + "s, " + sql + "sq*)");
//        break;
//      default:
//        System.out.println("(" + ql + "q, " + fl + "f, " + sl + "s, " + sql + "sq)");
//      }
//    else {
//      System.out.print(". ");
//      for (int i = 1; i <= stackBn; i++) { // print out Phase B part
//        final TurnB t = stackB[i].turn;
//        final int s = stackB[i - 1].symmetry;
//        ql += Metric.QUARTER.length(t.toA());
//        fl += Metric.FACE.length(t.toA());
//        sl += Metric.SLICE.length(t.toA());
//        sql += Metric.SLICE_QUARTER.length(t.toA());
//        System.out.print(symTransform.turn(t.toA(), s) + " ");
//      }
//      System.out.println("(" + ql + "q, " + fl + "f, " + sl + "s, " + sql + "sq)");
//    }
  }
}
