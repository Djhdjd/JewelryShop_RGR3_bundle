package com.example.jewelryshop.data;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
@RunWith(AndroidJUnit4.class) public class RoomTest {
 @Test public void crud() { Context c=ApplicationProvider.getApplicationContext(); ShopDatabase db=Room.inMemoryDatabaseBuilder(c,ShopDatabase.class).build(); try { OfferDao dao=db.offers(); assertNull(dao.find(1)); dao.put(new Offer(1,3,100)); assertEquals(1,dao.count()); assertEquals(3,dao.find(1).stock); dao.put(new Offer(1,6,200)); assertEquals(6,dao.find(1).stock); dao.delete(1); assertNull(dao.find(1)); } finally { db.close(); } }
 @Test public void migrateWithoutDataLoss() { Context c=ApplicationProvider.getApplicationContext(); String name="rgr3-migration.db"; c.deleteDatabase(name); SQLiteDatabase old=c.openOrCreateDatabase(name,Context.MODE_PRIVATE,null); old.execSQL("CREATE TABLE offer_cache (productId INTEGER NOT NULL PRIMARY KEY, stock INTEGER NOT NULL)"); old.execSQL("INSERT INTO offer_cache VALUES (1,7)"); old.setVersion(1); old.close(); ShopDatabase db=Room.databaseBuilder(c,ShopDatabase.class,name).addMigrations(ShopDatabase.MIGRATION_1_2).build(); try { Offer offer=db.offers().find(1); assertNotNull(offer); assertEquals(7,offer.stock); assertEquals(0L,offer.updatedAt); } finally { db.close(); c.deleteDatabase(name); } }
}
