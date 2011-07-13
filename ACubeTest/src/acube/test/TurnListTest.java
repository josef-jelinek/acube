package acube.test;

import org.junit.Test;
import acube.TurnList;
import acube.data.Node;

public final class TurnListTest {
  @Test(expected = RuntimeException.class)
  public void test_unknown_symbol_exception() {
    tryMakeTurnList("R2 U");
  }

  @Test(expected = RuntimeException.class)
  public void test_repeated_side_exception() {
    tryMakeTurnList("U* R2 U2");
  }

  @Test(expected = RuntimeException.class)
  public void test_repeated_symbol_exception() {
    tryMakeTurnList("U* R2 U*");
  }

  private void tryMakeTurnList(String s) {
    new TurnList(s.split(" "), null);
  }

  @Test
  public void test_standard() {
    new TurnList(null, Node.asLink(Node
        .parse("((U U) (U2 U) (U' U) (U U2) (U2 U2) (U' U2) (U U') (U2 U') (U' U'))")));
  }

  @Test
  public void test_R2U() {
    new TurnList("U* R2".split(" "), Node.asLink(Node
        .parse("((U U) (U2 U) (U' U) (U U2) (U2 U2) (U' U2) (U U') (U2 U') (U' U') (R2 R2))")));
  }
}
