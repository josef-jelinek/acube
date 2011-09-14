package acube.transform;

import java.util.EnumSet;
import acube.Corner;
import acube.Edge;
import acube.Reporter;

public final class Transform {
  private final Twist twist;
  private final Flip flip;
  private final CornerPos cornerPos;
  private final MEdgePos mEdgePos;
  private final UEdgePos uEdgePos;
  private final DEdgePos dEdgePos;
  public final TurnTable twistTable;
  public final TurnTable flipTable;
  public final TurnTable mEdgePosSetTable;
  public final TurnTable mEdgePosTable;
  public final TurnTable uEdgePosTable;
  public final TurnTable dEdgePosTable;
  public final TurnTable cornerPosTable;
  private final int[] mEdgePos_to_mEdgePosSet;

  public Transform(final EnumSet<Corner> cornerMask, final EnumSet<Edge> edgeMask,
      final EnumSet<Corner> knownTwistMask, final EnumSet<Edge> knownFlipMask, final int unknownTwisted,
      final int unknownFlipped, final Reporter reporter) {
    twist = new Twist(cornerMask, knownTwistMask, unknownTwisted);
    reporter.tableCreationStarted("transformation table (corner orientation) (" + twist.stateSize() + ")");
    twistTable = MoveKit.twist(twist);
    flip = new Flip(edgeMask, knownFlipMask, unknownFlipped);
    reporter.tableCreationStarted("transformation table (edge orientation) (" + flip.stateSize() + ")");
    flipTable = MoveKit.flip(flip);
    cornerPos = new CornerPos(cornerMask);
    reporter.tableCreationStarted("transformation table (corner position) (" + cornerPos.stateSize() + ")");
    cornerPosTable = MoveKit.cornerPos(cornerPos);
    mEdgePos = new MEdgePos(edgeMask);
    reporter.tableCreationStarted("transformation table (ring edge position) (" + mEdgePos.stateSize() + ")");
    mEdgePosTable = MoveKit.mEdgePos(mEdgePos);
    uEdgePos = new UEdgePos(edgeMask);
    reporter.tableCreationStarted("transformation table (U edge position) (" + uEdgePos.stateSize() + ")");
    uEdgePosTable = MoveKit.uEdgePos(uEdgePos);
    dEdgePos = new DEdgePos(edgeMask);
    reporter.tableCreationStarted("transformation table (D edge position) (" + dEdgePos.stateSize() + ")");
    dEdgePosTable = MoveKit.dEdgePos(dEdgePos);
    final MEdgePosSet mEdgePosSet = new MEdgePosSet(edgeMask);
    reporter.tableCreationStarted("transformation table (ring edge position set) (" + mEdgePosSet.stateSize() + ")");
    mEdgePosSetTable = MoveKit.mEdgePosSet(mEdgePosSet);
    reporter.tableCreationStarted("conversion table (ring edge position - middle edge position set)");
    mEdgePos_to_mEdgePosSet = MoveKit.get_mEdgePos_to_mEdgePosSet(mEdgePos, mEdgePosSet);
  }

  public Transform(final Reporter reporter) {
    this(Corner.valueSet, Edge.valueSet, Corner.valueSet, Edge.valueSet, 0, 0, reporter);
  }

  public int get_twist(final int[] twists) {
    twist.setup(twists);
    return twist.pack();
  }

  public int get_flip(final int[] flips) {
    flip.setup(flips);
    return flip.pack();
  }

  public int get_cornerPos(final Corner[] corners) {
    cornerPos.setup(corners);
    return cornerPos.pack();
  }

  public int get_mEdgePos(final Edge[] edges) {
    mEdgePos.setup(edges);
    return mEdgePos.pack();
  }

  public int get_uEdgePos(final Edge[] edges) {
    uEdgePos.setup(edges);
    return uEdgePos.pack();
  }

  public int get_dEdgePos(final Edge[] edges) {
    dEdgePos.setup(edges);
    return dEdgePos.pack();
  }

  public int get_mEdgePosSet(final Edge[] edges) {
    return convert_mEdgePos_to_mEdgePosSet(get_mEdgePos(edges));
  }

  private int convert_mEdgePos_to_mEdgePosSet(final int mep) {
    return mEdgePos_to_mEdgePosSet[mep];
  }

  public String cornerPosToString(final int cp) {
    cornerPos.unpack(cp);
    return cornerPos.toString();
  }

  public Object mEdgePosToString(final int mep) {
    mEdgePos.unpack(mep);
    return mEdgePos.toString();
  }

  public String uEdgePosToString(final int uep) {
    uEdgePos.unpack(uep);
    return uEdgePos.toString();
  }

  public String dEdgePosToString(final int dep) {
    dEdgePos.unpack(dep);
    return dEdgePos.toString();
  }
}
