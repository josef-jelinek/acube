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

  public String mEdgePosToString(final int mep) {
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

  @Override
  public CubeSpaceState startState(final int cubeSym) {
    return FullState.start(cubeSym, this);
  }

  private static final class FullState extends CubeSpaceState {
    private final Transform t;
    private final int twist;
    private final int flip;
    private final int cornerPos;
    private final int mEdgePos;
    private final int uEdgePos;
    private final int dEdgePos;

    public static CubeSpaceState start(final int cubeSym, final Transform t) {
      final int ct = t.twistTable.start(0);
      final int ef = t.flipTable.start(0);
      final int cp = t.cornerPosTable.start(0);
      final int mep = t.mEdgePosTable.start(0);
      final int uep = t.uEdgePosTable.start(0);
      final int dep = t.dEdgePosTable.start(0);
      return new FullState(cubeSym, ct, ef, cp, mep, uep, dep, t);
    }

    private FullState(final int cs, final int ct, final int ef, final int cp, final int mep, final int uep,
        final int dep, final Transform t) {
      super(cs);
      twist = ct;
      flip = ef;
      cornerPos = cp;
      mEdgePos = mep;
      uEdgePos = uep;
      dEdgePos = dep;
      this.t = t;
    }

    @Override
    public CubeSpaceState turn(final Turn userTurn) {
      final Turn cubeTurn = SymTransform.getTurn(userTurn, cubeSym);
      final int cs = SymTransform.getSymmetry(cubeSym, userTurn);
      final int ct = t.twistTable.turn(cubeTurn, twist);
      final int ef = t.flipTable.turn(cubeTurn, flip);
      final int cp = t.cornerPosTable.turn(cubeTurn, cornerPos);
      final int mep = t.mEdgePosTable.turn(cubeTurn, mEdgePos);
      final int uep = t.uEdgePosTable.turn(cubeTurn, uEdgePos);
      final int dep = t.dEdgePosTable.turn(cubeTurn, dEdgePos);
      return new FullState(cs, ct, ef, cp, mep, uep, dep, t);
    }

    @Override
    public boolean equals(final Object other) {
      if (!(other instanceof FullState))
        return false;
      final FullState o = (FullState)other;
      return cubeSym == o.cubeSym && twist == o.twist && flip == o.flip && cornerPos == o.cornerPos &&
          mEdgePos == o.mEdgePos && uEdgePos == o.uEdgePos && dEdgePos == o.dEdgePos;
    }

    @Override
    public int hashCode() {
      final int h = ((cubeSym * 113 + twist) * 113 + flip) * 113 + cornerPos;
      return ((h * 113 + mEdgePos) * 113 + uEdgePos) * 113 + dEdgePos;
    }
  }
}
