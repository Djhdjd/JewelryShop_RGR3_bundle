package com.example.jewelryshop;
import android.content.Context;
import android.content.Intent;
import androidx.test.core.app.ApplicationProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
@RunWith(RobolectricTestRunner.class) @Config(sdk=33, application=android.app.Application.class)
public class AndroidContextTest {
 private final Context context=ApplicationProvider.getApplicationContext();
 @Test public void sharedPreferences() { Catalog.setCount(context,0,2); assertEquals(2,Catalog.count(context,0)); assertEquals(25980,Catalog.totalPrice(context)); }
 @Test public void resources() { assertEquals("Украшения",context.getString(R.string.app_name)); }
 @Test public void intent() { Intent i=new Intent(context,ProductActivity.class).putExtra("product_index",2); assertEquals(ProductActivity.class.getName(),i.getComponent().getClassName()); assertEquals(2,i.getIntExtra("product_index",-1)); }
}
