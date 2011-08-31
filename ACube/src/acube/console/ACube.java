package acube.console;

import java.io.Console;
import java.util.EnumSet;
import acube.Corner;
import acube.CubeState;
import acube.Edge;
import acube.Metric;
import acube.Tools;
import acube.Turn;
import acube.format.CycleParser;
import acube.format.TurnParser;

/** The implementation of the (modified) Kociemba's 2-phase algorithm for
 * incompletely defined cubes.
 * @author Josef Jelinek josef.jelinek@gmail.com <a
 *         href="http://rubikscube.info">http://rubikscube.info</a>
 * @version 4.0 */
public final class ACube {
  public static void main(final String[] args) {
    final Console c = System.console();
    c.printf("ACube 4.0a\n by Josef Jelinek 2001-2011\n");
    boolean findAll = false;
    int maxLength = 20;
    Metric metric = Metric.FACE;
    EnumSet<Turn> turns = Turn.valueSet;
    CubeState cube = initState();
    for (;;) {
      c.printf("----------------------------\n");
      c.printf("#Template:\nUF UR UB UL DF DR DB DL FR FL BR BL UFR URB UBL ULF DRF DFL DLB DBR\n");
      c.printf("#Current:\n%s\n", cube.reidString());
      c.printf("#Turns:\n%s\n\n", TurnParser.toString(turns));
      c.printf("[1] Enter cube - standard notation\n");
      c.printf("[2] Enter cube - cycle notation\n");
      c.printf("[3] Enter allowed turns\n");
      c.printf("[4] Set metric to %s (%s)\n", Metric.nameString, metric.toString());
      c.printf("[5] Maximum length (%d)\n", maxLength);
      c.printf("[6] Find all sequences (%s)\n", findAll ? "yes" : "no");
      c.printf("[ ] Solve\n");
      c.printf("[9] Reset\n");
      c.printf("[0] Exit\n");
      String s = c.readLine();
      if (s == null)
        break;
      s = s.trim();
      if (s.equals("0"))
        break;
      if (s.equals("1"))
        c.printf("Not implemented - use [2]\n");
      else if (s.equals("2") || s.startsWith("2:")) {
        String p = Tools.substringAfter(s, "2:").trim();
        while (p.equals(""))
          p = readLine(c, "Enter cube state: ");
        cube = CycleParser.parse(p);
      } else if (s.equals("3") || s.startsWith("3:")) {
        String p = Tools.substringAfter(s, "3:").trim();
        while (p.equals(""))
          p = readLine(c, "Enter turns: ");
        turns = TurnParser.parse(p);
      } else if (s.equals("4") || s.startsWith("4:")) {
        String p = Tools.substringAfter(s, "4:").trim();
        while (p.equals(""))
          p = readLine(c, "Enter metric: ");
        for (final Metric m : Metric.values())
          if (p.equals(m.toString()))
            metric = m;
      } else if (s.equals("5") || s.startsWith("5:")) {
        String p = Tools.substringAfter(s, "5:").trim();
        while (p.equals(""))
          p = readLine(c, "Enter number: ");
        maxLength = Math.max(1, Math.min(40, Integer.parseInt(p)));
      } else if (s.equals("6"))
        findAll = !findAll;
      else if (s.equals(""))
        cube.solve(metric, turns, maxLength, findAll, new ConsoleReporter(c));
      else if (s.equals("9")) {
        maxLength = 20;
        metric = Metric.FACE;
        turns = Turn.valueSet;
        cube = initState();
      } else
        c.printf("Unrecognized command\n");
    }
    c.printf("\nHave a nice day.\n");
  }

  private static String readLine(final Console c, final String message) {
    c.printf(message);
    return c.readLine().trim();
  }

  private static CubeState initState() {
    return new CubeState(Corner.values(), Edge.values(), new int[8], new int[12]);
  }
}
