package acube.transform;

import java.util.EnumSet;
import acube.Corner;
import acube.Edge;
import acube.Reporter;

public final class TransformB {
  public final TurnTable mEdgePosTable;
  public final TurnTable udEdgePosTable;
  public final TurnTable cornerPosTable;
  private final boolean[] mEdgePos_inB;
  private final boolean[] uEdgePos_inB;
  private final boolean[] dEdgePos_inB;
  private final int[] uEdgePos_to_uEdgePosB;
  private final int[] dEdgePos_to_dEdgePosB;
  private final int[] mEdgePos_toB;
  private final int[][] uEdgePosB_dEdgePosB_to_udEdgePosB;

  public TransformB(final EnumSet<Corner> cornerMask, final EnumSet<Edge> edgeMask, final Reporter reporter) {
    final MEdgePosB mEdgePosB = new MEdgePosB(edgeMask);
    reporter.tableCreationStarted("transformation table (middle edge position B) (" + mEdgePosB.stateSize() + ")");
    mEdgePosTable = MoveKit.mEdgePosB(mEdgePosB);
    final UDEdgePosB udEdgePosB = new UDEdgePosB(edgeMask);
    reporter.tableCreationStarted("transformation table (U/D edge position B) (" + udEdgePosB.stateSize() + ")");
    udEdgePosTable = MoveKit.udEdgePosB(udEdgePosB);
    final CornerPos cornerPos = new CornerPos(cornerMask);
    reporter.tableCreationStarted("transformation table (corner position B) (" + cornerPos.stateSize() + ")");
    cornerPosTable = MoveKit.cornerPos(cornerPos);
    final MEdgePos mEdgePos = new MEdgePos(edgeMask);
    reporter.tableCreationStarted("conversion tables from phase A to phase B");
    mEdgePos_inB = MoveKit.get_mEdgePos_inB(mEdgePos);
    final UEdgePos uEdgePos = new UEdgePos(edgeMask);
    uEdgePos_inB = MoveKit.get_uEdgePos_inB(uEdgePos);
    final DEdgePos dEdgePos = new DEdgePos(edgeMask);
    dEdgePos_inB = MoveKit.get_dEdgePos_inB(dEdgePos);
    mEdgePos_toB = MoveKit.get_mEdgePos_to_mEdgePosB(mEdgePos, mEdgePosB);
    final UEdgePosB uEdgePosB = new UEdgePosB(edgeMask);
    uEdgePos_to_uEdgePosB = MoveKit.get_uEdgePos_to_uEdgePosB(uEdgePos, uEdgePosB);
    final DEdgePosB dEdgePosB = new DEdgePosB(edgeMask);
    dEdgePos_to_dEdgePosB = MoveKit.get_dEdgePos_to_dEdgePosB(dEdgePos, dEdgePosB);
    uEdgePosB_dEdgePosB_to_udEdgePosB = MoveKit.get_uEdgePosB_dEdgePosB_to_udEdgePosB(uEdgePosB, dEdgePosB, udEdgePosB);
  }

  public boolean is_mEdgePos_inB(final int mep) {
    return mEdgePos_inB[mep];
  }

  public boolean is_uEdgePos_inB(final int uep) {
    return uEdgePos_inB[uep];
  }

  public boolean is_dEdgePos_inB(final int dep) {
    return dEdgePos_inB[dep];
  }

  public int convertTo_mEdgePos(final int mep) {
    return mEdgePos_toB[mep];
  }

  public int convert_uEdgePos_dEdgePos_to_udEdgePosB(final int uep, final int dep) {
    return uEdgePosB_dEdgePosB_to_udEdgePosB[uEdgePos_to_uEdgePosB[uep]][dEdgePos_to_dEdgePosB[dep]];
  }
}
