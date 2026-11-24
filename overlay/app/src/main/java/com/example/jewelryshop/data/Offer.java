package com.example.jewelryshop.data;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
@Entity(tableName="offer_cache")
public class Offer {
    @PrimaryKey public int productId;
    public int stock;
    @ColumnInfo(defaultValue="0") public long updatedAt;
    public Offer(int productId, int stock, long updatedAt) { this.productId=productId; this.stock=stock; this.updatedAt=updatedAt; }
}
