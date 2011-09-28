package acube.symmetry;

public class Symmetry {
  private static final int N_CUBESYM = 48;
  private static final int N_SYM = 16;
  private static final int N_TWIST = 18;
  private static final int FACE_F = 0;
  private static final int FACE_R = 1;
  private static final int FACE_U = 2;
  private static final int FACE_B = 3;
  private static final int FACE_L = 4;
  private static final int FACE_D = 5;
  private static final int TWIST_F = 0;
  private static final int TWIST_F2 = 1;
  private static final int TWIST_F3 = 2;
  private static final int TWIST_R = 3;
  private static final int TWIST_R2 = 4;
  private static final int TWIST_R3 = 5;
  private static final int TWIST_U = 6;
  private static final int TWIST_U2 = 7;
  private static final int TWIST_U3 = 8;
  private static final int TWIST_B = 9;
  private static final int TWIST_B2 = 10;
  private static final int TWIST_B3 = 11;
  private static final int TWIST_L = 12;
  private static final int TWIST_L2 = 13;
  private static final int TWIST_L3 = 14;
  private static final int TWIST_D = 15;
  private static final int TWIST_D2 = 16;
  private static final int TWIST_D3 = 17;
  private int[][] sym_x_invsym_to_sym;

  public void calcGroupTable(final int[][] group_table) { // N_CUBESYM x N_CUBESYM
    final int[][] sym_array = new int[N_CUBESYM][6];
    final int[] sym_pack = new int[N_CUBESYM];
    final int[] temp_array = new int[6];
    int ii, jj, kk, pack;
    perm_n_init(6, sym_array[0]);
    perm_n_init(6, sym_array[1]);
    four_cycle(sym_array[1], FACE_F, FACE_L, FACE_B, FACE_R);
    for (ii = 2; ii < 4; ii++)
      perm_n_compose(6, sym_array[1], sym_array[ii - 1], sym_array[ii]);
    perm_n_init(6, sym_array[4]);
    two_cycle(sym_array[4], FACE_U, FACE_D);
    two_cycle(sym_array[4], FACE_R, FACE_L);
    for (ii = 5; ii < 8; ii++)
      perm_n_compose(6, sym_array[4], sym_array[ii - 4], sym_array[ii]);
    perm_n_init(6, sym_array[8]);
    two_cycle(sym_array[8], FACE_U, FACE_D);
    for (ii = 9; ii < 16; ii++)
      perm_n_compose(6, sym_array[8], sym_array[ii - 8], sym_array[ii]);
    perm_n_init(6, sym_array[16]);
    three_cycle(sym_array[16], FACE_U, FACE_F, FACE_R);
    three_cycle(sym_array[16], FACE_D, FACE_B, FACE_L);
    for (ii = 17; ii < 48; ii++)
      perm_n_compose(6, sym_array[16], sym_array[ii - 16], sym_array[ii]);
    for (ii = 0; ii < N_CUBESYM; ii++)
      sym_pack[ii] = perm_n_pack(6, sym_array[ii]);
    for (ii = 0; ii < N_CUBESYM; ii++)
      for (jj = 0; jj < N_CUBESYM; jj++) {
        perm_n_compose(6, sym_array[ii], sym_array[jj], temp_array);
        pack = perm_n_pack(6, temp_array);
        for (kk = 0; kk < N_CUBESYM; kk++)
          if (sym_pack[kk] == pack) {
            group_table[ii][jj] = kk;
            break;
          }
      }
  }

  private static void two_cycle(final int[] a, final int i1, final int i2) {
    final int temp = a[i1];
    a[i1] = a[i2];
    a[i2] = temp;
  }

  private static void three_cycle(final int[] a, final int i1, final int i2, final int i3) {
    final int temp = a[i1];
    a[i1] = a[i2];
    a[i2] = a[i3];
    a[i3] = temp;
  }

  private static void four_cycle(final int[] a, final int i1, final int i2, final int i3,
      final int i4) {
    final int temp = a[i1];
    a[i1] = a[i2];
    a[i2] = a[i3];
    a[i3] = a[i4];
    a[i4] = temp;
  }

  private static void perm_n_init(final int n, final int[] array_out) {
    for (int i = 0; i < n; i++)
      array_out[i] = i;
  }

