package acube.format;

import acube.Corner;
import acube.CubeState;
import acube.Edge;

public class ReidParser {
  public static CubeState parse(final String s) {
    final Corner[] corners = Corner.values();
    final Edge[] edges = Edge.values();
    final int[] twists = new int[Corner.size];
    final int[] flips = new int[Edge.size];
    final String[] a = s.trim().split("[\\s,]+");
    if (a.length != corners.length + edges.length)
      throw new RuntimeException("Expected " + (corners.length + edges.length) + " pieces, got " + a.length);
    return new CubeState(corners, edges, twists, flips);
  }
}
