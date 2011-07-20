package acube;

import acube.prune.Prune;
import acube.prune.PruneB;
import acube.transform.Transform;
import acube.transform.TransformB;

final class CubeState {
  public int coActive; // which corners are oriented
  public int eoActive; // which edges are oriented
  public int cActive; // which corners are placed
  public int eActive; // which edges are placed
  public int cornTwist; // corner orientations for phase A
  public int edgeFlip; // edge orientations for phase A
  public int edgeLoc; // unordered placements of the middle-layer edges for phase A
  public int cornPerm; // corner permutation for phase B
  public int midgePos; // placements of the middle edges for computing the middle edges permutation for phase B
  public int uEdgePos; // placements of U edges for UD edge permutation for phase B
  public int dEdgePos; // placements of D edges for UD edge permutation for phase B
  public int turnMask; // 30-bit mask of allowed turns (U F L D B R E S M  U2 F2 L2 D2 B2 R2 E2 S2 M2  u f l d b r  u2 f2 l2 d2 b2 r2)
  public int symmetry;
  public Transform transform;
  public TransformB transformB;
  public Prune prune;
  public PruneB pruneB;
  public Metric metric;
  public TurnList turnList;
}
