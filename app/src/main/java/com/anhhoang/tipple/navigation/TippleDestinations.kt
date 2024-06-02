package com.anhhoang.tipple.navigation

import kotlinx.serialization.Serializable


/** Navigation graph. */
@Serializable
object MainScreen

@Serializable
data class DetailsScreen(val id: Int) {

}