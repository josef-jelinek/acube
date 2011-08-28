package acube.transform;

import java.util.Set;
import acube.Corner;
import acube.Edge;
import acube.Reporter;
import acube.Turn;

public final class Transform {
  private final Set<Turn> turnMask;
  public final TurnTable twist;
  public final TurnTable flip;
  public final TurnTable mEdgePosSet;
  public final TurnTable mEdgePos;
  public final TurnTable uEdgePos;
  public final TurnTable dEdgePos;
  public final TurnTable cornerPos;
  private final int[] mEdgePos_to_mEdgePosSet;

  public Transform(final Set<Corner> cornerMask, final Set<Edge> edgeMask, final Set<Corner> twistMask,
      final Set<Edge> flipMask, final Set<Turn> turnMask, final Reporter reporter) {
    this.turnMask = turnMask;
    reporter.tableCreationStarted("transformation table (corner orientation)");
    twist = MoveKit.cornerTwist(cornerMask, twistMask, turnMask);
    reporter.tableCreationStarted("transformation table (edge orientation)");
    flip = MoveKit.edgeFlip(edgeMask, flipMask, turnMask);
    reporter.tableCreationStarted("transformation table (middle edge position set)");
    mEdgePosSet = MoveKit.mEdgePosSet(edgeMask, turnMask);
    reporter.tableCreationStarted("transformation table (middle edge position)");
    mEdgePos = MoveKit.mEdgePos(edgeMask, turnMask);
    reporter.tableCreationStarted("transformation table (U edge position)");
    uEdgePos = MoveKit.uEdgePos(edgeMask, turnMask);
    reporter.tableCreationStarted("transformation table (D edge position)");
    dEdgePos = MoveKit.dEdgePos(edgeMask, turnMask);
    reporter.tableCreationStarted("transformation table (corner position)");
    cornerPos = MoveKit.cornerPos(cornerMask, turnMask);
    reporter.tableCreationStarted("conversion table (middle edge position - middle edge position set)");
    mEdgePos_to_mEdgePosSet = MoveKit.get_mEdgePos_to_mEdgePosSet(edgeMask, turnMask);
  }

  public Transform(final Set<Turn> turnMask, final Reporter reporter) {
    this(Corner.valueSet, Edge.valueSet, Corner.valueSet, Edge.valueSet, turnMask, reporter);
  }

  public Set<Turn> turnMask() {
    return turnMask;
  }

  public int convert_mEdgePos_to_mEdgePosSet(final int mep) {
    return mEdgePos_to_mEdgePosSet[mep];
  }
}
