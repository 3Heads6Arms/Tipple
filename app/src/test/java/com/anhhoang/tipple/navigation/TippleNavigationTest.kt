package com.anhhoang.tipple.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.anhhoang.tipple.MainActivity
import com.anhhoang.tipple.core.coroutines.BlockingContext
import com.anhhoang.tipple.core.coroutines.LightweightContext
import com.anhhoang.tipple.core.coroutines.MainContext
import com.anhhoang.tipple.core.coroutines.di.DispatchersModule
import com.anhhoang.tipple.core.data.di.RepositoryModule
import com.anhhoang.tipple.core.data.model.Cocktail
import com.anhhoang.tipple.core.data.model.Resource
import com.anhhoang.tipple.core.data.repository.TippleRepository
import com.anhhoang.tipple.feature.searchcocktails.SearchCocktailsScreenTestTags.SEARCH_BAR
import com.anhhoang.tipple.feature.searchcocktails.SearchCocktailsScreenTestTags.SEARCH_RESULT
import dagger.Module
import dagger.Provides
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.mockk.coEvery
import io.mockk.mockk
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog

/** Tests for [TippleNavigation]. */
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
class TippleNavigationTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var repository: TippleRepository


    @Before
    fun setUp() {
        ShadowLog.setupLogging()
        hiltRule.inject()
    }

    @Test
    fun testNavigation_default_expectSearchCocktailsScreen() {
        composeRule.onNodeWithTag(SEARCH_BAR).assertIsDisplayed()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testNavigation_navigateToDetailsScreen_expectDetailsScreen() =
        runTest(MockDispatchersModule.testDispatchers) {
            coEvery { repository.searchCocktails(any()) } returns
                    Resource.Success(
                        listOf(
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
                            )
                        )
                    )


            composeRule.onNode(hasSetTextAction()).performTextInput("Mojito")
            advanceUntilIdle()
            composeRule.onNodeWithTag(SEARCH_RESULT).performClick()

            composeRule.onNodeWithTag(SEARCH_BAR).assertDoesNotExist()
        }

}

@TestInstallIn(components = [SingletonComponent::class], replaces = [DispatchersModule::class])
@Module
class MockDispatchersModule {
    @MainContext
    @Provides
    fun provideMainContext(): CoroutineContext = testDispatchers

    @BlockingContext
    @Provides
    fun provideBlockingContext(): CoroutineContext = testDispatchers

    @LightweightContext
    @Provides
    fun provideLightweightContext(): CoroutineContext = testDispatchers


    companion object {
        val testDispatchers = StandardTestDispatcher()
    }
}

@TestInstallIn(components = [SingletonComponent::class], replaces = [RepositoryModule::class])
@Module
class MockRepositoryModule {

    @Provides
    @Singleton
    fun provideTippleRepository(): TippleRepository {
        return mockk(relaxed = true)
    }

}
