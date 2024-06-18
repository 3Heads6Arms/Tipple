package com.anhhoang.tipple.feature.searchcocktails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anhhoang.tipple.core.coroutines.MainContext
import com.anhhoang.tipple.core.data.model.Cocktail
import com.anhhoang.tipple.core.data.model.Resource
import com.anhhoang.tipple.core.data.repository.TippleRepository
import com.anhhoang.tipple.feature.searchcocktails.usecase.SearchCocktailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

@HiltViewModel
class SearchCocktailsViewModel @Inject constructor(
    private val searchCocktails: SearchCocktailsUseCase,
    private val repository: TippleRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(SearchCocktailsState())
    val state = _state.asStateFlow()

    init {
        combine(
            searchCocktails.flow, repository.getFavouriteCocktails()
        ) { cocktailsResult, favourites ->
            when (cocktailsResult) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        hasError = false,
                        cocktails = cocktailsResult.data.map {
                            it.copy(
                                isFavourite = favourites.contains(
                                    it.id
                                )
                            )
                        },
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
    }

    fun onAction(action: SearchCocktailsAction) {
        when (action) {
            is SearchCocktailsAction.Search -> getCocktails(action.query)
            SearchCocktailsAction.Retry -> getCocktails()
            is SearchCocktailsAction.FavouriteToggle -> toggleFavourite(action.id)
            is SearchCocktailsAction.OpenCocktail -> {}
        }
    }

    private fun toggleFavourite(id: Int) {
        viewModelScope.launch {
            state.value.cocktails.find { it.id == id }?.let {
                if (it.isFavourite) {
                    repository.removeFavouriteCocktailById(id)
                } else {
                    repository.favouriteCocktail(id)
                }
            }
        }
    }


    private fun getCocktails(searchQuery: String = _state.value.searchQuery) {
        _state.value = _state.value.copy(
            searchQuery = searchQuery,
            isLoading = true,
            hasError = false,
        )

        searchCocktails(searchQuery)
    }
}

/** UI state for the search cocktails screen. */
data class SearchCocktailsState(
    val searchQuery: String = "",
    val cocktails: List<Cocktail> = emptyList(),
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
)

/** Actions for the search cocktails screen. */
sealed interface SearchCocktailsAction {
    data class Search(val query: String) : SearchCocktailsAction
    data object Retry : SearchCocktailsAction
    data class OpenCocktail(val id: Int) : SearchCocktailsAction
    data class FavouriteToggle(val id: Int) : SearchCocktailsAction
}
