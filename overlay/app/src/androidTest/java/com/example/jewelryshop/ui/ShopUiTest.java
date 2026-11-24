package com.example.jewelryshop.ui;
import android.app.Activity;
import android.app.Instrumentation;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.intent.Intents;
import com.example.jewelryshop.MainActivity;
import com.example.jewelryshop.CartActivity;
import com.example.jewelryshop.OperationTracker;
import com.example.jewelryshop.R;
import org.junit.*;
import org.junit.runner.RunWith;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
@RunWith(AndroidJUnit4.class) public class ShopUiTest {
 @Rule public ActivityScenarioRule<MainActivity> rule=new ActivityScenarioRule<>(MainActivity.class);
 private final TrackerIdle idle=new TrackerIdle();
 @Before public void setup() { OperationTracker.addListener(idle.listener); IdlingRegistry.getInstance().register(idle); }
 @After public void tearDown() { IdlingRegistry.getInstance().unregister(idle); OperationTracker.removeListener(idle.listener); }
 @Test public void createOrder() { new ShopPage().addFirst().cartCount().checkout().fill("Artem","Almaty 10").submit().tracking(); }
 @Test public void invalidReceiver() { new ShopPage().addFirst().checkout().fill("A","Almaty 10").submit().invalidReceiver(); }
 @Test public void cartNavigationIntent() { Intents.init(); try { org.hamcrest.Matcher<android.content.Intent> matcher=hasComponent(CartActivity.class.getName()); Intents.intending(matcher).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_CANCELED,null)); onView(withId(R.id.topCartButton)).perform(click()); Intents.intended(matcher); } finally { Intents.release(); } }
 private static final class TrackerIdle implements IdlingResource {
  private volatile ResourceCallback callback;
  final Runnable listener=()->{ ResourceCallback cb=callback; if(cb!=null) cb.onTransitionToIdle(); };
  @Override public String getName() { return "JewelryShop operations"; }
  @Override public boolean isIdleNow() { return OperationTracker.idle(); }
  @Override public void registerIdleTransitionCallback(ResourceCallback cb) { callback=cb; }
 }
}
