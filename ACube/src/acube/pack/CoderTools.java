package acube.pack;

public final class CoderTools {
  public static int sizeOfPermutation(final int n) { // n!
    int size = 1;
    for (int i = n; i > 1; i--)
      size *= i;
    return size;
  }

  public static int valuesUsed(final int[] values) {
    int k = 0;
    for (final int value : values)
      if (value != 0)
        k++;
    return k;
  }

  public static int[] maskIntersection(final int[] mask1, final int[] mask2) {
    if (mask1.length != mask2.length)
      throw new IllegalArgumentException("Agument sizes do not match");
    final int[] mask = new int[mask1.length];
    for (int i = 0; i < mask.length; i++)
      mask[i] = mask1[i] != 0 && mask2[i] != 0 ? 1 : 0;
    return mask;
  }

  public static int sizeOfPermutationOfUsed(final int[] values) {
    return sizeOfPermutation(valuesUsed(values));
  }

  public static int encodePermutationOfUsed(final int[] values) {
    int value = 0;
    for (int i = 0; i < values.length - 1; i++) {
      if (values[i] == 0)
        continue;
      int positionsUsed = 0;
      for (int j = i + 1; j < values.length; j++) {
        if (values[j] == 0)
          continue;
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

  public static void decodePermutationToUsed(final int[] outValues, int value) {
    int valuesUsed = 0;
    for (int i = outValues.length - 1; i >= 0; i--) {
      if (outValues[i] == 0)
        continue;
      valuesUsed++;
      outValues[i] = value % valuesUsed + 1;
      value /= valuesUsed;
      for (int j = outValues.length - 1; j > i; j--)
        if (outValues[j] != 0 && outValues[j] >= outValues[i])
          outValues[j]++;
    }
  }

  public static void composePermutation(final int[] outValues, final int[] values1,
      final int[] values2) {
    for (int i = 0; i < outValues.length; i++)
      outValues[i] = values1[values2[i]];
  }

  public static void conjugatePermutation(final int[] outValues, final int[] values,
      final int[] conjugatorValues) {
    for (int i = 0; i < outValues.length; i++)
      outValues[conjugatorValues[i]] = conjugatorValues[values[i]];
  }

  private CoderTools() {}
}
