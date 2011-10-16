package acube.transform;

public final class EncodedCube {
  public final int symmetry;
  public final int twist;
  public final int flip;
  public final int cornerPos;
  public final int mEdgePos;
  public final int uEdgePos;
  public final int dEdgePos;

  public EncodedCube(final int cs, final int ct, final int ef, final int cp, final int mep, final int uep, final int dep) {
    symmetry = cs;
    twist = ct;
    flip = ef;
    cornerPos = cp;
    mEdgePos = mep;
    uEdgePos = uep;
    dEdgePos = dep;
  }

  @Override
  public boolean equals(final Object other) {
    if (!(other instanceof EncodedCube))
      return false;
    final EncodedCube o = (EncodedCube)other;
    return symmetry == o.symmetry && twist == o.twist && flip == o.flip && cornerPos == o.cornerPos && mEdgePos == o.mEdgePos &&
        uEdgePos == o.uEdgePos && dEdgePos == o.dEdgePos;
  }

  @Override
  public int hashCode() {
    final int h = ((symmetry * 113 + twist) * 113 + flip) * 113 + cornerPos;
    return ((h * 113 + mEdgePos) * 113 + uEdgePos) * 113 + dEdgePos;
  }
}
