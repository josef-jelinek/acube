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
      throw new ParserError("Expected " + (corners.length + edges.length) + " pieces, got " + a.length);
    for (int i = 0; i < edges.length; i++) {
      edges[i] = getEdge(a[i]);
      flips[i] = getFlip(a[i]);
    }
    for (int i = 0; i < corners.length; i++) {
      corners[i] = getCorner(a[i + edges.length]);
      twists[i] = getTwist(a[i + edges.length]);
    }
    return new CubeState(corners, edges, twists, flips);
  }

  private static Corner getCorner(final String s) {
    return s.endsWith("?") ? null : Corner.corner(s.startsWith("@") ? s.substring(1) : s);
  }

  private static Edge getEdge(final String s) {
    return s.endsWith("?") ? null : Edge.edge(s.startsWith("@") ? s.substring(1) : s);
  }

  private static int getTwist(final String s) {
    return s.startsWith("@") ? -1 : s.equals("?") ? 0 : s.equals("+?") ? 1 : s.equals("-?") ? 2 : Corner.twist(s);
  }

  private static int getFlip(final String s) {
    return s.startsWith("@") ? -1 : s.equals("?") ? 0 : s.equals("-?") ? 1 : Edge.flip(s);
  }
}
