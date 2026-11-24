package com.example.jewelryshop.data;
import java.io.IOException;
public final class OfferRepository {
    private final OfferApi api;
    private final OfferDao dao;
    public OfferRepository(OfferApi api, OfferDao dao) { this.api=api; this.dao=dao; }
    public Result load(int id) {
        try {
            int stock=api.stock(id);
            Offer value=new Offer(id,stock,System.currentTimeMillis());
            dao.put(value);
            return new Result(value,false);
        } catch (IOException e) { return new Result(dao.find(id),true); }
    }
    public static final class Result {
        public final Offer offer;
        public final boolean cached;
        public Result(Offer offer, boolean cached) { this.offer=offer; this.cached=cached; }
    }
}
