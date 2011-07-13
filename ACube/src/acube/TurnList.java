package acube;

import java.util.Arrays;
import java.util.Vector;
import acube.data.Node;
import acube.data.NodeLink;
import acube.data.NodeSymbol;

public final class TurnList {
  private final int[][] nextA;
  private static final String fullTurns = "U* D* E* F* B* S* L* R* M* u* d* f* b* l* r*";
  private static final String halfTurns = fullTurns.replace('*', '2');
  private static final String[] allAllowedTurns = Tools.splitSortString(Tools
      .arrayToString(new String[] { fullTurns, halfTurns }));

  public TurnList(final String[] allowedTurns, final NodeLink list) {
    final String[] turns =
        allowedTurns == null || allowedTurns.length == 0 ? "U* D* F* B* L* R* E* S* M*".split(" ")
            : allowedTurns;
    if (!Tools.containsAll(allAllowedTurns, turns))
      throw new RuntimeException("Illegal symbol in allowed turns");
    if (Tools.containsDuplicities(turns))
      throw new RuntimeException("Duplicate symbol in allowed turns");
    final String[] full = fullTurns.split(" ");
    final String[] half = halfTurns.split(" ");
    final String[] sortedAllowedTurns = Tools.sortArrayCopy(turns);
    for (int i = 0; i < Math.max(full.length, half.length); i++)
      if (Arrays.binarySearch(sortedAllowedTurns, full[i]) >= 0 &&
          Arrays.binarySearch(sortedAllowedTurns, half[i]) >= 0)
        throw new RuntimeException("Ambiguous symbols '" + full[i] + "'/'" + half[i] + "' used");
    final NodeLink disabled = filter(list, sortedAllowedTurns);
    final NodeLink prefices = prefix(disabled);
    nextA = shrinkTable(makeTable(disabled, prefices, turns));
    printTable();
  }

  private void printTable() {
    System.out.print("     ");
    for (final Turn turn : Turn.values())
      System.out.printf("%-3s", turn.toString());
    System.out.println();
    int rowi = 0;
    for (final int[] row : nextA) {
      System.out.printf("%2d:", rowi++);
      for (final int turn : row)
        System.out.printf(turn < 0 ? "  -" : "%3d", turn);
      System.out.println();
    }
  }

  private static String[] expandTurns(final String[] turns) {
    final Vector<String> v = new Vector<String>();
    for (final String turn : turns)
      for (final String s : expandTurn(turn))
        v.add(s);
    return v.toArray(new String[v.size()]);
  }

  private static String[] expandTurn(final String turn) {
    if (!turn.endsWith("*"))
      return new String[] { turn };
    final String s = turn.substring(0, turn.length() - 1);
    return new String[] { s, s + "'", s + "2" };
  }

  private static boolean isSymmetrical(final String[] allowedTurns) {
    if (Tools.containsSomeNotAll("U* D* F* B* L* R*", allowedTurns))
      return false;
    if (Tools.containsSomeNotAll("U2 D2 F2 B2 L2 R2", allowedTurns))
      return false;
    if (Tools.containsSomeNotAll("E* S* M*", allowedTurns))
      return false;
    if (Tools.containsSomeNotAll("E2 S2 M2", allowedTurns))
      return false;
    if (Tools.containsSomeNotAll("u* d* f* b* l* r*", allowedTurns))
      return false;
    if (Tools.containsSomeNotAll("u2 d2 f2 b2 l2 r2", allowedTurns))
      return false;
    // may be extended by different symmetry classes (horizontal, vertical, depth axes, and 6 planes
    // e.g vertical axis ~ (F* B* L* R* | F2 B2 L2 R2 | S* M* | S2 M2 | f* b* l* r* | f2 b2 l2 r2)
    return true;
  }

  // preprocess the turn sequences according to the allowed-turns
  private static NodeLink filter(final NodeLink list, final String[] allowedTurns) {
    if (list == null)
      return null;
    if (Node.asLink(list.head()) == null)
      throw new RuntimeException("Expected list, found " + Node.asString(list.head()));
    final NodeLink item = filterItemRev(Node.asLink(list.head()), allowedTurns, null);
    return expandItemRev(item, filter(list.tail(), allowedTurns), null);
  }

  // preprocess and check if the sequence is active; returns reversed sequence
  private static NodeLink filterItemRev(final NodeLink list, final String[] allowedTurns,
      final NodeLink acu) {
    if (list == null)
      return acu;
    if (list.head() == null || list.head() instanceof NodeLink)
      return checkTurns((NodeLink)list.head(), allowedTurns) ? filterItemRev(list.tail(),
          allowedTurns, acu) : null;
    if (Node.asSymbol(list.head()) == null || !checkTurn(Node.asSymbol(list.head()), allowedTurns))
      return null;
    return filterItemRev(list.tail(), allowedTurns, NodeLink.link(list.head(), acu));
  }

