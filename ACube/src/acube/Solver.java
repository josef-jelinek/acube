package acube;

import static java.lang.Math.max;
import acube.transform.Transform;
import acube.transform.TransformB;

public final class Solver {
  private static final int MAX_LENGTH = 50;

  static final class ANode {
    int restDepth;
    Turn[] turns;
    Turn turn;
    Turn realTurn;
    int allowedTurnsState;
    int symmetry;
    int cornerTwist;
    int edgeFlip;
    int mEdgePosSet;
    int cornerPos;
    int uEdgePos;
    int dEdgePos;
    int mEdgePos;
    int cornerTwist_edgeFlip_distance;
    int cornerTwist_mEdgePosSet_distance;
    int edgeFlip_mEdgePosSet_distance;

    void setCubeStateA(final int ct, final int ef, final int meps) {
      cornerTwist = ct;
      edgeFlip = ef;
      mEdgePosSet = meps;
    }

    void setCubeStateAB(final int cp, final int mep, final int uep, final int dep) {
      cornerPos = cp;
      mEdgePos = mep;
      uEdgePos = uep;
      dEdgePos = dep;
    }

    public void setTurnState(final int symmetry, final TurnList turnList, final int turnListState) {
      this.symmetry = symmetry;
      allowedTurnsState = turnListState;
      turns = turnList.getAvailableTurns(turnListState);
    }

    void setDistances(final int ct_ef_d, final int ct_meps_d, final int ef_meps_d) {
      cornerTwist_edgeFlip_distance = ct_ef_d;
      cornerTwist_mEdgePosSet_distance = ct_meps_d;
      edgeFlip_mEdgePosSet_distance = ef_meps_d;
    }

    int getMaxDepth() {
      return max(cornerTwist_edgeFlip_distance, max(cornerTwist_mEdgePosSet_distance, edgeFlip_mEdgePosSet_distance));
    }
  }

  static final class BNode {
    int restDepth;
    TurnB[] turns;
    TurnB turn;
    Turn realTurn;
    int allowedTurnsState;
    int symmetry;
    int mEdgePos;
    int cornerPos;
    int oEdgePos;
    int mEdgePos_cornerPos_distance;
    int mEdgePos_oEdgePos_distance;

    void setCubeState(final int mep, final int cp, final int oep) {
      mEdgePos = mep;
      cornerPos = cp;
      oEdgePos = oep;
    }

    public void setTurnState(final int symmetry, final TurnList turnList, final int turnListState) {
      this.symmetry = symmetry;
      allowedTurnsState = turnListState;
      turns = turnList.getAvailableTurnsB(turnListState);
    }

    void setDistance(final int mep_cp_d, final int mep_oep_d) {
      mEdgePos_cornerPos_distance = mep_cp_d;
      mEdgePos_oEdgePos_distance = mep_oep_d;
    }

    int getMaxDepth() {
      return max(mEdgePos_cornerPos_distance, mEdgePos_oEdgePos_distance);
    }
  }

  private CubeState state;
  private final Options options;
  private final Reporter reporter;
  private int minFoundLength;
  private final ANode[] stackA = new ANode[MAX_LENGTH];
  private final BNode[] stackB = new BNode[MAX_LENGTH];
  private int stackASize; // current size of the stack for Phase A
  private int stackBSize; // current size of the stack for Phase B
  private int maxBLength; // maximum allowed depth for Phase B
  // statistics
  private long sol2, sol2x;
  private long aprn, bprn, apry, bpry;

  public Solver(final Options options, final Reporter reporter) {
    this.options = options;
    this.reporter = reporter;
    for (int i = 0; i < stackA.length; i++)
      stackA[i] = new ANode();
    for (int i = 0; i < stackB.length; i++)
      stackB[i] = new BNode();
  }

  public void solve(final CubeState state) {
    this.state = state;
    solveA();
  }

