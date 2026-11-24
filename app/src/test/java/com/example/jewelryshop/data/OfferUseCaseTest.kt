package com.example.jewelryshop.data

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class OfferUseCaseTest {
    private class FakeDao : OfferDao {
        private val values = mutableMapOf<Int, Offer>()
        override fun put(offer: Offer) { values[offer.productId] = offer }
        override fun find(id: Int): Offer? = values[id]
        override fun count(): Int = values.size
        override fun delete(id: Int) { values.remove(id) }
    }
    @Test fun flowWithTestDispatcher() = runTest {
        val repo = mock(OfferRepository::class.java)
        `when`(repo.load(1)).thenReturn(OfferRepository.Result(Offer(1, 8, 0), false))
        assertEquals(8, OfferUseCase(repo, StandardTestDispatcher(testScheduler)).observe(1).first().offer.stock)
        verify(repo).load(1)
    }
    @Test fun fakePersistsResult() = runTest {
        val dao = FakeDao()
        val repo = OfferRepository({ 9 }, dao)
        val result = OfferUseCase(repo, StandardTestDispatcher(testScheduler)).observe(3).first()
        assertEquals(9, result.offer.stock)
        assertEquals(9, dao.find(3)?.stock)
    }
}
