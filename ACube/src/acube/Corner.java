package acube;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public enum Corner {
  UFR, URB, UBL, ULF, DRF, DFL, DLB, DBR;
  public static final int size = values().length;
  public static final List<String> names;
  public static final SortedSet<Corner> valueSet = Collections.unmodifiableSortedSet(new TreeSet<Corner>(Arrays
      .asList(values())));
  static {
    final List<String> list = new ArrayList<String>();
    for (final Corner corner : values())
      list.add(corner.name());
    names = Collections.unmodifiableList(list);
  }

  public static Corner corner(final String name) {
    for (final Corner corner : values())
      if (Tools.sortCharacters(names.get(corner.ordinal())).equals(Tools.sortCharacters(name)))
        return corner;
    throw new IllegalArgumentException("Invalid corner symbol '" + name + "'");
  }

  public int orientation(final String name) {
    return names.get(ordinal()).indexOf(name.charAt(0));
  }

  public String name(final int orientation) {
    return Tools.rotate(names.get(ordinal()), orientation);
  }
}