  private void solveA() {
    minFoundLength = MAX_LENGTH; // TODO: set from user
    initStatistics();
    final ANode node = stackA[0];
    node.setCubeStateA(state.twist, state.flip, state.mEdgePosSet);
    node.setCubeStateAB(state.cornerPos, state.mEdgePos, state.uEdgePos, state.dEdgePos);
    node.setTurnState(state.symmetry, state.turnList, TurnList.INITIAL_STATE);
    final int ct_ef_d = state.prune.get_cornerTwist_edgeFlip_startDistance(node.cornerTwist, node.edgeFlip);
    final int ct_meps_d = state.prune.get_cornerTwist_mEdgePosSet_startDistance(node.cornerTwist, node.mEdgePosSet);
    final int ef_meps_d = state.prune.get_edgeFlip_mEdgePosSet_startDistance(node.edgeFlip, node.mEdgePosSet);
    node.setDistances(ct_ef_d, ct_meps_d, ef_meps_d);
    int d = node.getMaxDepth();
    boolean found = false;
    while (!found && d <= minFoundLength) {
      node.restDepth = d;
      reporter.depthChanged(d);
      if (options.findOptimal)
        found = searchAOptimal();
      else {
        maxBLength = options.findAll ? minFoundLength - d : minFoundLength - d - 1;
        found = searchA(0);
      }
      d++;
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
      reporter.onePhaseStatistics(apry, aprn);
      System.out.println("Done. (" + sol2 + " solutions found)");
      if (aprn > 0) {
        final long n = aprn < 10000 ? apry * 100 / aprn : apry / (aprn / 100);
        System.out.println(" (" + n + "% save - " + apry + " of " + aprn + " entries)");
      }
    } else {
      reporter.twoPhaseStatistics(apry, aprn, bpry, bprn);
      System.out.println("Done. (" + sol2 + " entries to Phase B of " + sol2x + " tries)");
      if (aprn > 0) {
        final long n = aprn < 10000 ? apry * 100 / aprn : apry / (aprn / 100);
        System.out.println(" (" + n + "% save in Phase A - " + apry + " of " + aprn + " entries)");
      }
      if (bprn > 0) {
        final long n = bprn < 10000 ? bpry * 100 / bprn : bpry / (bprn / 100);
        System.out.println(" (" + n + "% save in Phase B - " + bpry + " of " + bprn + " entries)");
      }
    }
  }

  private boolean searchAOptimal() {
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
    return false;
  }

  private boolean searchA(final int d) {
    if (d >= stackA.length)
      return false;
    final ANode node = stackA[d];
    if (node.restDepth == 0) {
      stackASize = d;
      return solveB();
    }
    if (d + 1 >= stackA.length)
      return false;
    final ANode nextNode = stackA[d + 1];
    for (final Turn turn : node.turns) {
      node.turn = turn;
      final Turn cubeTurn = SymTransform.getTurn(turn, node.symmetry);
      node.realTurn = cubeTurn;
      nextNode.restDepth = node.restDepth - state.metric.length(cubeTurn);
      if (nextNode.restDepth >= 0) {
        final int ct = state.transform.twist.turn(cubeTurn, node.cornerTwist);
        final int ef = state.transform.edgeFlip.turn(cubeTurn, node.edgeFlip);
        final int ct_ef_d = state.prune.get_cornerTwist_edgeFlip_distance(node.cornerTwist_edgeFlip_distance, ct, ef);
        if (ct_ef_d <= nextNode.restDepth) {
          final int meps = state.transform.mEdgePosSet.turn(cubeTurn, node.mEdgePosSet);
          final int ct_meps_d =
              state.prune.get_cornerTwist_mEdgePosSet_distance(node.cornerTwist_mEdgePosSet_distance, ct, meps);
          if (ct_meps_d <= nextNode.restDepth) {
            final int ef_meps_d =
                state.prune.get_edgeFlip_mEdgePosSet_distance(node.edgeFlip_mEdgePosSet_distance, ef, meps);
            if (ef_meps_d <= nextNode.restDepth) {
              nextNode.setCubeStateA(ct, ef, meps);
              nextNode.setDistances(ct_ef_d, ct_meps_d, ef_meps_d);
              final int symmetry = SymTransform.getSymmetry(node.symmetry, cubeTurn);
              final int allowedTurnsState = state.turnList.getNextState(node.allowedTurnsState, node.turn);
              nextNode.setTurnState(symmetry, state.turnList, allowedTurnsState);
              if (searchA(d + 1))
                return true;
            }
          }
        }
      }
    }
    return false;
  }

