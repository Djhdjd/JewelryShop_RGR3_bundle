package com.example.jewelryshop.data;
import androidx.room.*;
@Dao public interface OfferDao {
    @Insert(onConflict=OnConflictStrategy.REPLACE) void put(Offer offer);
    @Query("SELECT * FROM offer_cache WHERE productId = :id") Offer find(int id);
    @Query("SELECT COUNT(*) FROM offer_cache") int count();
    @Query("DELETE FROM offer_cache WHERE productId = :id") void delete(int id);
}
