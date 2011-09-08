package acube.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import acube.Corner;
import acube.CubeState;
import acube.Edge;

public class ReidParser {
  private static Pattern cornerPattern = Pattern.compile("^[+-@]?\\?|@?([UDFBLR]{3})$");
  private static Pattern edgePattern = Pattern.compile("^[+-@]?\\?|@?([UDFBLR]{2})$");

  public static CubeState parse(final String s) {
    final Corner[] corners = Corner.values();
    final Edge[] edges = Edge.values();
    final int[] twists = new int[Corner.size];
    final int[] flips = new int[Edge.size];
    final String[] a = s.trim().toUpperCase().split("[\\s,]+");
    if (a.length != corners.length + edges.length)
      throw new ParserError("Expected " + (corners.length + edges.length) + " pieces, got " + a.length);
    for (int i = 0; i < edges.length; i++) {
      checkIfValidEdge(a[i]);
      edges[i] = extractEdge(a[i]);
      flips[i] = extractFlip(a[i]);
    }
    for (int i = 0; i < corners.length; i++) {
      checkIfValidCorner(a[i + edges.length]);
      corners[i] = extractCorner(a[i + edges.length]);
      twists[i] = extractTwist(a[i + edges.length]);
    }
    return new CubeState(corners, edges, twists, flips);
  }

  private static void checkIfValidCorner(final String s) {
    final Matcher m = cornerPattern.matcher(s);
    if (!m.matches() || m.group(1) != null && !Corner.exists(m.group(1)))
      throw new ParserError("Invalid corner cubie: " + s);
  }

  private static void checkIfValidEdge(final String s) {
    final Matcher m = edgePattern.matcher(s);
    if (!m.matches() || m.group(1) != null && !Edge.exists(m.group(1)))
      throw new ParserError("Invalid edge cubie: " + s);
  }

  private static Corner extractCorner(final String s) {
    return s.endsWith("?") ? null : Corner.corner(s.startsWith("@") ? s.substring(1) : s);
  }

  private static Edge extractEdge(final String s) {
    return s.endsWith("?") ? null : Edge.edge(s.startsWith("@") ? s.substring(1) : s);
  }

  private static int extractTwist(final String s) {
    return s.startsWith("@") ? -1 : s.equals("?") ? 0 : s.equals("+?") ? 1 : s.equals("-?") ? 2 : Corner.twist(s);
  }

  private static int extractFlip(final String s) {
    return s.startsWith("@") ? -1 : s.equals("?") ? 0 : s.endsWith("?") ? 1 : Edge.flip(s);
  }
}
