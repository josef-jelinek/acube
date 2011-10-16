package acube;

import static java.lang.Math.max;
import static java.lang.Math.min;
import java.util.EnumSet;
import acube.prune.PruneA;
import acube.prune.PruneB;
import acube.transform.EncodedCube;
import acube.transform.Transform;
import acube.transform.TransformB;

public final class TwoPhaseSolver {
  static final class ANode {
    int length;
    Turn[] turns;
    int turnIndex;
    Turn userTurn;
    Turn cubeTurn;
    int turnListState;
    int symmetry;
    int twist;
    int flip;
    int mEdgePosSet;
    int twist_flip_dist;
    int twist_mEdgePosSet_dist;
    int flip_mEdgePosSet_dist;
    int twist_dist;
    int flip_dist;

    void setCubeStateA(final int ct, final int ef, final int meps) {
      twist = ct;
      flip = ef;
      mEdgePosSet = meps;
    }

    private void setTurnState(final int symmetry, final int turnListState, final TurnList turnList) {
      this.symmetry = symmetry;
      this.turnListState = turnListState;
      turns = turnList.getAvailableTurns(turnListState);
    }

    public void setTurnState(final int symmetry, final TurnList turnList) {
      setTurnState(symmetry, turnList.getInitialState(symmetry), turnList);
    }

    public void setTurnState(final ANode node, final Turn userTurn, final TurnList turnList) {
      final int symmetry = SymTransform.getSymmetry(node.symmetry, userTurn);
      final int allowedTurnsState = turnList.getNextState(node.turnListState, userTurn);
      setTurnState(symmetry, allowedTurnsState, turnList);
      turnIndex = 0;
    }

    void setDists(final int ct_ef_d, final int ct_meps_d, final int ef_meps_d, final int ctf_d, final int eff_d) {
      twist_flip_dist = ct_ef_d;
      twist_mEdgePosSet_dist = ct_meps_d;
      flip_mEdgePosSet_dist = ef_meps_d;
      twist_dist = ctf_d;
      flip_dist = eff_d;
    }

    int getMaxDist() {
      return max(max(max(twist_dist, flip_dist), twist_flip_dist), max(twist_mEdgePosSet_dist, flip_mEdgePosSet_dist));
    }
  }

  static final class BNode {
    int length;
    Turn[] turns;
    int turnIndex;
    Turn userTurn;
    Turn cubeTurn;
    int turnListState;
    int symmetry;
    int mEdgePos;
    int cornerPos;
    int udEdgePos;
    int mEdgePos_cornerPos_dist;
    int mEdgePos_udEdgePos_dist;

    public void setCubeState(final int mep, final int cp, final int udep) {
      mEdgePos = mep;
      cornerPos = cp;
      udEdgePos = udep;
    }

    public void setTurnState(final int symmetry, final Turn[] turns, final TurnList turnList) {
      this.symmetry = symmetry;
      turnListState = turnList.getInitialState(symmetry);
      this.turns = turns;
    }

    public void setTurnState(final BNode node, final Turn userTurn, final TurnList turnList) {
      final int symmetry = SymTransform.getSymmetry(node.symmetry, userTurn);
      final int turnsListState = turnList.getNextState(node.turnListState, userTurn);
      setTurnState(symmetry, turnsListState, turnList);
      turnIndex = 0;
    }

    private void setTurnState(final int symmetry, final int turnListState, final TurnList turnList) {
      this.symmetry = symmetry;
      this.turnListState = turnListState;
      turns = turnList.getAvailableTurns(turnListState);
    }

    public void setDists(final int mep_cp_d, final int mep_udep_d) {
      mEdgePos_cornerPos_dist = mep_cp_d;
      mEdgePos_udEdgePos_dist = mep_udep_d;
    }

    public int getMaxDist() {
      return max(mEdgePos_cornerPos_dist, mEdgePos_udEdgePos_dist);
    }
  }

  private static final int MAX_PHASE_STACK_SIZE = 50;
  private static final int CYCLES_BETWEEN_USER_CHECK = 100000;
  private final TurnList turnList;
  private final TurnList turnListB;
  private final Metric metric;
  private final Reporter reporter;
  private final ANode[] stackA = new ANode[MAX_PHASE_STACK_SIZE];
  private final BNode[] stackB = new BNode[MAX_PHASE_STACK_SIZE];
  private int stackASize;
  private int stackBSize;
  private boolean isInterrupted;
  private boolean findAll;
  private int maxSearchLength;
  private Transform transform;
  private TransformB transformB;
  private PruneA pruneA;
  private PruneB pruneB;
  private EncodedCube cube;
  // statistics
  private long aprn, bprn, apry, bpry;

