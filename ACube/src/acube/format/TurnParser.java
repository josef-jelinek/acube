package acube.format;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import acube.Turn;

public class TurnParser {
  public static EnumSet<Turn> parse(final String turnString) {
    final String[] a = turnString.trim().split("[\\s,]+");
    final List<Turn> list = new ArrayList<Turn>();
    for (final String s : a)
      for (final Turn t : Turn.values)
        if (isTurnSymbol(t, s))
          list.add(t);
    return EnumSet.copyOf(list);
  }

  private static boolean isTurnSymbol(final Turn t, final String s) {
    final String ts = t.toString();
    return ts.equals(s) || s.endsWith("*") && ts.startsWith(s.substring(0, s.length() - 1));
  }

  public static String toString(final EnumSet<Turn> turns) {
    final StringBuilder s = new StringBuilder();
    for (final Turn t : turns)
      s.append(' ').append(t.toString());
    return s.substring(1);
  }
}
