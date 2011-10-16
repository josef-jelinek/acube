package acube;

import static java.lang.Math.max;
import static java.lang.Math.min;
import java.util.EnumSet;
import acube.prune.Prune;
import acube.transform.EncodedCube;
import acube.transform.Transform;

public final class OptimalSolver {
  static final class Node {
    int length;
    Turn[] turns;
    int turnIndex;
    Turn userTurn;
    Turn cubeTurn;
    int turnListState;
    int symmetry;
    int twist;
    int flip;
    int cornerPos;
    int mEdgePos;
    int uEdgePos;
    int dEdgePos;
    int twist_dist;
    int flip_dist;
    int cornerPos_dist;
    int mEdgePos_dist;
    int uEdgePos_dist;
    int dEdgePos_dist;
    int twist_flip_dist;
    int twist_cornerPos_dist;
    int flip_cornerPos_dist;

    void setCubeState(final int ct, final int ef, final int cp, final int mep, final int uep, final int dep) {
      twist = ct;
      flip = ef;
      cornerPos = cp;
      mEdgePos = mep;
      uEdgePos = uep;
      dEdgePos = dep;
    }

    private void setTurnState(final int symmetry, final int turnListState, final TurnList turnList) {
      this.symmetry = symmetry;
      this.turnListState = turnListState;
      turns = turnList.getAvailableTurns(turnListState);
    }

    public void setTurnState(final int symmetry, final TurnList turnList) {
      setTurnState(symmetry, turnList.getInitialState(symmetry), turnList);
    }

    public void setTurnState(final Node node, final Turn userTurn, final TurnList turnList) {
      final int symmetry = SymTransform.getSymmetry(node.symmetry, userTurn);
      final int allowedTurnsState = turnList.getNextState(node.turnListState, userTurn);
      setTurnState(symmetry, allowedTurnsState, turnList);
      turnIndex = 0;
    }

    void setDists(final int ct_d, final int ef_d, final int cp_d, final int mep_d, final int uep_d, final int dep_d,
        final int ct_ef_d, final int ct_cp_d, final int ef_cp_d) {
      twist_dist = ct_d;
      flip_dist = ef_d;
      cornerPos_dist = cp_d;
      mEdgePos_dist = mep_d;
      uEdgePos_dist = uep_d;
      dEdgePos_dist = dep_d;
      twist_flip_dist = ct_ef_d;
      twist_cornerPos_dist = ct_cp_d;
      flip_cornerPos_dist = ef_cp_d;
    }

    int getMaxDist() {
      return max(
          max(max(max(twist_dist, flip_dist), cornerPos_dist), max(mEdgePos_dist, max(uEdgePos_dist, dEdgePos_dist))),
          max(twist_flip_dist, max(twist_cornerPos_dist, flip_cornerPos_dist)));
    }
  }

  private static final int MAX_PHASE_STACK_SIZE = 50;
  private static final int CYCLES_BETWEEN_USER_CHECK = 100000;
  private final TurnList turnList;
  private final Metric metric;
  private final Reporter reporter;
  private final Node[] stack = new Node[MAX_PHASE_STACK_SIZE];
  private int stackSize;
  private boolean isInterrupted;
  private boolean findAll;
  private int maxSearchLength;
  private Transform transform;
  private Prune prune;
  // statistics
  private long prn, pry;

  public OptimalSolver(final EnumSet<Turn> turns, final Metric metric, final Reporter reporter) {
    this.metric = metric;
    this.reporter = reporter;
    reporter.tableCreationStarted("turn transformation and pruning table");
    turnList = new TurnList(turns, metric, TurnList.Phase.A);
    for (int i = 0; i < stack.length; i++)
      stack[i] = new Node();
  }

  public void setFindAll(final boolean enabled) {
    findAll = enabled;
  }

  public void setMaxSearchLength(final int maxLength) {
    maxSearchLength = min(maxLength, MAX_PHASE_STACK_SIZE);
  }

  public void setTables(final Transform transform, final Prune prune) {
    this.transform = transform;
    this.prune = prune;
  }

  public void solve(final EncodedCube cube) {
    isInterrupted = false;
    initStatistics();
    stack[0].setCubeState(cube.twist, cube.flip, cube.cornerPos, cube.mEdgePos, cube.uEdgePos, cube.dEdgePos);
    stack[0].setTurnState(cube.symmetry, turnList);
    final int ct_d = prune.get_twist_startDist(stack[0].twist);
    final int ef_d = prune.get_flip_startDist(stack[0].flip);
    final int cp_d = prune.get_cornerPos_startDist(stack[0].cornerPos);
    final int mep_d = prune.get_mEdgePos_startDist(stack[0].mEdgePos);
    final int uep_d = prune.get_uEdgePos_startDist(stack[0].uEdgePos);
    final int dep_d = prune.get_dEdgePos_startDist(stack[0].dEdgePos);
    final int ct_ef_d = prune.get_twist_flip_startDist(stack[0].twist, stack[0].flip);
    final int ct_cp_d = prune.get_twist_cornerPos_startDist(stack[0].twist, stack[0].cornerPos);
    final int ef_cp_d = prune.get_flip_cornerPos_startDist(stack[0].flip, stack[0].cornerPos);
    stack[0].setDists(ct_d, ef_d, cp_d, mep_d, uep_d, dep_d, ct_ef_d, ct_cp_d, ef_cp_d);
    solveAllDepths();
    reportStatistics();
  }