  private static int perm_n_pack(final int n, final int[] array_in) {
    int value = 0;
    for (int i = 0; i < n; i++) {
      value *= n - i;
      for (int j = i + 1; j < n; j++)
        if (array_in[j] < array_in[i])
          value++;
    }
    return value;
  }

  private static void perm_n_compose(final int n, final int[] perm0_in, final int[] perm1_in,
      final int[] perm_out) {
    for (int i = 0; i < n; i++)
      perm_out[i] = perm0_in[perm1_in[i]];
  }

  private void init_sym_x_invsym_to_sym() {
    final int[][] group_table = new int[N_CUBESYM][N_CUBESYM];
    final int[] group_inverse = new int[N_CUBESYM];
    sym_x_invsym_to_sym = new int[N_SYM][N_SYM];
    calc_group_table(group_table);
    for (int i = 0; i < N_SYM; i++)
      for (int j = 0; j < N_SYM; j++)
        if (group_table[i][j] == 0) {
          group_inverse[i] = j;
          break;
        }
    for (int i = 0; i < N_SYM; i++)
      for (int j = 0; j < N_SYM; j++)
        sym_x_invsym_to_sym[i][j] = group_table[i][group_inverse[j]];
  }

  private void calc_sym_on_twist(final int[][] sym_on_twist) { // N_CUBESYM x N_TWIST
    perm_n_init(N_TWIST, sym_on_twist[0]);
    perm_n_init(N_TWIST, sym_on_twist[1]);
    four_cycle(sym_on_twist[1], TWIST_F, TWIST_L, TWIST_B, TWIST_R);
    four_cycle(sym_on_twist[1], TWIST_F2, TWIST_L2, TWIST_B2, TWIST_R2);
    four_cycle(sym_on_twist[1], TWIST_F3, TWIST_L3, TWIST_B3, TWIST_R3);
    for (int sym = 2; sym < 4; sym++)
      perm_n_compose(N_TWIST, sym_on_twist[1], sym_on_twist[sym - 1], sym_on_twist[sym]);
    perm_n_init(N_TWIST, sym_on_twist[4]);
    two_cycle(sym_on_twist[4], TWIST_R, TWIST_L);
    two_cycle(sym_on_twist[4], TWIST_R2, TWIST_L2);
    two_cycle(sym_on_twist[4], TWIST_R3, TWIST_L3);
    two_cycle(sym_on_twist[4], TWIST_U, TWIST_D);
    two_cycle(sym_on_twist[4], TWIST_U2, TWIST_D2);
    two_cycle(sym_on_twist[4], TWIST_U3, TWIST_D3);
    for (int sym = 5; sym < 8; sym++)
      perm_n_compose(N_TWIST, sym_on_twist[4], sym_on_twist[sym - 4], sym_on_twist[sym]);
    perm_n_init(N_TWIST, sym_on_twist[8]);
    two_cycle(sym_on_twist[8], TWIST_F, TWIST_F3);
    two_cycle(sym_on_twist[8], TWIST_R, TWIST_R3);
    two_cycle(sym_on_twist[8], TWIST_B, TWIST_B3);
    two_cycle(sym_on_twist[8], TWIST_L, TWIST_L3);
    two_cycle(sym_on_twist[8], TWIST_U, TWIST_D3);
    two_cycle(sym_on_twist[8], TWIST_U2, TWIST_D2);
    two_cycle(sym_on_twist[8], TWIST_U3, TWIST_D);
    for (int sym = 9; sym < 16; sym++)
      perm_n_compose(N_TWIST, sym_on_twist[8], sym_on_twist[sym - 8], sym_on_twist[sym]);
    perm_n_init(N_TWIST, sym_on_twist[16]);
    three_cycle(sym_on_twist[16], TWIST_F, TWIST_R, TWIST_U);
    three_cycle(sym_on_twist[16], TWIST_F2, TWIST_R2, TWIST_U2);
    three_cycle(sym_on_twist[16], TWIST_F3, TWIST_R3, TWIST_U3);
    three_cycle(sym_on_twist[16], TWIST_B, TWIST_L, TWIST_D);
    three_cycle(sym_on_twist[16], TWIST_B2, TWIST_L2, TWIST_D2);
    three_cycle(sym_on_twist[16], TWIST_B3, TWIST_L3, TWIST_D3);
    for (int sym = 17; sym < 48; sym++)
      perm_n_compose(N_TWIST, sym_on_twist[16], sym_on_twist[sym - 16], sym_on_twist[sym]);
  }