  public TwoPhaseSolver(final EnumSet<Turn> turns, final Metric metric, final Reporter reporter) {
    this.metric = metric;
    this.reporter = reporter;
    reporter.tableCreationStarted("turn transformation and pruning table");
    turnList = new TurnList(turns, metric, TurnList.Phase.A);
    turnListB = new TurnList(turns, metric, TurnList.Phase.B);
    for (int i = 0; i < stackA.length; i++)
      stackA[i] = new ANode();
    for (int i = 0; i < stackB.length; i++)
      stackB[i] = new BNode();
  }

  public void setFindAll(final boolean enabled) {
    findAll = enabled;
  }

  public void setMaxSearchLength(final int maxLength) {
    maxSearchLength = min(maxLength, MAX_PHASE_STACK_SIZE);
  }

  public void
      setTables(final Transform transform, final TransformB transformB, final PruneA pruneA, final PruneB pruneB) {
    this.transform = transform;
    this.transformB = transformB;
    this.pruneA = pruneA;
    this.pruneB = pruneB;
  }

  public void solve(final EncodedCube cube) {
    this.cube = cube;
    isInterrupted = false;
    initStatistics();
    stackA[0].setCubeStateA(cube.twist, cube.flip, transform.mEdgePos_to_mEdgePosSet(cube.mEdgePos));
    stackA[0].setTurnState(cube.symmetry, turnList);
    final int ct_ef_d = pruneA.get_twist_flip_startDist(stackA[0].twist, stackA[0].flip);
    final int ct_meps_d = pruneA.get_twist_mEdgePosSet_startDist(stackA[0].twist, stackA[0].mEdgePosSet);
    final int ef_meps_d = pruneA.get_flip_mEdgePosSet_startDist(stackA[0].flip, stackA[0].mEdgePosSet);
    final int ct_d = pruneA.get_twist_startDist(stackA[0].twist);
    final int ef_d = pruneA.get_flip_startDist(stackA[0].flip);
    stackA[0].setDists(ct_ef_d, ct_meps_d, ef_meps_d, ct_d, ef_d);
    for (int maxALength = stackA[0].getMaxDist(); shouldContinueSearchingA(maxALength); maxALength++) {
      reporter.depthChanged(maxALength);
      searchA(maxALength);
    }
    reportStatistics();
  }

  private boolean shouldContinueSearchingA(final int maxALength) {
    return !isInterrupted && maxALength <= maxSearchLength;
  }

  private void initStatistics() {
    aprn = bprn = apry = bpry = 0;
  }

  private void reportStatistics() {
    reporter.twoPhaseStatistics(aprn, apry, bprn, bpry);
  }

  private void searchA(final int maxALength) {
    stackA[0].length = 0;
    stackA[0].turnIndex = 0;
    int d = 0;
    do {
      while (stackA[d].length < maxALength) {
        final ANode node = stackA[d];
        if (node.turnIndex >= node.turns.length) {
          if (--d < 0)
            return;
        } else {
          final Turn userTurn = node.turns[node.turnIndex++];
          node.userTurn = userTurn;
          final Turn cubeTurn = SymTransform.getTurn(userTurn, node.symmetry);
          node.cubeTurn = cubeTurn;
          final ANode nextNode = stackA[d + 1];
          final int length = node.length + metric.length(cubeTurn);
          if (length <= maxALength && length <= maxSearchLength) {
            final int ct = transform.twistTable.turn(cubeTurn, node.twist);
            final int ef = transform.flipTable.turn(cubeTurn, node.flip);
            aprn++;
            if (aprn % CYCLES_BETWEEN_USER_CHECK == 0 && (isInterrupted = reporter.shouldStop()))
              return;
            final int ct_ef_d = pruneA.get_twist_flip_dist(node.twist_flip_dist, ct, ef);
            if (ct_ef_d <= maxALength - length) {
              final int meps = transform.mEdgePosSetTable.turn(cubeTurn, node.mEdgePosSet);
              final int ct_meps_d = pruneA.get_twist_mEdgePosSet_dist(node.twist_mEdgePosSet_dist, ct, meps);
              if (ct_meps_d <= maxALength - length) {
                final int ef_meps_d = pruneA.get_flip_mEdgePosSet_dist(node.flip_mEdgePosSet_dist, ef, meps);
                if (ef_meps_d <= maxALength - length) {
                  final int ct_d = pruneA.get_twistFull_dist(node.twist_dist, ct);
                  final int ef_d = pruneA.get_flipFull_dist(node.flip_dist, ef);
                  if (ct_d <= maxALength - length && ef_d <= maxALength - length) {
                    nextNode.setCubeStateA(ct, ef, meps);
                    nextNode.setDists(ct_ef_d, ct_meps_d, ef_meps_d, ct_d, ef_d);
                    nextNode.setTurnState(node, userTurn, turnList);
                    nextNode.length = length;
                    d++;
                  } else
                    apry++;
                } else
                  apry++;
              } else
                apry++;
            } else
              apry++;
          }
        }
      }
      stackASize = d--;
      solveB(maxALength);
    } while (!isInterrupted && d >= 0);
  }

