package com.example.jewelryshop.ui;
import com.example.jewelryshop.R;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.is;
public class ShopPage {
 public ShopPage addFirst() { onView(withTagValue(is((Object)"add_0"))).perform(scrollTo(),click()); return this; }
 public ShopPage cartCount() { onView(withId(R.id.topCartButton)).check(matches(withText("Корзина (1)"))); return this; }
 public ShopPage checkout() { onView(withId(R.id.topCartButton)).perform(scrollTo(),click()); onView(withId(R.id.checkoutButton)).perform(scrollTo(),click()); return this; }
 public ShopPage fill(String name,String address) { onView(withId(R.id.checkoutReceiver)).perform(scrollTo(),replaceText(name)); onView(withId(R.id.checkoutAddress)).perform(scrollTo(),replaceText(address)); return this; }
 public ShopPage submit() { onView(withId(R.id.confirmOrderButton)).perform(scrollTo(),click()); return this; }
 public ShopPage tracking() { onView(withId(R.id.trackingOrderId)).check(matches(withText(org.hamcrest.Matchers.startsWith("Заказ JS-")))); onView(withId(R.id.trackingCurrentStatus)).check(matches(isDisplayed())); return this; }
 public ShopPage invalidReceiver() { onView(withId(R.id.checkoutReceiver)).check(matches(hasErrorText("Укажите получателя"))); return this; }
}
