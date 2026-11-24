package com.example.jewelryshop.data;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;
import okhttp3.*;
import java.io.IOException;
/** Public demo endpoint: not real jewelry inventory. */
public final class HttpOfferApi implements OfferApi {
    private final OkHttpClient client;
    private final HttpUrl base;
    public HttpOfferApi(OkHttpClient client, HttpUrl base) { this.client=client; this.base=base; }
    @Override public int stock(int productId) throws IOException {
        HttpUrl url=base.newBuilder().addPathSegment("products").addPathSegment(String.valueOf(productId)).build();
        try (Response response=client.newCall(new Request.Builder().url(url).build()).execute()) {
            if (!response.isSuccessful() || response.body()==null) throw new IOException("HTTP " + response.code());
            try {
                JsonObject obj=JsonParser.parseString(response.body().string()).getAsJsonObject();
                int stock=obj.get("stock").getAsInt();
                if (stock<0) throw new IllegalArgumentException("negative stock");
                return stock;
            } catch (RuntimeException e) { throw new IOException("Invalid JSON",e); }
        }
    }
}