  private final int[][] invsym_on_twist_ud = new int[N_SYM][N_TWIST];
  private final int[][] invsym_on_twist_fb = new int[N_SYM][N_TWIST];
  private final int[][] invsym_on_twist_rl = new int[N_SYM][N_TWIST];

  private void init_invsym_on_twist() {
    final int[][] sym_on_twist = new int[N_CUBESYM][N_TWIST];
    final int[] ud_to_rl = new int[N_TWIST];
    calc_sym_on_twist(sym_on_twist);
    for (int sym = 0; sym < N_SYM; sym++)
      for (int twist = 0; twist < N_TWIST; twist++)
        invsym_on_twist_ud[sym][twist] = sym_on_twist[sym_x_invsym_to_sym[0][sym]][twist];
    perm_n_init(N_TWIST, ud_to_rl);
    three_cycle(ud_to_rl, TWIST_F, TWIST_R, TWIST_U);
    three_cycle(ud_to_rl, TWIST_F2, TWIST_R2, TWIST_U2);
    three_cycle(ud_to_rl, TWIST_F3, TWIST_R3, TWIST_U3);
    three_cycle(ud_to_rl, TWIST_B, TWIST_L, TWIST_D);
    three_cycle(ud_to_rl, TWIST_B2, TWIST_L2, TWIST_D2);
    three_cycle(ud_to_rl, TWIST_B3, TWIST_L3, TWIST_D3);
    for (int sym = 0; sym < N_SYM; sym++)
      for (int twist = 0; twist < N_TWIST; twist++)
        invsym_on_twist_rl[sym][twist] = invsym_on_twist_ud[sym][ud_to_rl[twist]];
    for (int sym = 0; sym < N_SYM; sym++)
      for (int twist = 0; twist < N_TWIST; twist++)
        invsym_on_twist_fb[sym][twist] = invsym_on_twist_rl[sym][ud_to_rl[twist]];
  }

  private void calc_group_table(final int[][] group_table) { // N_CUBESYM x N_CUBESYM
    final int[][] sym_array = new int[N_CUBESYM][6];
    final int[] sym_pack = new int[N_CUBESYM];
    final int[] temp_array = new int[6];
    perm_n_init(6, sym_array[0]);
    perm_n_init(6, sym_array[1]);
    four_cycle(sym_array[1], FACE_F, FACE_L, FACE_B, FACE_R);
    for (int i = 2; i < 4; i++)
      perm_n_compose(6, sym_array[1], sym_array[i - 1], sym_array[i]);
    perm_n_init(6, sym_array[4]);
    two_cycle(sym_array[4], FACE_U, FACE_D);
    two_cycle(sym_array[4], FACE_R, FACE_L);
    for (int i = 5; i < 8; i++)
      perm_n_compose(6, sym_array[4], sym_array[i - 4], sym_array[i]);
    perm_n_init(6, sym_array[8]);
    two_cycle(sym_array[8], FACE_U, FACE_D);
    for (int i = 9; i < 16; i++)
      perm_n_compose(6, sym_array[8], sym_array[i - 8], sym_array[i]);
    perm_n_init(6, sym_array[16]);
    three_cycle(sym_array[16], FACE_U, FACE_F, FACE_R);
    three_cycle(sym_array[16], FACE_D, FACE_B, FACE_L);
    for (int i = 17; i < 48; i++)
      perm_n_compose(6, sym_array[16], sym_array[i - 16], sym_array[i]);
    for (int i = 0; i < N_CUBESYM; i++)
      sym_pack[i] = perm_n_pack(6, sym_array[i]);
    for (int i = 0; i < N_CUBESYM; i++)
      for (int j = 0; j < N_CUBESYM; j++) {
        perm_n_compose(6, sym_array[i], sym_array[j], temp_array);
        final int pack = perm_n_pack(6, temp_array);
        for (int k = 0; k < N_CUBESYM; k++)
          if (sym_pack[k] == pack) {
            group_table[i][j] = k;
            break;
          }
      }
  }
}
