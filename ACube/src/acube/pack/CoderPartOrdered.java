package acube.pack;

final class CoderPartOrdered extends CoderPart {
  @Override
  public int size(final int n, final int k) { // n! / (n - k)!
    int x = 1;
    for (int i = n, j = k; j > 0; i--, j--)
      x *= i;
    return x;
  }

  @Override
  public int encode(final int[] values) {
    return CoderPart.unordered.encode(values) * CoderTools.sizeOfPermutationOfUsed(values) +
        CoderTools.encodePermutationOfUsed(values);
  }

  @Override
  public void decode(final int[] arr, final int k, final int x) {
    CoderPart.unordered.decode(arr, k, x / CoderTools.sizeOfPermutation(k));
    CoderTools.decodePermutationToUsed(arr, x % CoderTools.sizeOfPermutation(k));
  }
}
