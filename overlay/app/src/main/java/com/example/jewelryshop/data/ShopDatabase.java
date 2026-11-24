package com.example.jewelryshop.data;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
@Database(entities={Offer.class}, version=2, exportSchema=true)
public abstract class ShopDatabase extends RoomDatabase {
    public abstract OfferDao offers();
    public static final Migration MIGRATION_1_2 = new Migration(1,2) {
        @Override public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE offer_cache ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0");
        }
    };
    private static volatile ShopDatabase instance;
    public static synchronized ShopDatabase get(Context context) {
        if (instance == null) instance = Room.databaseBuilder(context.getApplicationContext(), ShopDatabase.class, "jewelry.db").addMigrations(MIGRATION_1_2).build();
        return instance;
    }
}
