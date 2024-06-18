package com.anhhoang.tipple.feature.searchcocktails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anhhoang.tipple.core.data.model.Cocktail

/** Screen for searching for cocktails. */
@Composable
fun SearchCocktailsScreen(state: SearchCocktailsState, onAction: (SearchCocktailsAction) -> Unit) {
    Scaffold(
        topBar = {
            TippleSearchBar(
                searchQuery = state.searchQuery,
                onSearchChanged = { onAction(SearchCocktailsAction.Search(it)) },
                onClearSearch = { onAction(SearchCocktailsAction.Search("")) },
            )
        },
    ) {
        Box(
            modifier = Modifier.padding(it).fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else if (state.hasError) {
                SearchCocktailsError { onAction(SearchCocktailsAction.Retry) }
            } else if (state.cocktails.isEmpty()) {
                EmptyCocktailList()
            } else {
                CocktailList(state.cocktails, isCocktailOfTheDay = state.searchQuery.isBlank())
            }
        }
    }
}

@Composable
private fun CocktailList(cocktails: List<Cocktail>, isCocktailOfTheDay: Boolean) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(cocktails) {
            Text(text = it.name)
        }
    }
}

@Composable
private fun EmptyCocktailList() {
    MessageView(
        message = stringResource(R.string.such_emptiness),
        icon = {
            Icon(
                modifier = Modifier.size(128.dp),
                painter = painterResource(id = R.drawable.baseline_bedtime_24),
                contentDescription = stringResource(R.string.such_emptiness)
            )
        },
        action = {},
    )
}

@Composable
private fun SearchCocktailsError(onRetry: () -> Unit) {
    MessageView(
        message = stringResource(R.string.something_went_wrong),
        icon = {
            Icon(
                modifier = Modifier.size(128.dp),
                painter = painterResource(id = R.drawable.rounded_error_24),
                contentDescription = stringResource(R.string.something_went_wrong)
            )
        },
        action = {
            TextButton(onClick = onRetry) {
                Text(text = stringResource(R.string.retry))
            }
        },
    )
}

@Composable
private fun MessageView(
    message: String,
    icon: @Composable () -> Unit,
    action: @Composable () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = message, style = MaterialTheme.typography.headlineSmall)
        icon()
        action()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TippleSearchBar(
    modifier: Modifier = Modifier,
    searchQuery: String,
    onSearchChanged: (String) -> Unit,
    onClearSearch: () -> Unit,
) {
    val imm = LocalSoftwareKeyboardController.current
    SearchBar(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        query = searchQuery,
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = stringResource(R.string.search_icon),
            )
        },
        onQueryChange = onSearchChanged,
        onSearch = {
            imm?.hide()
            onSearchChanged(it)
        },
        active = false,
        placeholder = { Text(stringResource(R.string.search_cocktail_hint)) },
        trailingIcon = {
            if (searchQuery.isNotBlank()) {
                IconButton(onClick = onClearSearch) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = stringResource(R.string.close_icon)
                    )
                }
            }
        },
        onActiveChange = {},
    ) {}
}

@Preview(showBackground = true, device = Devices.PIXEL_7)
@Composable
private fun TippleSearchBarPreviewEmpty() {
    SearchCocktailsScreen(SearchCocktailsState()) {}
}
