package com.anhhoang.tipple.core.data.extensions

import com.anhhoang.tipple.core.data.model.Cocktail
import com.anhhoang.tipple.core.data.model.Ingredient
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

private fun NetworkCocktail.getIngredients(): List<Ingredient> = buildList {
    ingredient1?.let { add(Ingredient(it, measure1)) }
    ingredient2?.let { add(Ingredient(it, measure2)) }
    ingredient3?.let { add(Ingredient(it, measure3)) }
    ingredient4?.let { add(Ingredient(it, measure4)) }
    ingredient5?.let { add(Ingredient(it, measure5)) }
    ingredient6?.let { add(Ingredient(it, measure6)) }
    ingredient7?.let { add(Ingredient(it, measure7)) }
    ingredient8?.let { add(Ingredient(it, measure8)) }
    ingredient9?.let { add(Ingredient(it, measure9)) }
    ingredient10?.let { add(Ingredient(it, measure10)) }
    ingredient11?.let { add(Ingredient(it, measure11)) }
    ingredient12?.let { add(Ingredient(it, measure12)) }
    ingredient13?.let { add(Ingredient(it, measure13)) }
    ingredient14?.let { add(Ingredient(it, measure14)) }
    ingredient15?.let { add(Ingredient(it, measure15)) }
}