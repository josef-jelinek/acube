package acube.prune;

import static java.lang.Math.max;
import acube.Reporter;
import acube.Turn;
import acube.transform.MoveTable2in1;
import acube.transform.Transform;
import acube.transform.TurnTable;

public final class Prune {
  private static final int MAX_TABLE_SIZE = 100000000;
  private final PruneTable twist;
  private final PruneTable flip;
  private final PruneTable cornerPos;
  private final PruneTable mEdgePos;
  private final PruneTable uEdgePos;
  private final PruneTable dEdgePos;
  private final PruneTable twist_flip;
  private final PruneTable twist_cornerPos;
  private final PruneTable flip_cornerPos;
  private final MoveTable2in1 merged_twist_flip;
  private final MoveTable2in1 merged_twist_cornerPos;
  private final MoveTable2in1 merged_flip_cornerPos;

  public Prune(final Transform transform, final Reporter reporter) {
    reporter.tableCreationStarted("pruning table (corner twist)");
    twist = createPruneTable(transform.twistTable);
    reporter.tableCreationStarted("pruning table (edge flip)");
    flip = createPruneTable(transform.flipTable);
    reporter.tableCreationStarted("pruning table (corner position)");
    cornerPos = createPruneTable(transform.cornerPosTable);
    reporter.tableCreationStarted("pruning table (E edge position)");
    mEdgePos = createPruneTable(transform.mEdgePosTable);
    reporter.tableCreationStarted("pruning table (U edge position)");
    uEdgePos = createPruneTable(transform.uEdgePosTable);
    reporter.tableCreationStarted("pruning table (D edge position)");
    dEdgePos = createPruneTable(transform.dEdgePosTable);
    final boolean combine_ct_ef = twist.stateSize() * flip.stateSize() <= MAX_TABLE_SIZE;
    merged_twist_flip = combine_ct_ef ? new MoveTable2in1(transform.twistTable, transform.flipTable) : null;
    final boolean combine_ct_cp = twist.stateSize() * cornerPos.stateSize() <= MAX_TABLE_SIZE;
    merged_twist_cornerPos = combine_ct_cp ? new MoveTable2in1(transform.twistTable, transform.cornerPosTable) : null;
    final boolean combine_ef_cp = flip.stateSize() * cornerPos.stateSize() <= MAX_TABLE_SIZE;
    merged_flip_cornerPos = combine_ef_cp ? new MoveTable2in1(transform.flipTable, transform.cornerPosTable) : null;
    reporter.tableCreationStarted("pruning table (corner twist + edge flip)");
    twist_flip = createPruneTable(merged_twist_flip);
    reporter.tableCreationStarted("pruning table (corner twist + corner position)");
    twist_cornerPos = createPruneTable(merged_twist_cornerPos);
    reporter.tableCreationStarted("pruning table (edge flip + corner position)");
    flip_cornerPos = createPruneTable(merged_flip_cornerPos);
  }

  private static PruneTable createPruneTable(final TurnTable moveTable) {
    return moveTable == null ? null : new PruneTable(moveTable, Turn.essentialValueSet);
  }

  public int get_twist_startDist(final int ct) {
    return twist.startDist(ct);
  }

  public int get_twist_dist(final int lastDist, final int ct) {
    return twist.dist(lastDist, ct);
  }

  public int get_flip_startDist(final int ef) {
    return flip.startDist(ef);
  }

  public int get_flip_dist(final int lastDist, final int ef) {
    return flip.dist(lastDist, ef);
  }

  public int get_cornerPos_startDist(final int cp) {
    return cornerPos.startDist(cp);
  }

  public int get_cornerPos_dist(final int lastDist, final int cp) {
    return cornerPos.dist(lastDist, cp);
  }

  public int get_mEdgePos_startDist(final int mep) {
    return mEdgePos.startDist(mep);
  }

  public int get_mEdgePos_dist(final int lastDist, final int mep) {
    return mEdgePos.dist(lastDist, mep);
  }

  public int get_uEdgePos_startDist(final int uep) {
    return uEdgePos.startDist(uep);
  }

  public int get_uEdgePos_dist(final int lastDist, final int uep) {
    return uEdgePos.dist(lastDist, uep);
  }

  public int get_dEdgePos_startDist(final int dep) {
    return dEdgePos.startDist(dep);
  }

  public int get_dEdgePos_dist(final int lastDist, final int dep) {
    return dEdgePos.dist(lastDist, dep);
  }

  public int get_twist_flip_startDist(final int ct, final int ef) {
    return twist_flip == null ? 0 : twist_flip.startDist(merged_twist_flip.state(ct, ef));
  }

  public int get_twist_flip_dist(final int lastDist, final int ct, final int ef) {
    return twist_flip == null ? 0 : twist_flip.dist(lastDist, merged_twist_flip.state(ct, ef));
  }

  public int get_twist_cornerPos_startDist(final int ct, final int cp) {
    return twist_cornerPos == null ? 0 : twist_cornerPos.startDist(merged_twist_cornerPos.state(ct, cp));
  }

  public int get_twist_cornerPos_dist(final int lastDist, final int ct, final int cp) {
    return twist_cornerPos == null ? 0 : twist_cornerPos.dist(lastDist, merged_twist_cornerPos.state(ct, cp));
  }

  public int get_flip_cornerPos_startDist(final int ef, final int cp) {
    return flip_cornerPos == null ? 0 : flip_cornerPos.startDist(merged_flip_cornerPos.state(ef, cp));
  }

  public int get_flip_cornerPos_dist(final int lastDist, final int ef, final int cp) {
    return flip_cornerPos == null ? 0 : flip_cornerPos.dist(lastDist, merged_flip_cornerPos.state(ef, cp));
  }

  public int maxDist() {
    return max(
        max(max(max(maxD(twist), maxD(flip)), maxD(cornerPos)),
            max(maxD(mEdgePos), max(maxD(uEdgePos), maxD(dEdgePos)))),
        max(maxD(twist_flip), max(maxD(twist_cornerPos), maxD(flip_cornerPos))));
  }

  private static int maxD(final PruneTable table) {
    return table == null ? 0 : table.maxDist();
  }

  public int stateSize() {
    return states(twist) + states(flip) + states(cornerPos) + states(mEdgePos) + states(uEdgePos) + states(dEdgePos) +
        states(twist_flip) + states(twist_cornerPos) + states(flip_cornerPos);
  }

  private static int states(final PruneTable table) {
    return table == null ? 0 : table.stateSize();
  }

  public int memorySize() {
    return bytes(twist) + bytes(flip) + bytes(cornerPos) + bytes(mEdgePos) + bytes(uEdgePos) + bytes(dEdgePos) +
        bytes(twist_flip) + bytes(twist_cornerPos) + bytes(flip_cornerPos);
  }

  private static int bytes(final PruneTable table) {
    return table == null ? 0 : table.memorySize();
  }
}
