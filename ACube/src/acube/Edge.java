package acube;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public enum Edge {
  UF, UR, UB, UL, DF, DR, DB, DL, FR, FL, BR, BL;
  public static final int size = values().length;
  private static final Edge[] values = values();
  public static final List<String> names;
  private static final List<String> canonicalNames;
  public static final EnumSet<Edge> valueSet = EnumSet.allOf(Edge.class);
  static {
    final List<String> list = new ArrayList<String>();
    for (final Edge edge : values)
      list.add(edge.name());
    names = Collections.unmodifiableList(list);
    final List<String> canonicalList = new ArrayList<String>();
    for (final Edge edge : values)
      canonicalList.add(Tools.sortCharacters(edge.name()));
    canonicalNames = Collections.unmodifiableList(canonicalList);
  }

  public static Edge value(final int ordinal) {
    return values[ordinal];
  }

  public static Edge edge(final String name) {
    return value(index(name));
  }

  public static boolean exists(final String name) {
    return index(name) >= 0;
  }

  public static int flip(final String name) {
    return names.get(index(name)).indexOf(name.charAt(0));
  }

  public static int index(final String name) {
    final String sortedName = Tools.sortCharacters(name);
    for (int i = 0; i < names.size(); i++)
      if (canonicalNames.get(i).equals(sortedName))
        return i;
    return -1;
  }

  public String name(final int flip) {
    return Tools.rotate(names.get(ordinal()), flip);
  }
}
