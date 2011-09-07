package acube.format;

import java.util.EnumSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import acube.Corner;
import acube.CubeState;
import acube.Edge;
import acube.Tools;

/** Supports a notation to directly write cycles and single piece orientations,
 * for instance "(UL,UR)(UFR,URB)" for the T-perm PLL or "URF+ URB-". You can
 * also mix cycles and orientations. The changes are executed from left to
 * right. To tell the program that you do not care about certain pieces, just
 * put the pieces inside brackets, for instance "[UL,UF,URB,UFR]". Information
 * about their locations is deleted, but the recorded orientations are kept.
 *
 * @author Stefan Pochmann (original cycle notation parser), Josef Jelinek */
public class CycleParser {
  private static final Pattern cyclePattern = Pattern.compile("\\((.*?)\\)");
  private static final Pattern ignorePattern = Pattern.compile("\\[(.*?)\\]");
  private static final Pattern orientationPattern = Pattern.compile("(\\w+)([-+?])");

  public static CubeState parse(final String source) {
    final Corner[] corners = Corner.values();
    final Edge[] edges = Edge.values();
    final int[] twists = new int[Corner.size];
    final int[] flips = new int[Edge.size];
    final EnumSet<Corner> cornersIgnored = EnumSet.noneOf(Corner.class);
    final EnumSet<Edge> edgesIgnored = EnumSet.noneOf(Edge.class);
    final Matcher cycleMatcher = cyclePattern.matcher(source);
    while (cycleMatcher.find()) {
      final String[] cubies = cycleMatcher.group(1).trim().split("[\\s,]+");
      processCycle(corners, edges, twists, flips, cubies);
    }
    final Matcher ignoreMatcher = ignorePattern.matcher(cycleMatcher.replaceAll(""));
    while (ignoreMatcher.find()) {
      final String[] cubies = ignoreMatcher.group(1).trim().split("[\\s,]+");
      cornersIgnored.addAll(getCorners(cubies));
      edgesIgnored.addAll(getEdges(cubies));
    }
    final Matcher orientationMatcher = orientationPattern.matcher(ignoreMatcher.replaceAll(""));
    while (orientationMatcher.find()) {
      final String sgn = orientationMatcher.group(2);
      if (sgn.equals("?"))
        processIgnoreOrientation(twists, flips, orientationMatcher.group(1));
      else
        processOrientation(twists, flips, orientationMatcher.group(1), sgn.equals("+") ? 1 : -1);
    }
    removeCorners(corners, cornersIgnored);
    removeEdges(edges, edgesIgnored);
    return new CubeState(corners, edges, twists, flips);
  }

  private static void processCycle(final Corner[] corners, final Edge[] edges, final int[] twists, final int[] flips,
      final String[] cubies) {
    if (cubies.length < 2)
      throw new ParserError("Too short cycle");
    if (Corner.exists(cubies[0]))
      for (int i = cubies.length - 1; i > 0; i--)
        updateCorners(corners, twists, cubies[i - 1], cubies[i]);
    else if (Edge.exists(cubies[0]))
      for (int i = cubies.length - 1; i > 0; i--)
        updateEdges(edges, flips, cubies[i - 1], cubies[i]);
    else
      throw new ParserError("Expected a corner or an edge: " + cubies[0]);
  }

  private static void updateCorners(final Corner[] corners, final int[] twists, final String a, final String b) {
    final int twist = Corner.twist(b) - Corner.twist(a);
    Tools.swap(corners, Corner.index(a), Corner.index(b));
    Tools.swap(twists, Corner.index(a), Corner.index(b));
    Tools.addMod(twists, Corner.index(a), twist, a.length());
    Tools.addMod(twists, Corner.index(b), -twist, b.length());
  }

  private static void updateEdges(final Edge[] edges, final int[] flips, final String a, final String b) {
    final int flip = Edge.flip(b) - Edge.flip(a);
    Tools.swap(edges, Edge.index(a), Edge.index(b));
    Tools.swap(flips, Edge.index(a), Edge.index(b));
    Tools.addMod(flips, Edge.index(a), flip, a.length());
    Tools.addMod(flips, Edge.index(b), -flip, b.length());
  }

  private static void processOrientation(final int[] twists, final int[] flips, final String cubie, final int d) {
    if (Corner.exists(cubie))
      Tools.addMod(twists, Corner.index(cubie), d, cubie.length());
    else if (Edge.exists(cubie))
      Tools.addMod(flips, Edge.index(cubie), d, cubie.length());
    else
      throw new ParserError("Expected a corner or an edge: " + cubie);
  }

  private static void processIgnoreOrientation(final int[] twists, final int[] flips, final String cubie) {
    if (Corner.exists(cubie))
      twists[Corner.index(cubie)] = -1;
    else if (Edge.exists(cubie))
      flips[Edge.index(cubie)] = -1;
    else
      throw new ParserError("Expected a corner or an edge: " + cubie);
  }

  private static EnumSet<Corner> getCorners(final String[] cubies) {
    final EnumSet<Corner> corners = EnumSet.noneOf(Corner.class);
    for (final String cubie : cubies)
      if (Corner.exists(cubie))
        corners.add(Corner.corner(cubie));
    return corners;
  }

  private static EnumSet<Edge> getEdges(final String[] cubies) {
    final EnumSet<Edge> edges = EnumSet.noneOf(Edge.class);
    for (final String cubie : cubies)
      if (Edge.exists(cubie))
        edges.add(Edge.edge(cubie));
    return edges;
  }

  private static void removeCorners(final Corner[] corners, final EnumSet<Corner> cornersToRemove) {
    for (int i = 0; i < corners.length; i++)
      if (cornersToRemove.contains(corners[i]))
        corners[i] = null;
  }

  private static void removeEdges(final Edge[] edges, final EnumSet<Edge> edgesToRemove) {
    for (int i = 0; i < edges.length; i++)
      if (edgesToRemove.contains(edges[i]))
        edges[i] = null;
  }
}
