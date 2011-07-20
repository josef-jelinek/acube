package acube.pack;

final class CoderPartUnordered extends CoderPart {
  @Override
  public int size(final int n, final int k) { // n! / ((n - k)! * k!)
    int nf = 1, kf = 1;
    for (int nn = n, kk = k; kk > 0; nn--, kk--) {
      nf *= nn;
      kf *= kk;
    }
    return nf / kf;
  }

  @Override
  public int encode(final int[] values) {
    int value = 0;
    int k = CoderTools.valuesUsed(values);
    for (int i = 0; i < values.length - k; i++)
      if (values[i] != 0)
        value += size(values.length - i - 1, k--);
    return value;
  }

  @Override
  public void decode(final int[] values, final int k, final int value) {
    int val = value;
    int count = k;
    for (int i = 0; i < values.length; i++) {
      final int c = size(values.length - i - 1, count);
      if (c > val)
        values[i] = 0;
      else {
        val -= c;
        values[i] = 1;
        count--;
      }
    }
  }
}
