package com.anhhoang.tipple.feature.searchcocktails.usecase

import com.anhhoang.tipple.core.coroutines.LightweightContext
import com.anhhoang.tipple.core.data.model.Resource
import com.anhhoang.tipple.core.data.repository.TippleRepository
import java.time.LocalDate
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn

class GetCocktailOfTheDayUseCase @Inject constructor(
    @LightweightContext private val coroutineContext: CoroutineContext,
    private val tippleRepository: TippleRepository,
) {
    private val query = MutableSharedFlow<Unit>(
        replay = 1, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val cocktailOfTheDay = tippleRepository.getCocktailOfTheDay().map {
        if (it != null && it.date.isEqual(LocalDate.now())) it else null
    }.stateIn(CoroutineScope(coroutineContext), SharingStarted.Eagerly, null)

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val flow = query.debounce(500.milliseconds).mapLatest {
        val cocktailOfTheDay = cocktailOfTheDay.value
        if (cocktailOfTheDay != null) {
            tippleRepository.getCocktailById(cocktailOfTheDay.id)
        } else {
            val newCocktailOfTheDay = tippleRepository.getRandomCocktail()
            if (newCocktailOfTheDay is Resource.Success) {
                tippleRepository.saveCocktailOfTheDay(newCocktailOfTheDay.data.id)
            }

            newCocktailOfTheDay
        }
    }
        .flowOn(coroutineContext)


    operator fun invoke() {
        query.tryEmit(Unit)
    }
}
