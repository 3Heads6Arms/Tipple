package com.anhhoang.tipple.core.data.extensions

import com.anhhoang.tipple.core.data.model.Cocktail
import com.anhhoang.tipple.core.network.model.NetworkCocktail

/**
 * Converts a [NetworkCocktail] to a [Cocktail].
 * Internal because we don't want this to be exposed to the public API. This is also good practice since this package
 * does not own [NetworkCocktail].
 */
internal fun NetworkCocktail.toCocktail(): Cocktail {
    return Cocktail(
        id = id,
        name = name,
        ingredients = getIngredients(),
        instructions = instructions,
        thumbnail = image,
        image = image,
        generation = generation,
        type = type,
        servingGlass = glass,
        category = category,
    )
}

private fun NetworkCocktail.getIngredients(): List<String> = buildList {
    ingredient1?.let { add("$it ${measure1 ?: ""}".trim()) }
    ingredient2?.let { add("$it ${measure2 ?: ""}".trim()) }
    ingredient3?.let { add("$it ${measure3 ?: ""}".trim()) }
    ingredient4?.let { add("$it ${measure4 ?: ""}".trim()) }
    ingredient5?.let { add("$it ${measure5 ?: ""}".trim()) }
    ingredient6?.let { add("$it ${measure6 ?: ""}".trim()) }
    ingredient7?.let { add("$it ${measure7 ?: ""}".trim()) }
    ingredient8?.let { add("$it ${measure8 ?: ""}".trim()) }
    ingredient9?.let { add("$it ${measure9 ?: ""}".trim()) }
    ingredient10?.let { add("$it ${measure10 ?: ""}".trim()) }
    ingredient11?.let { add("$it ${measure11 ?: ""}".trim()) }
    ingredient12?.let { add("$it ${measure12 ?: ""}".trim()) }
    ingredient13?.let { add("$it ${measure13 ?: ""}".trim()) }
    ingredient14?.let { add("$it ${measure14 ?: ""}".trim()) }
    ingredient15?.let { add("$it ${measure15 ?: ""}".trim()) }
}
