package acube.console;

import static java.lang.System.err;
import static java.lang.System.out;
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
import acube.format.ParserError;
import acube.format.ReidParser;
import acube.format.TurnParser;

/** The implementation of the (modified) Kociemba's 2-phase algorithm for
 * incompletely defined cubes.
 * @author Josef Jelinek josef.jelinek@gmail.com <a
 *         href="http://rubikscube.info">http://rubikscube.info</a>
 * @version 4.0 */
public final class ACube {
  private static final String version = "4.0a7\n";
  private static boolean findAll = false;
  private static boolean findOptimal = false;
  private static int maxLength = 20;
  private static Metric metric = Metric.FACE;
  private static EnumSet<Turn> turns = Turn.essentialValueSet;
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
    c.printf("ACube %s\n", version);
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
      out.printf("ACube %s\nFile input.\n", version);
      while (processFileInput(file)) {}
    } catch (final FileNotFoundException e) {
      err.println("File \"" + fileName + "\" not found.");
    } catch (final IOException e) {
      err.println(e.getMessage());
    }
  }

  private static boolean processFileInput(final BufferedReader file) throws IOException {
    try {
      final String line = file.readLine();
      if (line == null)
        return false;
      out.println("Input line: " + line);
      return line.startsWith("#") || executeCommand(null, line);
    } catch (final Problem e) {
      err.printf("Problem: %s\n", e.getMessage());
      return false;
    }
  }

  private static void printState(final Console c) {
    c.printf("----------------------------\n");
    c.printf("#Template:\nUF UR UB UL DF DR DB DL FR FL BR BL UFR URB UBL ULF DRF DFL DLB DBR\n");
    c.printf("#Current:\n%s\n", cube.reidString());
    c.printf("#Ignored positions: %s\n", noneIfEmpty(cube.ignoredPositionsString()));
    c.printf("#Ignored orientations: %s\n", noneIfEmpty(cube.ignoredOrientationsString()));
    c.printf("#Enabled turns:\n%s\n", noneIfEmpty(TurnParser.toString(turns)));
    c.printf("#Disabled turns:\n%s\n\n", noneIfEmpty(TurnParser.toString(EnumSet.complementOf(turns))));
    c.printf("[1] Enter cube - standard notation\n");
    c.printf("[2] Enter cube - cycle notation\n");
    c.printf("[3] Enter allowed turns\n");
    c.printf("[4] Set metric to %s (%s)\n", Metric.nameString, metric.toString());
    c.printf("[5] Maximum length (%d)\n", maxLength);
    c.printf("[6] Find all sequences (%s)\n", findAll ? "yes" : "no");
    c.printf("[7] Use optimal solver (%s)\n", findOptimal ? "yes" : "no");
    c.printf("[ ] Solve\n");
    c.printf("[9] Reset\n");
    c.printf("[0] Exit\n");
  }

  private static String noneIfEmpty(final String s) {
    return "".equals(s) ? "none" : s;
  }

  private static boolean executeCommand(final Console c, String s) {
    s = s.trim();
    if (isOption(s, "0"))
      return false;
    try {
      if (isOption(s, "1")) {
        String p = getArg(s, "1");
        while (p.equals(""))
          p = readLine(c, "Enter cube state: ");
        cube = ReidParser.parse(p);
      } else if (isOption(s, "2")) {
        String p = getArg(s, "2");
        while (p.equals(""))
          p = readLine(c, "Enter cube state: ");
        cube = CycleParser.parse(p);
      } else if (isOption(s, "3")) {
        String p = getArg(s, "3");
        while (p.equals(""))
          p = readLine(c, "Enter turns: ");
        turns = TurnParser.parse(p);
      } else if (isOption(s, "4")) {
        String p = getArg(s, "4");
        while (p.equals(""))
          p = readLine(c, "Enter metric: ");
        for (final Metric m : Metric.values())
          if (p.equals(m.toString()))
            metric = m;
      } else if (isOption(s, "5")) {
        String p = getArg(s, "5");
        while (p.equals(""))
          p = readLine(c, "Enter number: ");
        maxLength = Math.max(1, Math.min(40, Integer.parseInt(p)));
      } else if (isOption(s, "6"))
        findAll = !findAll;
      else if (isOption(s, "7"))
        findOptimal = !findOptimal;
      else if (s.equals(""))
        if (findOptimal)
          cube.solveOptimal(metric, turns, maxLength, findAll, new ConsoleReporter(c));
        else
        cube.solve(metric, turns, maxLength, findAll, new ConsoleReporter(c));
      else if (isOption(s, "9")) {
        maxLength = 20;
        metric = Metric.FACE;
        turns = Turn.valueSet;
        cube = initCubeState;
      } else if (c != null)
        c.printf("Unrecognized command\n");
      else
        err.printf("Unrecognized command\n");
    } catch (final ParserError e) {
      if (c != null)
        c.printf("Error: %s\n", e.getMessage());
      else
        err.printf("Error: %s\n", e.getMessage());
    }
    return true;
  }

  private static boolean isOption(final String s, final String opt) {
    return s.equals(opt) || s.startsWith(opt + ":");
  }

  private static String getArg(final String s, final String opt) {
    return Tools.substringAfter(s, opt + ":").trim();
  }

  private static String readLine(final Console c, final String message) {
    if (c == null)
      throw new Problem("No console from which to read");
    c.printf(message);
    return c.readLine().trim();
  }

  private static class Problem extends RuntimeException {
    public Problem(final String message) {
      super(message);
    }
  }
}
