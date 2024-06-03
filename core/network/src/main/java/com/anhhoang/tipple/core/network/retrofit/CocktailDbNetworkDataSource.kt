package com.anhhoang.tipple.core.network.retrofit

import com.anhhoang.tipple.core.coroutines.BlockingContext
import com.anhhoang.tipple.core.network.TippleNetworkDataSource
import com.anhhoang.tipple.core.network.model.NetworkCocktail
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Query

/** CocktailDB API network data source implementation. */
@Singleton
class CocktailDbNetworkDataSource @Inject internal constructor(
    private val api: CocktailDbApi,
    @BlockingContext private val blockingContext: CoroutineContext,
) : TippleNetworkDataSource {

    override suspend fun searchCocktails(name: String): List<NetworkCocktail> =
        withContext(blockingContext) {
            api.searchCocktails(name).drinks
        }
}

/** Exact DTO for the search endpoint. */
@Serializable
internal data class SearchCocktailsResponse(
    val drinks: List<NetworkCocktail> = emptyList()
)

/** Endpoints of TheCocktailDb. */
internal interface CocktailDbApi {
    @GET("search.php")
    suspend fun searchCocktails(@Query("s") name: String): SearchCocktailsResponse
}
