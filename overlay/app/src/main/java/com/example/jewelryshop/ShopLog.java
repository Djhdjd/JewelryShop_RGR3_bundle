package com.example.jewelryshop;
import android.util.Log;
public final class ShopLog {
    private ShopLog() { }
    public static void d(String tag, String message) { if (BuildConfig.DEBUG) Log.d(tag, message); }
    public static void i(String tag, String message) { if (BuildConfig.DEBUG) Log.i(tag, message); }
    public static void w(String tag, String message) { if (BuildConfig.DEBUG) Log.w(tag, message); }
    public static void e(String tag, String message, Throwable error) { if (BuildConfig.DEBUG) Log.e(tag, message, error); }
}
