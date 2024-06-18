package com.anhhoang.tipple.feature.searchcocktails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anhhoang.tipple.core.data.model.Cocktail
import com.anhhoang.tipple.core.data.model.Resource
import com.anhhoang.tipple.feature.searchcocktails.usecase.SearchCocktailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel
class SearchCocktailsViewModel @Inject constructor(
    private val searchCocktails: SearchCocktailsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(SearchCocktailsState())
    val state = _state.asStateFlow()

    init {
        searchCocktails.flow.onEach {
            when (it) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        cocktails = it.data,
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
        }
    }

    private fun getCocktails(searchQuery: String = _state.value.searchQuery) {
        _state.value = _state.value.copy(
            searchQuery = searchQuery,
            isLoading = true,
            hasError = false,
        )
        viewModelScope.launch {
            searchCocktails(searchQuery)
        }
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
}
