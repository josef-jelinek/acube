package acube.pack;

public final class CoderTools {
  public static int permutationSize(final int n) { // n!
    int size = 1;
    for (int i = n; i > 1; i--)
      size *= i;
    return size;
  }

  public static int valuesUsed(final boolean[] mask) {
    int k = 0;
    for (final boolean flag : mask)
      if (flag)
        k++;
    return k;
  }

  public static int valuesUsed(final int[] values) {
    int k = 0;
    for (final int value : values)
      if (value >= 0)
        k++;
    return k;
  }

  public static boolean[] maskIntersection(final boolean[] mask1, final boolean[] mask2) {
    assert mask1.length != mask2.length;
    final boolean[] mask = new boolean[mask1.length];
    for (int i = 0; i < mask.length; i++)
      mask[i] = mask1[i] && mask2[i];
    return mask;
  }

  public static int usedPermutationSize(final int[] values) {
    return permutationSize(valuesUsed(values));
  }

  public static int encodeUsedPermutation(final int[] values) {
    int value = 0;
    for (int i = 0; i < values.length - 1; i++)
      if (values[i] >= 0) {
        int positionsUsed = 0;
        for (int j = i + 1; j < values.length; j++)
          if (values[j] >= 0) {
            positionsUsed++;
            if (values[j] < values[i])
              value++;
          }
        if (positionsUsed <= 1)
          return value;
        value *= positionsUsed;
      }
    return 0;
  }

  public static void decodePermutationToUsed(final int[] outValues, final int value) {
    int valuesUsed = 0;
    int val = value;
    for (int i = outValues.length - 1; i >= 0; i--)
      if (outValues[i] >= 0) {
        valuesUsed++;
        outValues[i] = val % valuesUsed;
        val /= valuesUsed;
        for (int j = outValues.length - 1; j > i; j--)
          if (outValues[j] >= 0 && outValues[j] >= outValues[i])
            outValues[j]++;
      }
  }

  public static void composePermutation(final int[] outValues, final int[] values1, final int[] values2) {
    for (int i = 0; i < outValues.length; i++)
      outValues[i] = values1[values2[i]];
  }

  public static void conjugatePermutation(final int[] outValues, final int[] values, final int[] conjugatorValues) {
    for (int i = 0; i < outValues.length; i++)
      outValues[conjugatorValues[i]] = conjugatorValues[values[i]];
  }

  public static int totalOrientation(final int[] values, final int order) {
    int total = 0;
    for (final int value : values)
      total += Math.max(0, value);
    return total % order;
  }

  public static int permutationParity(final int[] values) {
    int p = 0;
    final int[] v = fillPermutationGaps(values);
    final int[] t = new int[v.length];
    for (int i = 0; i < v.length; i++)
      if (t[i] == 1)
        p++;
      else
        for (int j = v[i]; j != i; j = v[j])
          t[j] = 1;
    return p % 2;
  }

  private static int[] fillPermutationGaps(final int[] values) {
    final int[] v = values.clone();
    final int[] t = new int[v.length];
    for (int i = 0; i < v.length; i++)
      if (v[i] >= 0)
        t[v[i]] = 1;
    for (int i = 0; i < t.length; i++)
      if (t[i] == 0)
        for (int j = 0; j < v.length; j++)
          if (v[j] < 0)
            v[j] = i;
    return v;
  }
}
