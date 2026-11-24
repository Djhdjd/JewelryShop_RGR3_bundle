package com.example.jewelryshop.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class OfferUseCase(
    private val repository: OfferRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    fun observe(id: Int): Flow<OfferRepository.Result> = flow {
        emit(repository.load(id))
    }.flowOn(dispatcher)
}
