package acubetextui;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;

/**
 * The implementation of the (modified) Kociemba's 2-phase algorithm for incompletely defined cubes.
 * @author Josef Jelinek josef.jelinek@gmail.com <a href="http://rubikscube.info">http://rubikscube.info</a>
 * @version 4.0
 */
public final class ACube {

//  private static Options options(String[] args) {
//    Options options = new Options();
//    options.metric = -1;
//    options.findAll = false;
//    options.findOptimal = false;
//    for (int i = 0; i < args.length; i++) {
//      for (int j = 0; j < args[i].length(); j++) {
//        switch (args[i].charAt(j)) {
//         case 'a':
//          options.findAll = true;
//          break;
//         case 'o':
//          options.findOptimal = true;
//          break;
//         case 'q':
//          options.metric = options.metric == Metric.STM ? Metric.SQTM : Metric.QTM;
//          break;
//         case 'f':
//          options.metric = Metric.FTM;
//          break;
//         case 's':
//          options.metric = options.metric == Metric.QTM ? Metric.SQTM : Metric.STM;
//          break;
//         default:
//          System.err.println("Unknown option: " + args[i].charAt(j));
//          System.err.println("Available options:");
//          System.err.println(" q - quarter-turn metrics");
//          System.err.println(" f - face-turn metrics (default)");
//          System.err.println(" s - slice-turn metrics");
//          System.err.println(" a - search for all sequences");
//          System.err.println(" o - search only for optimal sequences");
//        }
//      }
//    }
//    if (options.metric == -1)
//      options.metric = Metric.FTM;
//    if (options.findAll)
//      System.err.println("Searching for all sequences enabled.");
//    if (options.findOptimal)
//      System.err.println("Searching for optimal sequences enabled.");
//    System.err.println();
//    return options;
//  }

  public static void main(String[] args) {
    System.err.println("ACube 4.0");
    System.err.println(" by Josef Jelinek 2001-2005");
    System.err.println();
    InputStreamReader isr = new InputStreamReader(System.in);
    BufferedReader br = new BufferedReader(isr);
//    Options options = options(args);
//    CubeReader cubeReader = new CubeReader();
    System.err.println("Supported notation:");
    System.err.println(" '000' to '777' - octal number of allowed turns bits ~ UDFBLRESM");
    System.err.println(" '000000' to '777777' - octal number of allowed turns and double turns");
    System.err.println(" '~UDE' - initial moves (E, E', E2, D, D2 E', U D2 E2, ...)");
    System.err.println(" 'UF'(='-FU') 'FU'(='-UF') '@UF'(='@FU')...");
    System.err.println(" 'UFR'(='URF') 'FRU' 'RUF' '@UFR' '-UFR' '+UFR'...");
    System.err.println(" '!'(or.) '+!'(cw.) '-!'(c-cw.) '@!'(unor.) - in the correct position");
    System.err.println(" '?' '+?' '-?' '@?' - unknown cubie in the position");
    System.err.println(" '!!' '@!!' '??' '@??' - repeat to the end of corners/edges");
    System.err.println(" 'Q' - quit");
    System.err.println(" 'N' - new cube");
    System.err.println();
    while (true) {
      System.err.println("Enter a cube configuration:");
      System.err.println("UF UR UB UL DF DR DB DL FR FL BR BL UFR URB UBL ULF DRF DFL DLB DBR");
      String input = readLine(br);
      if (input == null)
        break;
      if (input.trim().equals(""))
        continue;
//      String r = cubeReader.init(options, input);
      /* returns String to signal success or error */
//      if (r == "QUIT")
//        break;
//      if (r == "OK") {
//        System.out.println();
//        System.out.println(input);
//        cubeReader.solve();
//      } else if (r == "NEW") {
//        System.err.println("Canceled.");
//      } else {
//        System.out.println();
//        System.out.println(input);
//        System.out.println("Error: " + r + ".");
//        System.err.println("Error: " + r + ".");
//      }
    }
    System.err.println();
    System.err.println("Have a nice day.");
  }

  public static String readLine(BufferedReader br) {
    try {
      return br.readLine();
    } catch (IOException e) {
      System.err.println("Input error: " + e.getMessage());
    }
    return null;
  }
}
