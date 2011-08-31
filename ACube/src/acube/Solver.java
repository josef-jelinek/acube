package acube;

import static java.lang.Math.max;
import static java.lang.Math.min;
import acube.transform.Transform;
import acube.transform.TransformB;

public final class Solver {
  static final class ANode {
    int restDist;
    Turn[] turns;
    int turnIndex;
    Turn userTurn;
    Turn cubeTurn;
    int turnListState;
    int symmetry;
    int twist;
    int flip;
    int mEdgePosSet;
    int cornerPos;
    int uEdgePos;
    int dEdgePos;
    int mEdgePos;
    int twist_flip_dist;
    int twist_mEdgePosSet_dist;
    int flip_mEdgePosSet_dist;

    void setCubeStateA(final int ct, final int ef, final int meps) {
      twist = ct;
      flip = ef;
      mEdgePosSet = meps;
    }

    void setCubeStateAB(final int cp, final int mep, final int uep, final int dep) {
      cornerPos = cp;
      mEdgePos = mep;
      uEdgePos = uep;
      dEdgePos = dep;
    }

    public void setTurnState(final int symmetry, final int turnListState, final TurnList turnList) {
      this.symmetry = symmetry;
      this.turnListState = turnListState;
      turns = turnList.getAvailableTurns(turnListState);
    }

    public void setTurnState(final ANode node, final Turn userTurn, final TurnList turnList) {
      final int symmetry = SymTransform.getSymmetry(node.symmetry, userTurn);
      final int allowedTurnsState = turnList.getNextState(node.turnListState, userTurn);
      setTurnState(symmetry, allowedTurnsState, turnList);
      turnIndex = 0;
    }

    void setDists(final int ct_ef_d, final int ct_meps_d, final int ef_meps_d) {
      twist_flip_dist = ct_ef_d;
      twist_mEdgePosSet_dist = ct_meps_d;
      flip_mEdgePosSet_dist = ef_meps_d;
    }

    int getMaxDist() {
      return max(twist_flip_dist, max(twist_mEdgePosSet_dist, flip_mEdgePosSet_dist));
    }
  }

  static final class BNode {
    int restDist;
    Turn[] turns;
    int turnIndex;
    Turn userTurn;
    Turn cubeTurn;
    int turnListState;
    int symmetry;
    int mEdgePos;
    int cornerPos;
    int oEdgePos;
    int mEdgePos_cornerPos_dist;
    int mEdgePos_oEdgePos_dist;

    public void setCubeState(final int mep, final int cp, final int oep) {
      mEdgePos = mep;
      cornerPos = cp;
      oEdgePos = oep;
    }

    public void setTurnState(final int symmetry, final int turnListState, final TurnList turnList) {
      this.symmetry = symmetry;
      this.turnListState = turnListState;
      turns = turnList.getAvailableTurns(turnListState);
    }

    public void setTurnState(final BNode node, final Turn userTurn, final TurnList turnList) {
      final int symmetry = SymTransform.getSymmetry(node.symmetry, userTurn);
      final int allowedTurnsState = turnList.getNextState(node.turnListState, userTurn);
      setTurnState(symmetry, allowedTurnsState, turnList);
      turnIndex = 0;
    }

    public void setDists(final int mep_cp_d, final int mep_oep_d) {
      mEdgePos_cornerPos_dist = mep_cp_d;
      mEdgePos_oEdgePos_dist = mep_oep_d;
    }

    public int getMaxDist() {
      return max(mEdgePos_cornerPos_dist, mEdgePos_oEdgePos_dist);
    }
  }

  private static final int MAX_LENGTH = 50;
  private CubeState state;
  private final boolean findAll;
  private final boolean findOptimal;
  private final Metric metric;
  private final Reporter reporter;
  private int minFoundLength;
  private final ANode[] stackA = new ANode[MAX_LENGTH];
  private final BNode[] stackB = new BNode[MAX_LENGTH];
  private int stackASize; // current size of the stack for Phase A
  private int stackBSize; // current size of the stack for Phase B
  private int maxBLength; // maximum allowed depth for Phase B
  private boolean isInterrupted;
  // statistics
  private long aprn, bprn, apry, bpry;

  public Solver(final boolean findAll, final boolean findOptimal, final Metric metric, final Reporter reporter) {
    this.findAll = findAll;
    this.findOptimal = findOptimal;
    this.metric = metric;
    this.reporter = reporter;
    for (int i = 0; i < stackA.length; i++)
      stackA[i] = new ANode();
    for (int i = 0; i < stackB.length; i++)
      stackB[i] = new BNode();
  }

  public void solve(final CubeState state, final int maxLength) {
    this.state = state;
    isInterrupted = false;
    solveA(maxLength);
  }

