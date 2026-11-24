package com.example.jewelryshop.domain;

/** Pure business rules called by the real catalog, checkout, tracking and calculator. */
public final class ShopRules {
    private ShopRules() { }
    public static int quantity(int count) { return Math.max(0, Math.min(999, count)); }
    public static int lineTotal(int price, int count) {
        if (price < 0) throw new IllegalArgumentException("negative price");
        return Math.multiplyExact(price, quantity(count));
    }
    public static int cartTotal(int[] prices, int[] counts) {
        if (prices.length != counts.length) throw new IllegalArgumentException("size mismatch");
        int total = 0;
        for (int i = 0; i < prices.length; i++) total = Math.addExact(total, lineTotal(prices[i], counts[i]));
        return total;
    }
    public static boolean validReceiver(String value) { return value != null && value.trim().length() >= 2; }
    public static boolean validAddress(String value) { return value != null && value.trim().length() >= 5; }
    public static int stage(int value) { return Math.max(0, Math.min(5, value)); }
    public static int basePayment(int amount, int months) { checkTerm(amount, months); return amount / months; }
    public static int largerPayments(int amount, int months) { checkTerm(amount, months); return amount % months; }
    public static int monthlyPayment(int amount, int months) {
        int base = basePayment(amount, months);
        return base + (largerPayments(amount, months) == 0 ? 0 : 1);
    }
    private static void checkTerm(int amount, int months) {
        if (amount < 0 || (months != 3 && months != 6 && months != 12)) throw new IllegalArgumentException("invalid amount or term");
    }
}
