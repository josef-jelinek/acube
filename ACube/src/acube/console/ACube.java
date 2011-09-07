package acube.console;

import java.io.BufferedReader;
import java.io.Console;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.EnumSet;
import acube.Corner;
import acube.CubeState;
import acube.Edge;
import acube.Metric;
import acube.Tools;
import acube.Turn;
import acube.format.CycleParser;
import acube.format.ReidParser;
import acube.format.TurnParser;

/** The implementation of the (modified) Kociemba's 2-phase algorithm for
 * incompletely defined cubes.
 * @author Josef Jelinek josef.jelinek@gmail.com <a
 *         href="http://rubikscube.info">http://rubikscube.info</a>
 * @version 4.0 */
public final class ACube {
  private static final String infoString = "ACube 4.0a090711\n";
  private static boolean findAll = false;
  private static int maxLength = 20;
  private static Metric metric = Metric.FACE;
  private static EnumSet<Turn> turns = Turn.valueSet;
  private static final CubeState initCubeState = new CubeState(Corner.values(), Edge.values(), new int[8], new int[12]);
  private static CubeState cube = initCubeState;

  public static void main(final String[] args) {
    if (args.length == 0)
      interactiveConsole();
    else
      fileInput(args[0]);
  }

  private static void interactiveConsole() {
    final Console c = System.console();
    c.printf(infoString);
    while (processConsoleInput(c)) {}
    c.printf("\nHave a nice day.\n");
  }

  private static boolean processConsoleInput(final Console c) {
    printState(c);
    final String line = c.readLine();
    return line != null && executeCommand(c, line);
  }

  private static void fileInput(final String fileName) {
    try {
      final BufferedReader file = new BufferedReader(new FileReader(fileName));
      System.out.print(infoString);
      System.out.println("File output.");
      while (processFileInput(file)) {}
    } catch (final FileNotFoundException e) {
      System.err.println("File \"" + fileName + "\" not found.");
    } catch (final IOException e) {
      System.err.println(e.getMessage());
    }
  }

  private static boolean processFileInput(final BufferedReader file) throws IOException {
    final String line = file.readLine();
    if (line == null)
      return false;
    System.out.println("Input line: " + line);
    return line.startsWith("#") || executeCommand(null, line);
  }

  private static void printState(final Console c) {
    c.printf("----------------------------\n");
    c.printf("#Template:\nUF UR UB UL DF DR DB DL FR FL BR BL UFR URB UBL ULF DRF DFL DLB DBR\n");
    c.printf("#Current:\n%s\n", cube.reidString());
    c.printf("#Ignored positions: %s\n", cube.ignoredPositionsString());
    c.printf("#Ignored orientations: %s\n", cube.ignoredOrientationsString());
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
  }

  private static boolean executeCommand(final Console c, String s) {
    s = s.trim();
    if (s.equals("0"))
      return false;
    if (s.equals("1") || s.startsWith("1:")) {
      String p = Tools.substringAfter(s, "1:").trim();
      while (p.equals(""))
        p = readLine(c, "Enter cube state: ");
      cube = ReidParser.parse(p);
    } else if (s.equals("2") || s.startsWith("2:")) {
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
      cube = initCubeState;
    } else
      c.printf("Unrecognized command\n");
    return true;
  }

  private static String readLine(final Console c, final String message) {
    c.printf(message);
    return c.readLine().trim();
  }
}