  private void solveB(final int maxALength) {
    int cp = cube.cornerPos;
    int mep = cube.mEdgePos;
    int uep = cube.uEdgePos;
    int dep = cube.dEdgePos;
    for (int i = 0; i < stackASize; i++) {
      final Turn turn = stackA[i].cubeTurn;
      cp = transform.cornerPosTable.turn(turn, cp);
      mep = transform.mEdgePosTable.turn(turn, mep);
      uep = transform.uEdgePosTable.turn(turn, uep);
      dep = transform.dEdgePosTable.turn(turn, dep);
    }
    /* entry to the phase B need not be correct if midges are not completely
     * defined and some U or D edges are in the ring */
    if (!transformB.is_mEdgePos_inB(mep) || !transformB.is_uEdgePos_inB(uep) || !transformB.is_dEdgePos_inB(dep))
      return;
    final int udepB = transformB.convert_uEdgePos_dEdgePos_to_udEdgePosB(uep, dep);
    final int mepB = transformB.convertTo_mEdgePos(mep);
    bprn++;
    final int mep_cp_d = pruneB.get_mEdgePos_cornerPos_startDist(mepB, cp);
    if (mep_cp_d > maxSearchLength - maxALength) {
      bpry++;
      return;
    }
    final int mep_udep_d = pruneB.get_mEdgePos_udEdgePos_startDist(mepB, udepB);
    if (mep_udep_d > maxSearchLength - maxALength) {
      bpry++;
      return;
    }
    stackB[0].setCubeState(mepB, cp, udepB);
    stackB[0].setTurnState(stackA[stackASize].symmetry, stackA[stackASize].turns, turnListB);
    stackB[0].setDists(mep_cp_d, mep_udep_d);
    for (int maxBLength = stackB[0].getMaxDist(); shouldContinueSearchingB(maxALength, maxBLength); maxBLength++)
      searchB(maxALength, maxBLength);
  }

  private boolean shouldContinueSearchingB(final int maxALength, final int maxBLength) {
    return !isInterrupted && maxALength + maxBLength <= maxSearchLength;
  }

  private void searchB(final int maxALength, final int maxBLength) {
    stackB[0].length = 0;
    stackB[0].turnIndex = 0;
    int d = 0;
    do {
      while (stackB[d].length < maxBLength) {
        final BNode node = stackB[d];
        if (node.turnIndex >= node.turns.length) {
          if (--d < 0)
            return;
        } else {
          final Turn userTurn = node.turns[node.turnIndex++];
          node.userTurn = userTurn;
          final Turn cubeTurn = SymTransform.getTurn(userTurn, node.symmetry);
          node.cubeTurn = cubeTurn;
          final int length = node.length + metric.length(userTurn);
          if (cubeTurn.isB() && maxALength + length <= maxSearchLength && length <= maxBLength) {
            final BNode nextNode = stackB[d + 1];
            final int mep = transformB.mEdgePosTable.turn(cubeTurn, node.mEdgePos);
            final int cp = transformB.cornerPosTable.turn(cubeTurn, node.cornerPos);
            bprn++;
            if (bprn % CYCLES_BETWEEN_USER_CHECK == 0 && (isInterrupted = reporter.shouldStop()))
              return;
            final int mep_cp_d = pruneB.get_mEdgePos_cornerPos_dist(node.mEdgePos_cornerPos_dist, mep, cp);
            if (mep_cp_d <= maxBLength - length) {
              final int udep = transformB.udEdgePosTable.turn(cubeTurn, node.udEdgePos);
              final int mep_udep_d = pruneB.get_mEdgePos_udEdgePos_dist(node.mEdgePos_udEdgePos_dist, mep, udep);
              if (mep_udep_d <= maxBLength - length) {
                nextNode.setCubeState(mep, cp, udep);
                nextNode.setDists(mep_cp_d, mep_udep_d);
                nextNode.setTurnState(node, userTurn, turnListB);
                nextNode.length = length;
                d++;
              } else
                bpry++;
            } else
              bpry++;
          }
        }
      }
      stackBSize = d--;
      display();
      maxSearchLength = maxALength + maxBLength - (findAll ? 0 : 1);
    } while (!isInterrupted && d >= 0);
  }

  private void display() {
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
