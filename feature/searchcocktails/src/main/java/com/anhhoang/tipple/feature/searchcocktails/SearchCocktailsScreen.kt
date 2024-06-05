package com.anhhoang.tipple.feature.searchcocktails

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
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
import com.anhhoang.tipple.feature.searchcocktails.SearchCocktailsScreenTestTags.SEARCH_COCKTAIL_OF_THE_DAY
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
    const val SEARCH_COCKTAIL_OF_THE_DAY = "SEARCH_COCKTAIL_OF_THE_DAY"
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
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.testTag(SEARCH_LOADING))
                }

                state.isCocktailOfTheDay -> {
                    CocktailOfTheDayBody(state, onAction)
                }

                else -> {
                    SearchResultBody(state, onAction)
                }


            }
        }
    }
}

@Composable
private fun CocktailOfTheDayBody(
    state: SearchCocktailsState, onAction: (SearchCocktailsAction) -> Unit,
) {
    when {
        state.hasCocktailOfTheDayError -> {
            SearchCocktailsError { onAction(SearchCocktailsAction.Retry) }
        }

        state.cocktailOfTheDay == null -> {
            EmptyCocktailList { onAction(SearchCocktailsAction.Retry) }
        }

        else -> {
            CocktailOfTheDayItem(
                checkNotNull(state.cocktailOfTheDay),
                onCocktailClick = {
                    onAction(SearchCocktailsAction.OpenCocktail(state.cocktailOfTheDay.id))
                },
                onFavouriteClick = {
                    onAction(SearchCocktailsAction.FavouriteToggle(state.cocktailOfTheDay.id))
                },
            )
        }
    }
}

@Composable
private fun SearchResultBody(
    state: SearchCocktailsState,
    onAction: (SearchCocktailsAction) -> Unit,
) {
    when {
        state.hasCocktailsError -> {
            SearchCocktailsError { onAction(SearchCocktailsAction.Retry) }
        }

        state.cocktails.isEmpty() -> {
            EmptyCocktailList { onAction(SearchCocktailsAction.Retry) }
        }

        else -> {
            CocktailList(
                state.cocktails,
                onCocktailClick = { onAction(SearchCocktailsAction.OpenCocktail(it)) },
                onFavouriteClick = { onAction(SearchCocktailsAction.FavouriteToggle(it)) },
            )
        }
    }
}

@Composable
private fun CocktailList(
    cocktails: List<Cocktail>, onCocktailClick: (Int) -> Unit, onFavouriteClick: (Int) -> Unit
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
private fun CocktailOfTheDayItem(
    cocktailOfTheDay: Cocktail, onCocktailClick: () -> Unit, onFavouriteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .testTag(SEARCH_COCKTAIL_OF_THE_DAY),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = onCocktailClick,
    ) {
        Text(
            text = stringResource(R.string.cocktail_of_the_day),
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(16.dp))
        CocktailImage(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            imageUrl = cocktailOfTheDay.image,
            contentDescription = cocktailOfTheDay.name
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                cocktailOfTheDay.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            FavouriteButton(cocktailOfTheDay.isFavourite, onFavouriteClick)
        }
        CocktailAdditionalInfo(cocktailOfTheDay)
    }
}

@Composable
private fun CocktailAdditionalInfo(cocktail: Cocktail) {
    Row(modifier = Modifier.wrapContentSize(unbounded = true)) {
        for (label in listOfNotNull(
            cocktail.category, cocktail.type, cocktail.servingGlass, cocktail.generation
        )) {
            SuggestionChip(
                onClick = {},
                label = { Text(label) },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
                border = null,
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
private fun CocktailItem(
    cocktail: Cocktail, onCocktailClick: () -> Unit, onFavouriteClick: () -> Unit
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
            CocktailImage(
                modifier = Modifier.size(64.dp),
                imageUrl = cocktail.image,
                contentDescription = cocktail.name,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(text = cocktail.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = cocktail.instructions,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelMedium
                )
            }
            FavouriteButton(cocktail.isFavourite, onFavouriteClick)
        }
    }
}

@Composable
private fun FavouriteButton(
    isFavourite: Boolean, onFavouriteClick: () -> Unit
) {
    val icon = if (isFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder
    val tint = if (isFavourite) Color.Red else MaterialTheme.colorScheme.onSurface
    IconButton(onClick = onFavouriteClick) {
        Icon(
            imageVector = icon,
            contentDescription = stringResource(R.string.add_to_favorites),
            tint = tint,
        )
    }
}

@Composable
private fun CocktailImage(modifier: Modifier, imageUrl: String, contentDescription: String) {
    val imageLoader = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current).data(imageUrl).size(Size.ORIGINAL)
            .build()
    )

    Box(
        modifier = modifier.clip(MaterialTheme.shapes.medium), contentAlignment = Alignment.Center
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
private fun EmptyCocktailList(onRetry: () -> Unit) {
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
        onRetry = onRetry
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
        onRetry = onRetry
    )
}

@Composable
private fun MessageView(
    modifier: Modifier,
    message: String,
    icon: @Composable () -> Unit,
    onRetry: () -> Unit,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = message, style = MaterialTheme.typography.headlineSmall)
        icon()
        TextButton(onClick = onRetry) {
            Text(text = stringResource(R.string.retry))
        }
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
    SearchCocktailsScreen(SearchCocktailsState(hasCocktailsError = true)) {}
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
