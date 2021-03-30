import static org.junit.Assert.*;
import org.junit.*;

public class SupplyChainManagerTest {
  @Test
  public void evaluatesExpression() {
    Calculator calculator = new Calculator();
    int sum = calculator.evaluate("1+2+3");
    assertEquals(6, sum);
  }
}