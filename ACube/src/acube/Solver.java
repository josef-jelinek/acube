package acube;

import static java.lang.Math.max;
import acube.transform.Transform;
import acube.transform.TransformB;

public final class Solver {
  private static final int MAX_LENGTH = 50;

  static final class ANode {
    int restDepth;
    Turn[] turns;
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

    public void setTurnState(final int symmetry, final TurnList turnList, final int turnListState) {
      this.symmetry = symmetry;
      this.turnListState = turnListState;
      turns = turnList.getAvailableTurns(turnListState);
    }

    void setDistances(final int ct_ef_d, final int ct_meps_d, final int ef_meps_d) {
      twist_flip_dist = ct_ef_d;
      twist_mEdgePosSet_dist = ct_meps_d;
      flip_mEdgePosSet_dist = ef_meps_d;
    }

    int getMaxDepth() {
      return max(twist_flip_dist, max(twist_mEdgePosSet_dist, flip_mEdgePosSet_dist));
    }
  }

  static final class BNode {
    int restDepth;
    Turn[] turns;
    Turn userTurn;
    Turn cubeTurn;
    int turnListState;
    int symmetry;
    int mEdgePos;
    int cornerPos;
    int oEdgePos;
    int mEdgePos_cornerPos_dist;
    int mEdgePos_oEdgePos_dist;

    void setCubeState(final int mep, final int cp, final int oep) {
      mEdgePos = mep;
      cornerPos = cp;
      oEdgePos = oep;
    }

    public void setTurnState(final int symmetry, final TurnList turnList, final int turnListState) {
      this.symmetry = symmetry;
      this.turnListState = turnListState;
      turns = turnList.getAvailableTurnsB(turnListState);
    }

    void setDistance(final int mep_cp_d, final int mep_oep_d) {
      mEdgePos_cornerPos_dist = mep_cp_d;
      mEdgePos_oEdgePos_dist = mep_oep_d;
    }

    int getMaxDepth() {
      return max(mEdgePos_cornerPos_dist, mEdgePos_oEdgePos_dist);
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
    final int ct_ef_d = state.prune.get_cornerTwist_edgeFlip_startDistance(node.twist, node.flip);
    final int ct_meps_d = state.prune.get_cornerTwist_mEdgePosSet_startDistance(node.twist, node.mEdgePosSet);
    final int ef_meps_d = state.prune.get_edgeFlip_mEdgePosSet_startDistance(node.flip, node.mEdgePosSet);
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
    aprn = bprn = apry = bpry = 0;
  }

  private void reportStatistics() {
    if (options.findOptimal)
      reporter.onePhaseStatistics(aprn, apry);
    else
      reporter.twoPhaseStatistics(aprn, apry, bprn, bpry);
  }

  private boolean searchAOptimal() {
    // TODO copy searchA, factor out common code
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
    for (final Turn userTurn : node.turns) {
      node.userTurn = userTurn;
      final Turn cubeTurn = SymTransform.getTurn(userTurn, node.symmetry);
      node.cubeTurn = cubeTurn;
      nextNode.restDepth = node.restDepth - state.metric.length(cubeTurn);
      if (nextNode.restDepth >= 0) {
        final int ct = state.transform.twist.turn(cubeTurn, node.twist);
        final int ef = state.transform.flip.turn(cubeTurn, node.flip);
        aprn++;
        final int ct_ef_d = state.prune.get_cornerTwist_edgeFlip_distance(node.twist_flip_dist, ct, ef);
        if (ct_ef_d <= nextNode.restDepth) {
          final int meps = state.transform.mEdgePosSet.turn(cubeTurn, node.mEdgePosSet);
          final int ct_meps_d = state.prune.get_twist_mEdgePosSet_dist(node.twist_mEdgePosSet_dist, ct, meps);
          if (ct_meps_d <= nextNode.restDepth) {
            final int ef_meps_d = state.prune.get_flip_mEdgePosSet_dist(node.flip_mEdgePosSet_dist, ef, meps);
            if (ef_meps_d <= nextNode.restDepth) {
              nextNode.setCubeStateA(ct, ef, meps);
              nextNode.setDistances(ct_ef_d, ct_meps_d, ef_meps_d);
              final int symmetry = SymTransform.getSymmetry(node.symmetry, userTurn);
              final int allowedTurnsState = state.turnList.getNextState(node.turnListState, node.userTurn);
              nextNode.setTurnState(symmetry, state.turnList, allowedTurnsState);
              if (searchA(d + 1))
                return true;
            } else
              apry++;
          } else
            apry++;
        } else
          apry++;
      }
    }
    return false;
  }

  private boolean solveB() {
    int cp = stackA[0].cornerPos;
    int mep = stackA[0].mEdgePos;
    int uep = stackA[0].uEdgePos;
    int dep = stackA[0].dEdgePos;
    final Transform transform = state.transform;
    for (int i = 0; i < stackASize; i++) {
      final Turn turn = stackA[i].cubeTurn;
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
    final int oepB = transformB.convertTo_oEdgePos(uep, dep);
    final int mepB = transformB.convertTo_mEdgePos(mep);
    bprn++;
    final int mep_cp_d = state.pruneB.get_mEdgePos_cornerPos_startDist(mepB, cp);
    if (mep_cp_d > maxBLength) {
      bpry++;
      return false;
    }
    final int mep_oep_d = state.pruneB.get_mEdgePos_oEdgePos_startDist(mepB, oepB);
    if (mep_oep_d > maxBLength) {
      bpry++;
      return false;
    }
    final BNode node = stackB[0];
    node.setCubeState(mepB, cp, oepB);
    node.setTurnState(stackA[stackASize].symmetry, state.turnList, stackA[stackASize].turnListState);
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
    for (final Turn userTurn : node.turns) {
      node.userTurn = userTurn;
      final Turn cubeTurn = SymTransform.getTurn(userTurn, node.symmetry);
      node.cubeTurn = cubeTurn;
      nextNode.restDepth = node.restDepth - state.metric.length(cubeTurn);
      if (nextNode.restDepth >= 0) {
        final int mep = state.transformB.mEdgePos.turn(cubeTurn, node.mEdgePos);
        final int cp = state.transformB.cornerPos.turn(cubeTurn, node.cornerPos);
        bprn++;
        final int mep_cp_d = state.pruneB.get_mEdgePos_cornerPos_dist(node.mEdgePos_cornerPos_dist, mep, cp);
        if (mep_cp_d <= nextNode.restDepth) {
          final int oep = state.transformB.oEdgePos.turn(cubeTurn, node.oEdgePos);
          final int mep_oep_d = state.pruneB.get_mEdgePos_oEdgePos_dist(node.mEdgePos_oEdgePos_dist, mep, oep);
          if (mep_oep_d <= nextNode.restDepth) {
            nextNode.setCubeState(mep, cp, oep);
            nextNode.setDistance(mep_cp_d, mep_oep_d);
            final int symmetry = SymTransform.getSymmetry(node.symmetry, userTurn);
            final int allowedTurnsState = state.turnList.getNextState(node.turnListState, userTurn);
            nextNode.setTurnState(symmetry, state.turnList, allowedTurnsState);
            if (searchB(d + 1))
              return true;
          } else
            bpry++;
        } else
          bpry++;
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
