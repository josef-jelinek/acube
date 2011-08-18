package acube;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public enum Edge {
  UF, UR, UB, UL, DF, DR, DB, DL, FR, FL, BR, BL;
  public static final int size = values().length;
  public static final List<String> names;
  public static final SortedSet<Edge> valueSet = Collections.unmodifiableSortedSet(new TreeSet<Edge>(Arrays
      .asList(values())));
  static {
    final List<String> list = new ArrayList<String>();
    for (final Edge edge : values())
      list.add(edge.name());
    names = Collections.unmodifiableList(list);
  }

  public static Edge edge(final String name) {
    for (final Edge edge : values())
      if (Tools.sortCharacters(names.get(edge.ordinal())).equals(Tools.sortCharacters(name)))
        return edge;
    throw new IllegalArgumentException("Invalid edge symbol '" + name + "'");
  }

  public int orientation(final String name) {
    return names.get(ordinal()).indexOf(name.charAt(0));
  }

  public String name(final int orientation) {
    return Tools.rotate(names.get(ordinal()), orientation);
  }
}
