package com.example.jewelryshop;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CopyOnWriteArrayList;
public final class OperationTracker {
    private static final AtomicInteger COUNT = new AtomicInteger();
    private static final CopyOnWriteArrayList<Runnable> LISTENERS = new CopyOnWriteArrayList<>();
    private OperationTracker() { }
    public static void begin() { COUNT.incrementAndGet(); }
    public static void end() { if (COUNT.decrementAndGet() == 0) for (Runnable r : LISTENERS) r.run(); }
    public static boolean idle() { return COUNT.get() == 0; }
    public static void addListener(Runnable r) { LISTENERS.add(r); }
    public static void removeListener(Runnable r) { LISTENERS.remove(r); }
}
