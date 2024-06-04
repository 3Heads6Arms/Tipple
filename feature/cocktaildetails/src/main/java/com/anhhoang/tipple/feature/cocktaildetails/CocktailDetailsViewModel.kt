package com.anhhoang.tipple.feature.cocktaildetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anhhoang.tipple.core.data.model.Cocktail
import com.anhhoang.tipple.core.data.model.Resource
import com.anhhoang.tipple.core.data.repository.TippleRepository
import com.anhhoang.tipple.feature.cocktaildetails.usecase.GetCocktailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

/** ViewModel for the cocktail details screen. */
@HiltViewModel
class CocktailDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCocktail: GetCocktailUseCase,
    private val repository: TippleRepository,
) : ViewModel() {
    private val cocktailId: Int = checkNotNull(savedStateHandle["id"])
    private val _state =
        MutableStateFlow(CocktailDetailsState())
    val state = _state.asStateFlow()

    init {
        combine(
            getCocktail.flow,
            repository.getFavouriteCocktailById(cocktailId)
        ) { cocktailResult, favourite ->
            when (cocktailResult) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        hasError = false,
                        cocktail = cocktailResult.data.copy(isFavourite = favourite != null),
                    )
                }

                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        hasError = true,
                    )
                }
            }
        }.launchIn(viewModelScope)

        getCocktail()
    }

    fun onAction(action: CocktailDetailsAction) {
        when (action) {
            CocktailDetailsAction.Retry -> getCocktail()
            CocktailDetailsAction.FavouriteToggle -> toggleFavourite()
            CocktailDetailsAction.GoBack -> {}
        }
    }

    private fun toggleFavourite() {
        viewModelScope.launch {
            state.value.cocktail?.let {
                if (it.isFavourite) {
                    repository.removeFavouriteCocktailById(cocktailId)
                } else {
                    repository.favouriteCocktail(cocktailId)
                }
            }
        }
    }

    private fun getCocktail() {
        _state.value = _state.value.copy(
            isLoading = true,
            hasError = false,
        )

        getCocktail(cocktailId)
    }
}

/**
 * UI state for the cocktail details screen.
 *
 * Initial state is [isLoading] = true so we can avoid invalid state. This is a quick hax.
 */
data class CocktailDetailsState(
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
    val cocktail: Cocktail? = null,
)

/** Actions for the cocktail details screen. */
sealed interface CocktailDetailsAction {
    data object Retry : CocktailDetailsAction
    data object FavouriteToggle : CocktailDetailsAction
    data object GoBack : CocktailDetailsAction
}
