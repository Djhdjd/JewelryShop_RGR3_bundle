package com.example.jewelryshop.domain;
import org.junit.Test;
import static org.junit.Assert.*;
public class ShopRulesTest {
 @Test public void negativeQuantity() { assertEquals(0,ShopRules.quantity(-1)); }
 @Test public void maximumQuantity() { assertEquals(999,ShopRules.quantity(1000)); }
 @Test public void normalQuantity() { assertEquals(5,ShopRules.quantity(5)); }
 @Test public void lineTotal() { assertEquals(25980,ShopRules.lineTotal(12990,2)); }
 @Test(expected=IllegalArgumentException.class) public void negativePrice() { ShopRules.lineTotal(-1,1); }
 @Test public void emptyCart() { assertEquals(0,ShopRules.cartTotal(new int[0],new int[0])); }
 @Test public void cartTotal() { assertEquals(41970,ShopRules.cartTotal(new int[]{12990,15990},new int[]{2,1})); }
 @Test(expected=IllegalArgumentException.class) public void invalidCartShape() { ShopRules.cartTotal(new int[]{1},new int[0]); }
 @Test public void receiverValid() { assertTrue(ShopRules.validReceiver(" AB ")); assertFalse(ShopRules.validReceiver("A")); assertFalse(ShopRules.validReceiver(null)); }
 @Test public void addressValid() { assertTrue(ShopRules.validAddress("12345")); assertFalse(ShopRules.validAddress("1234")); assertFalse(ShopRules.validAddress(null)); }
 @Test public void deliveryBoundaries() { assertEquals(0,ShopRules.stage(-1)); assertEquals(5,ShopRules.stage(7)); assertEquals(3,ShopRules.stage(3)); }
 @Test public void installmentRemainder() { assertEquals(1082,ShopRules.basePayment(12990,12)); assertEquals(6,ShopRules.largerPayments(12990,12)); assertEquals(1083,ShopRules.monthlyPayment(12990,12)); }
 @Test(expected=ArithmeticException.class) public void overflow() { ShopRules.lineTotal(Integer.MAX_VALUE,2); }
 @Test public void exactInstallment() { assertEquals(1000,ShopRules.monthlyPayment(12000,12)); }
 @Test(expected=IllegalArgumentException.class) public void invalidTerm() { ShopRules.monthlyPayment(1000,0); }
 @Test(expected=IllegalArgumentException.class) public void negativeAmount() { ShopRules.monthlyPayment(-1,3); }
}
