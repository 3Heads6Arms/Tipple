package com.anhhoang.tipple.feature.cocktaildetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.anhhoang.tipple.core.data.model.Cocktail

@Composable
fun CocktailDetailsScreen(state: CocktailDetailsState, onAction: (CocktailDetailsAction) -> Unit) {
    Scaffold(topBar = {
        CocktailDetailsTopBar(state.cocktail?.name) { onAction(CocktailDetailsAction.GoBack) }
    }) { paddingValues ->
        CocktailBody(
            Modifier.padding(paddingValues), state
        ) { onAction(CocktailDetailsAction.Retry) }
    }
}

@Composable
private fun CocktailBody(
    modifier: Modifier = Modifier, state: CocktailDetailsState, onRetry: () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        when {
            state.isLoading -> CircularProgressIndicator()
            state.hasError -> ErrorView(modifier = Modifier, onRetry = onRetry)
            else -> {
                val cocktail = checkNotNull(state.cocktail)
                CocktailDetails(cocktail)
            }
        }
    }
}

@Composable
private fun CocktailDetails(cocktail: Cocktail) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        CocktailImage(cocktail.image, cocktail.name)
        InstructionSection(cocktail.instructions)
        ItemsSection(stringResource(R.string.ingredients), cocktail.ingredients)
        ItemsSection(
            stringResource(R.string.additional_information), listOfNotNull(
                cocktail.servingGlass, cocktail.type, cocktail.category, cocktail.generation
            )
        )
    }
}

@Composable
private fun InstructionSection(instructions: String) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(stringResource(R.string.instructions), style = MaterialTheme.typography.titleMedium)
        Text(instructions, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun ItemsSection(title: String, items: List<String>) {
    Column {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
        ) {
            items(items) {
                SuggestionChip(
                    onClick = {},
                    label = { Text(it) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                    border = null,
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
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(MaterialTheme.shapes.medium)
            .aspectRatio(1f)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        when (imageLoader.state) {
            is AsyncImagePainter.State.Error -> {
                Image(
                    painter = rememberVectorPainter(image = Icons.Default.Warning),
                    modifier = Modifier.size(64.dp),
                    contentDescription = null,
                )
            }

            is AsyncImagePainter.State.Success -> {
                Image(
                    painter = imageLoader,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    contentDescription = contentDescription,
                )
            }

            else -> CircularProgressIndicator()
        }
    }
}

// Ideally this potentially can be in a common UI component library.
@Composable
private fun ErrorView(
    modifier: Modifier,
    onRetry: () -> Unit,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(R.string.something_went_wrong),
            style = MaterialTheme.typography.headlineSmall
        )
        Icon(
            modifier = Modifier.size(128.dp),
            imageVector = Icons.Default.Warning,
            contentDescription = stringResource(R.string.something_went_wrong)
        )
        TextButton(onClick = onRetry) {
            Text(text = stringResource(R.string.retry))
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CocktailDetailsTopBar(
    name: String?, onNavigationAction: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = { Text(text = name ?: "", style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            IconButton(onClick = onNavigationAction) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(R.string.go_back)
                )
            }
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = stringResource(R.string.add_to_favorites)
                )
            }
        },
    )
}

@Preview(showBackground = true, device = Devices.PIXEL_7)
@Composable
private fun CocktailDetailsTopBarPreview_loading() {
    CocktailDetailsScreen(CocktailDetailsState()) {}
}

@Preview(showBackground = true, device = Devices.PIXEL_7)
@Composable
private fun CocktailDetailsTopBarPreview_error() {
    CocktailDetailsScreen(CocktailDetailsState(isLoading = false, hasError = true)) {}
}

@Preview(showBackground = true, device = Devices.PIXEL_7)
@Composable
private fun CocktailDetailsTopBarPreview_cocktail() {
    CocktailDetailsScreen(
        CocktailDetailsState(
            isLoading = false, cocktail = Cocktail(
                id = 1,
                name = "Cocktail",
                instructions = "Instructions",
                ingredients = listOf("Ingredient 1", "Ingredient 2"),
                image = "",
                servingGlass = "Serving Glass",
                type = "Type",
                category = "Category",
                generation = "Generation",
                thumbnail = ""
            )
        )
    ) {}
}
