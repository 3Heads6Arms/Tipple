package com.anhhoang.tipple.feature.searchcocktails

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.anhhoang.tipple.core.data.model.Cocktail
import com.anhhoang.tipple.feature.searchcocktails.SearchCocktailsScreenTestTags.EMPTY_LIST
import com.anhhoang.tipple.feature.searchcocktails.SearchCocktailsScreenTestTags.SEARCH_BAR
import com.anhhoang.tipple.feature.searchcocktails.SearchCocktailsScreenTestTags.SEARCH_ERROR
import com.anhhoang.tipple.feature.searchcocktails.SearchCocktailsScreenTestTags.SEARCH_LOADING
import com.anhhoang.tipple.feature.searchcocktails.SearchCocktailsScreenTestTags.SEARCH_RESULT
import com.anhhoang.tipple.feature.searchcocktails.SearchCocktailsScreenTestTags.SEARCH_RESULTS

@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
object SearchCocktailsScreenTestTags {
    const val SEARCH_BAR = "SEARCH_BAR"
    const val SEARCH_RESULTS = "SEARCH_RESULTS"
    const val SEARCH_RESULT = "SEARCH_RESULT"
    const val SEARCH_LOADING = "SEARCH_LOADING"
    const val SEARCH_ERROR = "SEARCH_ERROR"
    const val EMPTY_LIST = "EMPTY_LIST"
}

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
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.testTag(SEARCH_LOADING))
            } else if (state.hasError) {
                SearchCocktailsError { onAction(SearchCocktailsAction.Retry) }
            } else if (state.cocktails.isEmpty()) {
                EmptyCocktailList()
            } else {
                CocktailList(
                    state.cocktails,
                    onCocktailClick = { onAction(SearchCocktailsAction.OpenCocktail(it)) },
                    onFavouriteClick = { onAction(SearchCocktailsAction.FavouriteToggle(it)) },
                )
            }
        }
    }
}

@Composable
private fun CocktailList(
    cocktails: List<Cocktail>,
    onCocktailClick: (Int) -> Unit,
    onFavouriteClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag(SEARCH_RESULTS),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(cocktails) {
            CocktailItem(
                cocktail = it,
                onCocktailClick = { onCocktailClick(it.id) },
                onFavouriteClick = { onFavouriteClick(it.id) },
            )
        }
    }
}

@Composable
private fun CocktailItem(
    cocktail: Cocktail,
    onCocktailClick: () -> Unit,
    onFavouriteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(SEARCH_RESULT),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = onCocktailClick,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CocktailImage(cocktail.image, cocktail.name)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.SpaceBetween) {
                Text(text = cocktail.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = cocktail.instructions,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelMedium
                )
            }
            val icon =
                if (cocktail.isFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder
            val tint = if (cocktail.isFavourite) Color.Red else MaterialTheme.colorScheme.onSurface
            IconButton(onClick = onFavouriteClick) {
                Icon(
                    imageVector = icon,
                    contentDescription = stringResource(R.string.add_to_favorites),
                    tint = tint,
                )
            }
        }
    }
}

@Composable
private fun CocktailImage(imageUrl: String, contentDescription: String) {
    val imageLoader = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current).data(imageUrl).size(Size.ORIGINAL)
            .build()
    )

    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .size(64.dp),
        contentAlignment = Alignment.Center
    ) {
        when (imageLoader.state) {
            is AsyncImagePainter.State.Error -> {
                Image(
                    painter = rememberVectorPainter(image = Icons.Default.Warning),
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = null,
                )
            }

            is AsyncImagePainter.State.Success -> {
                Image(
                    painter = imageLoader,
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = contentDescription,
                )
            }

            else -> CircularProgressIndicator()
        }
    }
}

@Composable
private fun EmptyCocktailList() {
    MessageView(
        modifier = Modifier.testTag(EMPTY_LIST),
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
        modifier = Modifier.testTag(SEARCH_ERROR),
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
    modifier: Modifier,
    message: String,
    icon: @Composable () -> Unit,
    action: @Composable () -> Unit,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
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
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag(SEARCH_BAR),
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
private fun TippleSearchBarPreview_empty() {
    SearchCocktailsScreen(SearchCocktailsState()) {}
}

@Preview(showBackground = true, device = Devices.PIXEL_7)
@Composable
private fun TippleSearchBarPreview_loading() {
    SearchCocktailsScreen(SearchCocktailsState(isLoading = true)) {}
}

@Preview(showBackground = true, device = Devices.PIXEL_7)
@Composable
private fun TippleSearchBarPreview_error() {
    SearchCocktailsScreen(SearchCocktailsState(hasError = true)) {}
}

@Preview(showBackground = true, device = Devices.PIXEL_7)
@Composable
private fun TippleSearchBarPreview_withItems() {
    SearchCocktailsScreen(
        SearchCocktailsState(
            cocktails = listOf(
                Cocktail(
                    id = 1,
                    name = "Mojito",
                    instructions = "Mix all ingredients",
                    thumbnail = "",
                    generation = null,
                    servingGlass = "",
                    image = "",
                    ingredients = emptyList(),
                    category = "",
                    type = "",
                ), Cocktail(
                    id = 1,
                    name = "Mojito",
                    instructions = "Mix all ingredients",
                    thumbnail = "",
                    generation = null,
                    servingGlass = "",
                    image = "",
                    ingredients = emptyList(),
                    category = "",
                    type = "",
                )
            )
        )
    ) {}
}