  // check if the turn is enabled
  private static boolean checkTurn(final NodeSymbol symbol, final String[] allowedTurns) {
    return Arrays.binarySearch(expandTurns(allowedTurns), symbol.name()) >= 0;
  }

  // check if all symbols in the mask group are enabled
  private static boolean checkTurns(NodeLink list, final String[] allowedTurns) {
    while (list != null) {
      final NodeSymbol symbol = Node.asSymbol(list.head());
      if (symbol == null)
        return false;
      if (symbol.name().equals("@") && isSymmetrical(allowedTurns))
        return false;
      if (Arrays.binarySearch(allowedTurns, symbol.name()) < 0)
        return false;
      list = list.tail();
    }
    return true;
  }

  // expand compacted notation (reversed)
  private static NodeLink
      expandItemRev(final NodeLink list, final NodeLink tail, final NodeLink acu) {
    if (list == null)
      return acu == null || find(acu, tail) >= 0 ? tail : NodeLink.link(acu, tail);
    final String[] expanded = expandTurn(((NodeSymbol)list.head()).name());
    NodeLink link = tail;
    for (int j = expanded.length - 1; j >= 0; j--)
      link = expandItemRev(list.tail(), link, NodeLink.link(NodeSymbol.create(expanded[j]), acu));
    return link;
  }

  private static boolean equals(NodeLink list1, NodeLink list2) {
    for (; list1 != null && list2 != null; list1 = list1.tail(), list2 = list2.tail()) {
      if (list1.head() == null && list2.head() != null)
        return false;
      if (list1.head() == null || !list1.head().equals(list2.head()))
        return false;
    }
    return list1 == list2;
  }

  private static int find(final NodeLink item, NodeLink list) {
    for (int i = 0; list != null; list = list.tail(), i++)
      if (equals(item, (NodeLink)list.head()))
        return i;
    return -1;
  }

  private static NodeLink copy(final NodeLink list, final int len) {
    return len == 0 ? null : NodeLink.link(list.head(), copy(list.tail(), len - 1));
  }

  // list all prefixes of the given list items excluding duplicates and the list items
  private static NodeLink prefix(NodeLink list) {
    NodeLink acu = null;
    while (list != null) {
      final NodeLink item = Node.asLink(list.head());
      for (int i = 1, len = item.length(); i < len; i++) {
        final NodeLink pre = copy(item, i);
        if (find(pre, acu) < 0 && find(pre, list) < 0)
          acu = NodeLink.link(pre, acu);
      }
      list = list.tail();
    }
    return acu;
  }

  private static int[][] makeTable(final NodeLink finals, NodeLink prefices,
      final String[] allowedTurns) {
    prefices = NodeLink.link(null, prefices);
    final int rows = prefices.length();
    final int[][] table = new int[rows][Turn.size()];
    final String[] turns = expandTurns(allowedTurns);
    Arrays.sort(turns);
    for (int row = 0; row < rows; row++) {
      final NodeLink preseq = (NodeLink)prefices.nth(row);
      for (final Turn turn : Turn.values()) {
        NodeLink seq = NodeLink.add(preseq, turn.symbol());
        if (Arrays.binarySearch(turns, turn.toString()) < 0 || find(seq, finals) >= 0) {
          table[row][turn.ordinal()] = -1;
          continue;
        }
        for (;;) {
          final int i = find(seq, prefices);
          if (i >= 0) {
            table[row][turn.ordinal()] = i;
            break;
          }
          if (seq == null)
            throw new IllegalStateException("WTF?!");
          seq = seq.tail();
        }
      }
    }
    return table;
  }

  private static int[][] shrinkTable(final int[][] table) {
    final int[] groups = new int[table.length];
    final int[] groups2 = new int[table.length];
    for (int i = 0; i < groups.length; i++)
      groups2[i] = 0;
    int group = 1;
    int prevgroup = 0;
    while (group != prevgroup) {
      for (int i = 0; i < groups.length; i++) {
        groups[i] = groups2[i];
        groups2[i] = -1;
      }
      prevgroup = group;
      group = 0;
      for (int i = 0; i < groups.length; i++)
        if (groups2[i] < 0) {
          groups2[i] = group;
          for (int j = i + 1; j < groups.length; j++)
            if (groups2[j] < 0) {
              groups2[j] = group;
              for (int t = 0; t < table[i].length; t++)
                if (table[i][t] < 0 || table[j][t] < 0) {
                  if (table[i][t] != table[j][t])
                    groups2[j] = -1;
                } else if (groups[table[i][t]] != groups[table[j][t]])
                  groups2[j] = -1;
            }
          group++;
        }
    }
    final int[][] ntable = new int[group][];
    for (int r = 0; r < ntable.length; r++)
      for (int i = 0; i < groups2.length; i++)
        if (groups2[i] == r) {
          ntable[r] = new int[table[i].length];
          for (int t = 0; t < ntable[r].length; t++)
            ntable[r][t] = table[i][t] < 0 ? -1 : groups2[table[i][t]];
          break;
        }
    return ntable;
  }
}
