package acube;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum Edge {

  UF, UR, UB, UL, DF, DR, DB, DL, FR, FL, BR, BL;

  public static final List<String> names;

  static {
    List<String> list = new ArrayList<String>();
    for (Edge edge : values())
      list.add(edge.name());
    names = Collections.unmodifiableList(list);
  }

  public static int size() {
    return values().length;
  }

  public static Edge edge(String name) {
    for (Edge edge : values())
      if (Tools.sortCharacters(names.get(edge.ordinal())).equals(Tools.sortCharacters(name)))
        return edge;
    throw new IllegalArgumentException("Invalid edge symbol '" + name + "'");
  }

  public int orientation(String name) {
    return names.get(ordinal()).indexOf(name.charAt(0));
  }

  public String name(int orientation) {
    return Tools.rotate(names.get(ordinal()), orientation);
  }
}
