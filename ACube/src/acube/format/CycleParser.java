package acube.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import acube.Corner;
import acube.CubeState;
import acube.Edge;
import acube.Tools;

/** Supports a notation to directly write cycles and single piece orientations,
 * for instance "(UL,UR)(UFR,URB)" for the T-perm PLL or "URF+ URB-". You can
 * also mix cycles and orientations. The changes are executed from left to
 * right. So far there's no way to ignore parts of the cube, but it's very good
 * for finding blindcubing algorithms.
 *
 * @author Stefan Pochmann, Josef Jelinek */
public class CycleParser {
  private static final Pattern cyclePattern = Pattern.compile("\\((.*?)\\)");
  private static final Pattern orientationPattern = Pattern.compile("(\\w+)([-+])");

  public static CubeState parse(final String source) {
    final Corner[] corners = Corner.values();
    final Edge[] edges = Edge.values();
    final int[] twists = new int[Corner.size];
    final int[] flips = new int[Edge.size];
    final Matcher cycleMatcher = cyclePattern.matcher(source);
    while (cycleMatcher.find()) {
      final String[] cubies = cycleMatcher.group(1).trim().split("[\\s,]+");
      processCycle(corners, edges, twists, flips, cubies);
    }
    final Matcher orientationMatcher = orientationPattern.matcher(cycleMatcher.replaceAll(""));
    while (orientationMatcher.find()) {
      final int o = orientationMatcher.group(2).equals("+") ? 1 : -1;
      processOrientation(twists, flips, orientationMatcher.group(1), o);
    }
    for (int i = 0; i < twists.length; i++)
      twists[i]++;
    for (int i = 0; i < flips.length; i++)
      flips[i]++;
    return new CubeState(corners, edges, twists, flips);
  }

  private static void processCycle(final Corner[] corners, final Edge[] edges, final int[] twists, final int[] flips,
      final String[] cubies) {
    if (cubies.length < 2)
      throw new RuntimeException("Too short cycle");
    if (Corner.exists(cubies[0]))
      for (int i = cubies.length - 1; i > 0; i--)
        updateCorners(corners, twists, cubies[i - 1], cubies[i]);
    else if (Edge.exists(cubies[0]))
      for (int i = cubies.length - 1; i > 0; i--)
        updateEdges(edges, flips, cubies[i - 1], cubies[i]);
    else
      throw new RuntimeException("Expected a corner or an edge: " + cubies[0]);
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
      throw new RuntimeException("Expected a corner or an edge: " + cubie);
  }
}
