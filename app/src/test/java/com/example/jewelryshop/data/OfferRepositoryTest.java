package com.example.jewelryshop.data;
import org.junit.Test;
import java.io.IOException;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
public class OfferRepositoryTest {
 @Test public void onlineCaches() throws IOException { OfferApi api=mock(OfferApi.class); OfferDao dao=mock(OfferDao.class); when(api.stock(1)).thenReturn(5); OfferRepository.Result r=new OfferRepository(api,dao).load(1); assertEquals(5,r.offer.stock); assertFalse(r.cached); verify(api).stock(1); verify(dao).put(argThat(o->o.productId==1 && o.stock==5)); }
 @Test public void offlineCache() throws IOException { OfferApi api=mock(OfferApi.class); OfferDao dao=mock(OfferDao.class); when(api.stock(1)).thenThrow(new IOException()); when(dao.find(1)).thenReturn(new Offer(1,4,1)); OfferRepository.Result r=new OfferRepository(api,dao).load(1); assertTrue(r.cached); assertEquals(4,r.offer.stock); verify(dao).find(1); verify(dao,never()).put(any()); }
 @Test public void noCache() throws IOException { OfferApi api=mock(OfferApi.class); OfferDao dao=mock(OfferDao.class); when(api.stock(2)).thenThrow(new IOException()); assertNull(new OfferRepository(api,dao).load(2).offer); verify(dao).find(2); }
}
