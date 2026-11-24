package com.example.jewelryshop.domain;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import java.util.Collection;
@RunWith(Parameterized.class) public class InstallmentParameterizedTest {
 @Parameterized.Parameters(name="{0}/{1} = {2}") public static Collection<Object[]> data() { return Arrays.asList(new Object[][]{{0,3,0},{1,3,1},{12990,3,4330},{12990,6,2165},{12990,12,1083},{12000,12,1000},{10,6,2},{Integer.MAX_VALUE,12,178956971}}); }
 private final int amount,term,expected;
 public InstallmentParameterizedTest(int amount,int term,int expected) {this.amount=amount;this.term=term;this.expected=expected;}
 @Test public void calculate() { assertEquals(expected, ShopRules.monthlyPayment(amount,term)); }
}