  private void solveA(final int maxLength) {
    minFoundLength = min(maxLength + (findAll ? 0 : 1), MAX_LENGTH);
    initStatistics();
    stackA[0].setCubeStateA(state.twist, state.flip, state.mEdgePosSet);
    stackA[0].setCubeStateAB(state.cornerPos, state.mEdgePos, state.uEdgePos, state.dEdgePos);
    stackA[0].setTurnState(state.symmetry, TurnList.INITIAL_STATE, state.turnList);
    final int ct_ef_d = state.prune.get_twist_flip_startDist(stackA[0].twist, stackA[0].flip);
    final int ct_meps_d = state.prune.get_twist_mEdgePosSet_startDist(stackA[0].twist, stackA[0].mEdgePosSet);
    final int ef_meps_d = state.prune.get_flip_mEdgePosSet_startDist(stackA[0].flip, stackA[0].mEdgePosSet);
    stackA[0].setDists(ct_ef_d, ct_meps_d, ef_meps_d);
    for (int d = stackA[0].getMaxDist(); shouldContinueSearchingA(d); d++) {
      stackA[0].restDist = d;
      stackA[0].turnIndex = 0;
      reporter.depthChanged(d);
      if (findOptimal)
        searchAOptimal();
      else {
        maxBLength = findAll ? minFoundLength - d : minFoundLength - d - 1;
        searchA();
      }
    }
    reportStatistics();
  }

  private boolean shouldContinueSearchingA(final int d) {
    return !isInterrupted && (findAll && d <= minFoundLength || d < minFoundLength);
  }

  private void initStatistics() {
    aprn = bprn = apry = bpry = 0;
  }

  private void reportStatistics() {
    if (findOptimal)
      reporter.onePhaseStatistics(aprn, apry);
    else
      reporter.twoPhaseStatistics(aprn, apry, bprn, bpry);
  }

  private boolean searchAOptimal() {
    // TODO copy searchA, factor out common code
    return false;
  }

  private void searchA() {
    int d = 0;
    do {
      while (stackA[d].restDist > 0) {
        final ANode node = stackA[d];
        if (node.turnIndex >= node.turns.length) {
          if (--d < 0)
            return;
        } else {
          final Turn userTurn = node.turns[node.turnIndex++];
          node.userTurn = userTurn;
          final Turn cubeTurn = SymTransform.getTurn(userTurn, node.symmetry);
          node.cubeTurn = cubeTurn;
          if (findAll || d + 1 < minFoundLength) {
            final ANode nextNode = stackA[d + 1];
            nextNode.restDist = node.restDist - metric.length(cubeTurn);
            if (nextNode.restDist >= 0) {
              final int ct = state.transform.twistTable.turn(cubeTurn, node.twist);
              final int ef = state.transform.flipTable.turn(cubeTurn, node.flip);
              aprn++;
              if (aprn % 10000 == 0 && (isInterrupted = reporter.shouldStop()))
                return;
              final int ct_ef_d = state.prune.get_twist_flip_dist(node.twist_flip_dist, ct, ef);
              if (ct_ef_d <= nextNode.restDist) {
                final int meps = state.transform.mEdgePosSetTable.turn(cubeTurn, node.mEdgePosSet);
                final int ct_meps_d = state.prune.get_twist_mEdgePosSet_dist(node.twist_mEdgePosSet_dist, ct, meps);
                if (ct_meps_d <= nextNode.restDist) {
                  final int ef_meps_d = state.prune.get_flip_mEdgePosSet_dist(node.flip_mEdgePosSet_dist, ef, meps);
                  if (ef_meps_d <= nextNode.restDist) {
                    nextNode.setCubeStateA(ct, ef, meps);
                    nextNode.setDists(ct_ef_d, ct_meps_d, ef_meps_d);
                    nextNode.setTurnState(node, userTurn, state.turnList);
                    d++;
                  } else
                    apry++;
                } else
                  apry++;
              } else
                apry++;
            }
          }
        }
      }
      stackASize = d--;
      solveB();
    } while (!isInterrupted && d >= 0);
  }

