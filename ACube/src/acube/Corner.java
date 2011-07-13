package acube;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum Corner {
  UFR, URB, UBL, ULF, DRF, DFL, DLB, DBR;
  public static final List<String> names;
  static {
    List<String> list = new ArrayList<String>();
    for (Corner corner : values())
      list.add(corner.name());
    names = Collections.unmodifiableList(list);
  }

  public static int size() {
    return values().length;
  }

  public static Corner corner(String name) {
    for (Corner corner : values())
      if (Tools.sortCharacters(names.get(corner.ordinal())).equals(Tools.sortCharacters(name)))
        return corner;
    throw new IllegalArgumentException("Invalid corner symbol '" + name + "'");
  }

  public int orientation(String name) {
    return names.get(ordinal()).indexOf(name.charAt(0));
  }

  public String name(int orientation) {
    return Tools.rotate(names.get(ordinal()), orientation);
  }
}
