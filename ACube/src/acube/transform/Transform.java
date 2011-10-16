package acube.transform;

import java.util.EnumSet;
import acube.Corner;
import acube.Edge;
import acube.NullReporter;
import acube.Reporter;
import acube.SymTransform;
import acube.Turn;

public final class Transform implements CubeSpaceProvider {
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

  public Transform() {
    this(new NullReporter());
  }

  public int mEdgePos_to_mEdgePosSet(final int mep) {
    return mEdgePos_to_mEdgePosSet[mep];
  }

  public EncodedCube encode(final int cubeSym, final Corner[] corners, final Edge[] edges, final int[] twists,
      final int[] flips) {
    twist.setup(twists);
    final int ct = twist.pack();
    flip.setup(flips);
    final int ef = flip.pack();
    cornerPos.setup(corners);
    final int cp = cornerPos.pack();
    mEdgePos.setup(edges);
    final int mep = mEdgePos.pack();
    uEdgePos.setup(edges);
    final int uep = uEdgePos.pack();
    dEdgePos.setup(edges);
    final int dep = dEdgePos.pack();
    return new EncodedCube(cubeSym, ct, ef, cp, mep, uep, dep);
  }

  @Override
  public CubeSpaceState startState(final int cubeSym) {
    return FullState.start(cubeSym, this);
  }

  private static final class FullState implements CubeSpaceState {
    private final Transform t;
    private final EncodedCube cube;

    public static CubeSpaceState start(final int cubeSym, final Transform t) {
      final int ct = t.twistTable.start(0);
      final int ef = t.flipTable.start(0);
      final int cp = t.cornerPosTable.start(0);
      final int mep = t.mEdgePosTable.start(0);
      final int uep = t.uEdgePosTable.start(0);
      final int dep = t.dEdgePosTable.start(0);
      return new FullState(new EncodedCube(cubeSym, ct, ef, cp, mep, uep, dep), t);
    }

    private FullState(final EncodedCube c, final Transform t) {
      cube = c;
      this.t = t;
    }

    @Override
    public CubeSpaceState turn(final Turn userTurn) {
      final Turn cubeTurn = SymTransform.getTurn(userTurn, cube.symmetry);
      final int cs = SymTransform.getSymmetry(cube.symmetry, userTurn);
      final int ct = t.twistTable.turn(cubeTurn, cube.twist);
      final int ef = t.flipTable.turn(cubeTurn, cube.flip);
      final int cp = t.cornerPosTable.turn(cubeTurn, cube.cornerPos);
      final int mep = t.mEdgePosTable.turn(cubeTurn, cube.mEdgePos);
      final int uep = t.uEdgePosTable.turn(cubeTurn, cube.uEdgePos);
      final int dep = t.dEdgePosTable.turn(cubeTurn, cube.dEdgePos);
      return new FullState(new EncodedCube(cs, ct, ef, cp, mep, uep, dep), t);
    }

    @Override
    public boolean equals(final Object other) {
      if (!(other instanceof FullState))
        return false;
      return cube.equals(((FullState)other).cube);
    }

    @Override
    public int hashCode() {
      return cube.hashCode();
    }
  }
}
