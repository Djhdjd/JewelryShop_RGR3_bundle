package com.example.jewelryshop.data;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.*;
import org.junit.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.*;
public class HttpOfferApiTest {
 private MockWebServer server;
 private HttpOfferApi api;
 @Before public void setup() throws IOException { server=new MockWebServer(); server.start(); api=new HttpOfferApi(new OkHttpClient.Builder().readTimeout(500,TimeUnit.MILLISECONDS).callTimeout(2,TimeUnit.SECONDS).build(),server.url("/")); }
 @After public void close() throws IOException { server.shutdown(); }
 @Test public void success() throws Exception { server.enqueue(new MockResponse().setBody("{\"stock\":7}")); assertEquals(7,api.stock(1)); assertEquals("/products/1",server.takeRequest(1,TimeUnit.SECONDS).getPath()); }
 @Test(expected=IOException.class) public void notFound() throws IOException { server.enqueue(new MockResponse().setResponseCode(404)); api.stock(1); }
 @Test(expected=IOException.class) public void serverError() throws IOException { server.enqueue(new MockResponse().setResponseCode(500)); api.stock(1); }
 @Test(expected=IOException.class) public void timeout() throws IOException { server.enqueue(new MockResponse().setBody("{\"stock\":7}").setBodyDelay(3,TimeUnit.SECONDS)); api.stock(1); }
 @Test(expected=IOException.class) public void badJson() throws IOException { server.enqueue(new MockResponse().setBody("not-json")); api.stock(1); }
}
