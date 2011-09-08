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
  private static final Pattern twistPattern = Pattern.compile("\\b([UDFBLR]{3})([-+?])");
  private static final Pattern flipPattern = Pattern.compile("\\b([UDFBLR]{2})([-+?])");

  public static CubeState parse(final String source) {
    final Corner[] corners = Corner.values();
    final Edge[] edges = Edge.values();
    final int[] twists = new int[Corner.size];
    final int[] flips = new int[Edge.size];
    final EnumSet<Corner> cornersIgnored = EnumSet.noneOf(Corner.class);
    final EnumSet<Edge> edgesIgnored = EnumSet.noneOf(Edge.class);
    final Matcher cycleMatcher = cyclePattern.matcher(source);
    while (cycleMatcher.find())
      processCycle(corners, edges, twists, flips, cycleMatcher.group(1).trim().toUpperCase().split("[\\s,]+"));
    final Matcher ignoreMatcher = ignorePattern.matcher(cycleMatcher.replaceAll(""));
    while (ignoreMatcher.find())
      processIgnoredPos(cornersIgnored, edgesIgnored, ignoreMatcher.group(1).trim().toUpperCase().split("[\\s,]+"));
    final String orientationString = ignoreMatcher.replaceAll("");
    final Matcher twistMatcher = twistPattern.matcher(orientationString);
    while (twistMatcher.find())
      processTwistString(twists, twistMatcher.group(2), twistMatcher.group(1));
    final Matcher flipMatcher = flipPattern.matcher(orientationString);
    while (flipMatcher.find())
      processFlipString(flips, flipMatcher.group(2), flipMatcher.group(1));
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

  private static void processTwistString(final int[] twists, final String twist, final String name) {
    if (twist.equals("?"))
      processIgnoreTwist(twists, name);
    else
      processTwist(twists, name, twist.equals("+") ? 1 : -1);
  }

  private static void processFlipString(final int[] flips, final String flip, final String name) {
    if (flip.equals("?"))
      processIgnoreFlip(flips, name);
    else
      processFlip(flips, name, -1);
  }

  private static void processTwist(final int[] twists, final String name, final int d) {
    if (!Corner.exists(name))
      throw new ParserError("Expected a corner: " + name);
    Tools.addMod(twists, Corner.index(name), d, 3);
  }

  private static void processFlip(final int[] flips, final String name, final int d) {
    if (!Edge.exists(name))
      throw new ParserError("Expected an edge: " + name);
    Tools.addMod(flips, Edge.index(name), d, 2);
  }

  private static void processIgnoreTwist(final int[] twists, final String name) {
    if (!Corner.exists(name))
      throw new ParserError("Expected a corner: " + name);
    twists[Corner.index(name)] = -1;
  }

  private static void processIgnoreFlip(final int[] flips, final String name) {
    if (!Edge.exists(name))
      throw new ParserError("Expected an edge: " + name);
    flips[Edge.index(name)] = -1;
  }

  private static void processIgnoredPos(final EnumSet<Corner> corners, final EnumSet<Edge> edges, final String[] names) {
    for (final String name : names)
      if (name.endsWith("*"))
        processIgnorePosMask(corners, edges, name.substring(0, name.length() - 1));
      else if (Corner.exists(name))
        corners.add(Corner.corner(name));
      else if (Edge.exists(name))
        edges.add(Edge.edge(name));
      else
        throw new ParserError("Expected a corner or an edge: " + name);
  }

  private static void processIgnorePosMask(final EnumSet<Corner> corners, final EnumSet<Edge> edges, final String name) {
    for (final Corner c : Corner.valueSet)
      if (Tools.containsCharacters(c.toString(), name))
        corners.add(c);
    for (final Edge e : Edge.valueSet)
      if (Tools.containsCharacters(e.toString(), name))
        edges.add(e);
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
