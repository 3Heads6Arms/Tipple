package com.anhhoang.tipple.feature.searchcocktails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anhhoang.tipple.core.data.model.Cocktail
import com.anhhoang.tipple.core.data.model.Resource
import com.anhhoang.tipple.core.data.repository.TippleRepository
import com.anhhoang.tipple.feature.searchcocktails.usecase.GetCocktailOfTheDayUseCase
import com.anhhoang.tipple.feature.searchcocktails.usecase.SearchCocktailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

@HiltViewModel
class SearchCocktailsViewModel @Inject constructor(
    private val searchCocktails: SearchCocktailsUseCase,
    private val getCocktailOfTheDay: GetCocktailOfTheDayUseCase,
    private val repository: TippleRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(SearchCocktailsState())
    val state = _state.asStateFlow()

    init {
        combine(
            searchCocktails.flow, repository.getFavouriteCocktails(), getCocktailOfTheDay.flow
        ) { cocktailsResult, favourites, cocktailOfTheDayResult ->
            updateCocktailOfTheDay(cocktailOfTheDayResult, favourites)
            updateCocktailsList(cocktailsResult, favourites)
        }.launchIn(viewModelScope)

        // Initiate flow of searchCocktails so combination can start working
        searchCocktails("")
        getCocktails()
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
            val cocktail =
                if (state.value.isCocktailOfTheDay) {
                    state.value.cocktailOfTheDay
                } else {
                    state.value.cocktails.find { it.id == id }
                }

            cocktail?.let {
                if (it.isFavourite) {
                    repository.removeFavouriteCocktailById(id)
                } else {
                    repository.favouriteCocktail(id)
                }
            }
        }
    }

    private fun updateCocktailsList(result: Resource<List<Cocktail>>, favourites: List<Int>) {
        when (result) {
            is Resource.Success -> {
                _state.value = _state.value.copy(
                    isLoading = false,
                    hasCocktailsError = false,
                    cocktails = result.data.map {
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
                    hasCocktailsError = true,
                )
            }
        }
    }

    private fun updateCocktailOfTheDay(result: Resource<Cocktail>, favourites: List<Int>) {
        when (result) {
            is Resource.Success -> {
                _state.value = _state.value.copy(
                    isLoading = false,
                    hasCocktailOfTheDayError = false,
                    cocktailOfTheDay = result.data.copy(isFavourite = favourites.contains(result.data.id)),
                )
            }

            is Resource.Error -> {
                _state.value = _state.value.copy(
                    isLoading = false,
                    hasCocktailOfTheDayError = true,
                )
            }
        }
    }

    private fun getCocktails(searchQuery: String = _state.value.searchQuery) {
        _state.value = _state.value.copy(
            searchQuery = searchQuery,
            isLoading = true,
            hasCocktailsError = false,
        )

        if (searchQuery.isNotBlank()) {
            searchCocktails(searchQuery)
        } else {
            getCocktailOfTheDay()
        }
    }
}

/** UI state for the search cocktails screen. */
data class SearchCocktailsState(
    val searchQuery: String = "",
    val cocktails: List<Cocktail> = emptyList(),
    val cocktailOfTheDay: Cocktail? = null,
    val isLoading: Boolean = false,
    val hasCocktailsError: Boolean = false,
    val hasCocktailOfTheDayError: Boolean = false,
) {
    val isCocktailOfTheDay: Boolean
        get() = searchQuery.isBlank()
}

/** Actions for the search cocktails screen. */
sealed interface SearchCocktailsAction {
    data class Search(val query: String) : SearchCocktailsAction
    data object Retry : SearchCocktailsAction
    data class OpenCocktail(val id: Int) : SearchCocktailsAction
    data class FavouriteToggle(val id: Int) : SearchCocktailsAction
}