  private boolean solveB() {
    sol2x++;
    int cp = stackA[0].cornerPos;
    int mep = stackA[0].mEdgePos;
    int uep = stackA[0].uEdgePos;
    int dep = stackA[0].dEdgePos;
    final Transform transform = state.transform;
    for (int i = 0; i < stackASize; i++) {
      final Turn turn = SymTransform.getTurn(stackA[i].turn, stackA[i].symmetry);
      cp = transform.cornerPos.turn(turn, cp);
      mep = transform.mEdgePos.turn(turn, mep);
      uep = transform.uEdgePos.turn(turn, uep);
      dep = transform.dEdgePos.turn(turn, dep);
    }
    final TransformB transformB = state.transformB;
    /* entry to the phase B need not be correct if midges are not completely
     * defined and some U or D edges are in the ring */
    if (!transformB.is_mEdgePos_inB(mep) || !transformB.is_uEdgePos_inB(uep) || !transformB.is_dEdgePos_inB(dep))
      return false;
    sol2++;
    final int oepB = transformB.convertTo_oEdgePos(uep, dep);
    final int mepB = transformB.convertTo_mEdgePos(mep);
    bprn++;
    final int mep_cp_d = state.pruneB.get_mEdgePos_cornerPos_startDistance(mepB, cp);
    if (mep_cp_d > maxBLength) {
      bpry++;
      return false;
    }
    final int mep_oep_d = state.pruneB.get_mEdgePos_oEdgePos_startDistance(mepB, oepB);
    if (mep_oep_d > maxBLength) {
      bpry++;
      return false;
    }
    final BNode node = stackB[0];
    node.setCubeState(mepB, cp, oepB);
    node.setTurnState(stackA[stackASize].symmetry, state.turnList, stackA[stackASize].allowedTurnsState);
    node.setDistance(mep_cp_d, mep_oep_d);
    int d = node.getMaxDepth();
    while (d <= maxBLength) {
      node.restDepth = d;
      if (searchB(0))
        return true;
      d++;
    }
    return false;
  }

  private boolean searchB(final int d) {
    if (d >= stackB.length)
      return false;
    final BNode node = stackB[d];
    if (node.restDepth == 0) {
      stackBSize = d;
      display();
      return true;
    }
    if (d + 1 >= stackB.length)
      return false;
    final BNode nextNode = stackB[d + 1];
    for (final TurnB turn : node.turns) {
      node.turn = turn;
      final Turn cubeTurn = SymTransform.getTurn(turn.toA(), node.symmetry);
      node.realTurn = cubeTurn;
      nextNode.restDepth = node.restDepth - state.metric.length(cubeTurn);
      if (nextNode.restDepth >= 0) {
        final int mep = state.transformB.mEdgePos.turn(cubeTurn, node.mEdgePos);
        final int cp = state.transformB.cornerPos.turn(cubeTurn, node.cornerPos);
        final int mep_cp_d = state.pruneB.get_mEdgePos_cornerPos_distance(node.mEdgePos_cornerPos_distance, mep, cp);
        if (mep_cp_d <= nextNode.restDepth) {
          final int oep = state.transformB.oEdgePos.turn(cubeTurn, node.oEdgePos);
          final int mep_oep_d = state.pruneB.get_mEdgePos_oEdgePos_distance(node.mEdgePos_oEdgePos_distance, mep, oep);
          if (mep_oep_d <= nextNode.restDepth) {
            nextNode.setCubeState(mep, cp, oep);
            nextNode.setDistance(mep_cp_d, mep_oep_d);
            final int symmetry = SymTransform.getSymmetry(node.symmetry, cubeTurn);
            final int allowedTurnsState = state.turnList.getNextState(node.allowedTurnsState, turn.toA());
            nextNode.setTurnState(symmetry, state.turnList, allowedTurnsState);
            if (searchB(d + 1))
              return true;
          }
        }
      }
    }
    return false;
  }

  private void display() {
    minFoundLength = stackASize + stackBSize;
    final StringBuilder s = new StringBuilder();
    int ql = 0;
    int fl = 0;
    int sl = 0;
    int sql = 0;
    for (int i = 0; i < stackASize; i++) {
      final Turn t = stackA[i].turn;
      ql += Metric.QUARTER.length(t);
      fl += Metric.FACE.length(t);
      sl += Metric.SLICE.length(t);
      sql += Metric.SLICE_QUARTER.length(t);
      final Turn rt = stackA[i].realTurn;
      s.append(t.toString()).append(':').append(rt).append(' ');
    }
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
    s.append(". ");
    for (int i = 0; i < stackBSize; i++) { // print out Phase B part
      final TurnB t = stackB[i].turn;
      ql += Metric.QUARTER.length(t.toA());
      fl += Metric.FACE.length(t.toA());
      sl += Metric.SLICE.length(t.toA());
      sql += Metric.SLICE_QUARTER.length(t.toA());
      final Turn rt = stackB[i].realTurn;
      s.append(t.toString()).append(':').append(rt).append(' ');
    }
    s.append("(" + ql + "q, " + fl + "f, " + sl + "s, " + sql + "sq)");
//    }
    reporter.sequenceFound(s.toString());
  }
}
