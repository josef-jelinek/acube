package acube.pack;

public class PackPositionPartOrdered extends Pack {

  public PackPositionPartOrdered(int[] usedMask, int[] careMask) {
    super(CoderPart.ordered, CoderTools.maskIntersection(usedMask, careMask));
  }
}