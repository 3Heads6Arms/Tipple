package com.anhhoang.tipple.feature.cocktaildetails.usecase

import com.anhhoang.tipple.core.coroutines.LightweightContext
import com.anhhoang.tipple.core.data.repository.TippleRepository
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest

/**
 * Use case for getting a cocktail by id.
 * Helps with transforming data into flow, to provide a seamless data update.
 */
class GetCocktailUseCase @Inject constructor(
    @LightweightContext private val coroutineContext: CoroutineContext,
    private val tippleRepository: TippleRepository,
) {
    private val query = MutableSharedFlow<Int>(
        replay = 1, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val flow = query.debounce(500.milliseconds).mapLatest { tippleRepository.getCocktailById(it) }
        .distinctUntilChanged().flowOn(coroutineContext)

    operator fun invoke(id: Int) {
        query.tryEmit(id)
    }
}