package acube.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import acube.Tools;

/** Supports a notation to directly write cycles and single piece orientations,
 * for instance "(UL,UR)(UFR,URB)" for the T-perm PLL or "URF+ URB-". You can
 * also mix cycles and orientations. The changes are executed from left to
 * right. So far there's no way to ignore parts of the cube, but it's very good
 * for finding blindcubing algorithms.
 * 
 * @author Stefan Pochmann */
public class CycleParser {
  public static State parse(String source) {
    Matcher matcher =
        Pattern.compile("\\((.*?)\\)|(\\w+)([-+])").matcher(source.replaceAll("\\s", ""));
    int[] position = Tools.identityPermutation(20);
    int[] orientation = new int[20];
    while (matcher.find()) {
      if (matcher.group(1) != null) { // cycle
        String[] cubies = matcher.group(1).split(",");
        for (int i = cubies.length - 1; i > 0; i--) {
          String a = cubies[i - 1];
          String b = cubies[i];
          int posA = position(a);
          int posB = position(b);
          int oriA = orientation(a);
          int oriB = orientation(b);
          Tools.swap(position, posA, posB);
          Tools.swap(orientation, posA, posB);
          Tools.addMod(orientation, posA, oriB - oriA, a.length());
          Tools.addMod(orientation, posB, oriA - oriB, b.length());
        }
      }
      if (matcher.group(2) != null) { // orient
        String cubie = matcher.group(2);
        int d = matcher.group(3).equals("+") ? 1 : -1;
        Tools.addMod(orientation, position(cubie), d, cubie.length());
      }
    }
    return new State(position, orientation);
  }

  private static int position(String cubie) {
    for (int i = 0; i < Tools.ReidOrder.size(); i++)
      if (Tools.sortCharacters(cubie).equals(Tools.sortCharacters(Tools.ReidOrder.get(i))))
        return i;
    throw new RuntimeException("Invalid symbol '" + cubie + "'");
  }

  private static int orientation(String cubie) {
    return Tools.ReidOrder.get(position(cubie)).indexOf(cubie.charAt(0));
  }

  private CycleParser() {}
}