  private void solveB() {
    int cp = stackA[0].cornerPos;
    int mep = stackA[0].mEdgePos;
    int uep = stackA[0].uEdgePos;
    int dep = stackA[0].dEdgePos;
    final Transform transform = state.transform;
    for (int i = 0; i < stackASize; i++) {
      final Turn turn = stackA[i].cubeTurn;
      cp = transform.cornerPosTable.turn(turn, cp);
      mep = transform.mEdgePosTable.turn(turn, mep);
      uep = transform.uEdgePosTable.turn(turn, uep);
      dep = transform.dEdgePosTable.turn(turn, dep);
    }
    final TransformB transformB = state.transformB;
    /* entry to the phase B need not be correct if midges are not completely
     * defined and some U or D edges are in the ring */
    if (!transformB.is_mEdgePos_inB(mep) || !transformB.is_uEdgePos_inB(uep) || !transformB.is_dEdgePos_inB(dep))
      return;
    final int udepB = transformB.convert_uEdgePos_dEdgePos_to_udEdgePosB(uep, dep);
    final int mepB = transformB.convertTo_mEdgePos(mep);
    bprn++;
    final int mep_cp_d = state.pruneB.get_mEdgePos_cornerPos_startDist(mepB, cp);
    if (mep_cp_d > maxBLength) {
      bpry++;
      return;
    }
    final int mep_oep_d = state.pruneB.get_mEdgePos_oEdgePos_startDist(mepB, udepB);
    if (mep_oep_d > maxBLength) {
      bpry++;
      return;
    }
    stackB[0].setCubeState(mepB, cp, udepB);
    stackB[0].setTurnState(stackA[stackASize].symmetry, stackA[stackASize].turnListState, state.turnList);
    stackB[0].setDists(mep_cp_d, mep_oep_d);
    for (int d = stackB[0].getMaxDist(); shouldContinueSearchingB(d); d++) {
      stackB[0].restDist = d;
      stackB[0].turnIndex = 0;
      searchB();
    }
  }

  private boolean shouldContinueSearchingB(final int d) {
    return !isInterrupted && (findAll && d <= maxBLength || stackASize + d < minFoundLength);
  }

  private void searchB() {
    int d = 0;
    do {
      while (stackB[d].restDist > 0) {
        final BNode node = stackB[d];
        if (node.turnIndex >= node.turns.length) {
          if (--d < 0)
            return;
        } else {
          final Turn userTurn = node.turns[node.turnIndex++];
          node.userTurn = userTurn;
          final Turn cubeTurn = SymTransform.getTurn(userTurn, node.symmetry);
          node.cubeTurn = cubeTurn;
          if (cubeTurn.isB() && (findAll || stackASize + d + 1 < minFoundLength)) {
            final BNode nextNode = stackB[d + 1];
            nextNode.restDist = node.restDist - metric.length(cubeTurn);
            if (nextNode.restDist >= 0) {
              final int mep = state.transformB.mEdgePosTable.turn(cubeTurn, node.mEdgePos);
              final int cp = state.transformB.cornerPosTable.turn(cubeTurn, node.cornerPos);
              bprn++;
              if (bprn % 10000 == 0 && (isInterrupted = reporter.shouldStop()))
                return;
              final int mep_cp_d = state.pruneB.get_mEdgePos_cornerPos_dist(node.mEdgePos_cornerPos_dist, mep, cp);
              if (mep_cp_d <= nextNode.restDist) {
                final int oep = state.transformB.udEdgePosTable.turn(cubeTurn, node.oEdgePos);
                final int mep_oep_d = state.pruneB.get_mEdgePos_oEdgePos_dist(node.mEdgePos_oEdgePos_dist, mep, oep);
                if (mep_oep_d <= nextNode.restDist) {
                  nextNode.setCubeState(mep, cp, oep);
                  nextNode.setDists(mep_cp_d, mep_oep_d);
                  nextNode.setTurnState(node, userTurn, state.turnList);
                  d++;
                } else
                  bpry++;
              } else
                bpry++;
            }
          }
        }
      }
      stackBSize = d--;
      display();
    } while (!isInterrupted && d >= 0);
  }

  private void display() {
    minFoundLength = stackASize + stackBSize;
    final StringBuilder s = new StringBuilder();
    int ql = 0;
    int fl = 0;
    int sl = 0;
    int sql = 0;
    for (int i = 0; i < stackASize; i++) {
      final Turn t = stackA[i].userTurn;
      ql += Metric.QUARTER.length(t);
      fl += Metric.FACE.length(t);
      sl += Metric.SLICE.length(t);
      sql += Metric.SLICE_QUARTER.length(t);
      s.append(t.toString()).append(' ');
    }
    if (stackBSize > 0)
      s.append(". ");
    for (int i = 0; i < stackBSize; i++) { // print out Phase B part
      final Turn t = stackB[i].userTurn;
      ql += Metric.QUARTER.length(t);
      fl += Metric.FACE.length(t);
      sl += Metric.SLICE.length(t);
      sql += Metric.SLICE_QUARTER.length(t);
      s.append(t.toString()).append(' ');
    }
    reporter.sequenceFound(s.substring(0, max(s.length() - 1, 0)), ql, fl, sl, sql);
  }
}
