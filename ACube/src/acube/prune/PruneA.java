package acube.prune;

import static java.lang.Math.max;
import acube.Reporter;
import acube.Turn;
import acube.transform.MoveTable2in1;
import acube.transform.Transform;
import acube.transform.TurnTable;

public final class PruneA {
  private final PruneTable twist;
  private final PruneTable flip;
  private final PruneTable twist_flip;
  private final PruneTable twist_mEdgePosSet;
  private final PruneTable flip_mEdgePosSet;
  private final MoveTable2in1 moveTable_twist_flip;
  private final MoveTable2in1 moveTable_twist_mEdgePosSet;
  private final MoveTable2in1 moveTable_flip_mEdgePosSet;

  public PruneA(final Transform transform, final Reporter reporter) {
    reporter.tableCreationStarted("pruning table (corner twist)");
    twist = createPruneTable(transform.twistTable);
    reporter.tableCreationStarted("pruning table (edge flip)");
    flip = createPruneTable(transform.flipTable);
    final boolean combine_twist_flip = twist.stateSize() * flip.stateSize() <= 100000000;
    moveTable_twist_flip = combine_twist_flip ? new MoveTable2in1(transform.twistTable, transform.flipTable) : null;
    moveTable_twist_mEdgePosSet = new MoveTable2in1(transform.twistTable, transform.mEdgePosSetTable);
    moveTable_flip_mEdgePosSet = new MoveTable2in1(transform.flipTable, transform.mEdgePosSetTable);
    reporter.tableCreationStarted("pruning table (corner twist + edge flip)");
    twist_flip = createPruneTable(moveTable_twist_flip);
    reporter.tableCreationStarted("pruning table (corner twist + middle edge position set)");
    twist_mEdgePosSet = createPruneTable(moveTable_twist_mEdgePosSet);
    reporter.tableCreationStarted("pruning table (edge flip + middle edge position set)");
    flip_mEdgePosSet = createPruneTable(moveTable_flip_mEdgePosSet);
  }

  private static PruneTable createPruneTable(final TurnTable moveTable) {
    return moveTable == null ? null : new PruneTable(moveTable, Turn.essentialValueSet);
  }

  public int get_twist_flip_startDist(final int ct, final int ef) {
    return twist_flip == null ? 0 : twist_flip.startDist(moveTable_twist_flip.state(ct, ef));
  }

  public int get_twist_startDist(final int ctf) {
    return twist.startDist(ctf);
  }

  public int get_flip_startDist(final int eff) {
    return flip.startDist(eff);
  }

  public int get_twist_mEdgePosSet_startDist(final int ct, final int meps) {
    return twist_mEdgePosSet.startDist(moveTable_twist_mEdgePosSet.state(ct, meps));
  }

  public int get_flip_mEdgePosSet_startDist(final int ef, final int meps) {
    return flip_mEdgePosSet.startDist(moveTable_flip_mEdgePosSet.state(ef, meps));
  }

  public int get_twist_flip_dist(final int lastDist, final int ct, final int ef) {
    return twist_flip == null ? 0 : twist_flip.dist(lastDist, moveTable_twist_flip.state(ct, ef));
  }

  public int get_twistFull_dist(final int lastDist, final int ctf) {
    return twist.dist(lastDist, ctf);
  }

  public int get_flipFull_dist(final int lastDist, final int eff) {
    return flip.dist(lastDist, eff);
  }

  public int get_twist_mEdgePosSet_dist(final int lastDist, final int ct, final int meps) {
    return twist_mEdgePosSet.dist(lastDist, moveTable_twist_mEdgePosSet.state(ct, meps));
  }

  public int get_flip_mEdgePosSet_dist(final int lastDist, final int ef, final int meps) {
    return flip_mEdgePosSet.dist(lastDist, moveTable_flip_mEdgePosSet.state(ef, meps));
  }

  public int maxDist() {
    return max(max(max(twist.maxDist(), flip.maxDist()), twist_flip == null ? 0 : twist_flip.maxDist()),
        max(twist_mEdgePosSet.maxDist(), flip_mEdgePosSet.maxDist()));
  }

  public int stateSize() {
    return (twist_flip == null ? 0 : twist_flip.stateSize()) + twist_mEdgePosSet.stateSize() +
        flip_mEdgePosSet.stateSize() + twist.stateSize() + flip.stateSize();
  }

  public int memorySize() {
    return (twist_flip == null ? 0 : twist_flip.memorySize()) + twist_mEdgePosSet.memorySize() +
        flip_mEdgePosSet.memorySize() + twist.memorySize() + flip.memorySize();
  }
}