  private void solveAllDepths() {
    for (int maxLength = stack[0].getMaxDist(); shouldContinueSearching(maxLength); maxLength++) {
      reporter.depthChanged(maxLength);
      search(maxLength);
    }
  }

  private boolean shouldContinueSearching(final int maxLength) {
    return !isInterrupted && maxLength <= maxSearchLength;
  }

  private void initStatistics() {
    prn = pry = 0;
  }

  private void reportStatistics() {
    reporter.onePhaseStatistics(prn, pry);
  }

  private void search(final int maxLength) {
    stack[0].length = 0;
    stack[0].turnIndex = 0;
    int d = 0;
    do {
      while (stack[d].length < maxLength) {
        final Node node = stack[d];
        if (node.turnIndex >= node.turns.length) {
          if (--d < 0)
            return;
        } else {
          final Turn userTurn = node.turns[node.turnIndex++];
          node.userTurn = userTurn;
          final Turn cubeTurn = SymTransform.getTurn(userTurn, node.symmetry);
          node.cubeTurn = cubeTurn;
          final Node nextNode = stack[d + 1];
          final int length = node.length + metric.length(cubeTurn);
          if (length <= maxLength && length <= maxSearchLength) {
            final int ct = transform.twistTable.turn(cubeTurn, node.twist);
            final int ef = transform.flipTable.turn(cubeTurn, node.flip);
            prn++;
            if (prn % CYCLES_BETWEEN_USER_CHECK == 0 && (isInterrupted = reporter.shouldStop()))
              return;
            final int ct_ef_d = prune.get_twist_flip_dist(node.twist_flip_dist, ct, ef);
            if (ct_ef_d <= maxLength - length) {
              final int cp = transform.cornerPosTable.turn(cubeTurn, node.cornerPos);
              final int ct_cp_d = prune.get_twist_cornerPos_dist(node.twist_cornerPos_dist, ct, cp);
              final int ef_cp_d = prune.get_flip_cornerPos_dist(node.flip_cornerPos_dist, ef, cp);
              if (ct_cp_d <= maxLength - length && ef_cp_d <= maxLength - length) {
                final int cp_d = prune.get_cornerPos_dist(node.cornerPos_dist, cp);
                if (cp_d <= maxLength - length) {
                  final int mep = transform.mEdgePosTable.turn(cubeTurn, node.mEdgePos);
                  final int mep_d = prune.get_mEdgePos_dist(node.mEdgePos_dist, mep);
                  final int uep = transform.uEdgePosTable.turn(cubeTurn, node.uEdgePos);
                  final int uep_d = prune.get_uEdgePos_dist(node.uEdgePos_dist, uep);
                  final int dep = transform.dEdgePosTable.turn(cubeTurn, node.dEdgePos);
                  final int dep_d = prune.get_dEdgePos_dist(node.dEdgePos_dist, dep);
                  if (mep_d <= maxLength - length && uep_d <= maxLength - length && dep_d <= maxLength - length) {
                    final int ct_d = prune.get_twist_dist(node.twist_dist, ct);
                    final int ef_d = prune.get_flip_dist(node.flip_dist, ef);
                    if (ct_d <= maxLength - length && ef_d <= maxLength - length) {
                      nextNode.setCubeState(ct, ef, cp, mep, uep, dep);
                      nextNode.setDists(ct_d, ef_d, cp_d, mep_d, uep_d, dep_d, ct_ef_d, ct_cp_d, ef_cp_d);
                      nextNode.setTurnState(node, userTurn, turnList);
                      nextNode.length = length;
                      d++;
                    } else
                      pry++;
                  } else
                    pry++;
                } else
                  pry++;
              } else
                pry++;
            } else
              pry++;
          }
        }
      }
      stackSize = d--;
      display();
      maxSearchLength = maxLength - (findAll ? 0 : 1);
    } while (!isInterrupted && d >= 0);
  }

  private void display() {
    final StringBuilder s = new StringBuilder();
    int ql = 0;
    int fl = 0;
    int sl = 0;
    int sql = 0;
    for (int i = 0; i < stackSize; i++) {
      final Turn t = stack[i].userTurn;
      ql += Metric.QUARTER.length(t);
      fl += Metric.FACE.length(t);
      sl += Metric.SLICE.length(t);
      sql += Metric.SLICE_QUARTER.length(t);
      s.append(t.toString()).append(' ');
    }
    reporter.sequenceFound(s.substring(0, max(s.length() - 1, 0)), ql, fl, sl, sql);
  }
}
