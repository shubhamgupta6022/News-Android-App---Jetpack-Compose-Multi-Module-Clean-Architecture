package com.sgupta.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.paging.compose.collectAsLazyPagingItems
import com.sgupta.composite.home.HomeScreenViewModel
import com.sgupta.composite.home.NewsHomeScreen
import com.sgupta.composite.home.events.HomeScreenEvents
import com.sgupta.composite.listing.NewsList
import com.sgupta.composite.listing.NewsListViewModel
import com.sgupta.composite.splash.SplashScreen

@Composable
fun NavigationHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screens.Splash.id) {
        composable(Screens.Splash.id) {
            SplashScreen {
                navController.navigate(Screens.Home.id) {
                    popUpTo(Screens.Splash.id) { inclusive = true } // Remove Splash from backstack
                }
            }
        }
        composable(Screens.Home.id) {
            val viewModel = hiltViewModel<HomeScreenViewModel>()
            NewsHomeScreen(
                state = viewModel.states,
                aIAssistantBottomSheetViewState = viewModel.aiAssistantBottomSheetStates,
                onEvent = {
                    when (it) {
                        is HomeScreenEvents.CountriesViewAllClicked -> {
                            navController.run {
                                navigate(
                                    Screens.Listing.withArgs("country" to it.id)
                                )
                            }
                        }

                        is HomeScreenEvents.CategoryFilterClicked -> {
                            navController.navigate(
                                Screens.Listing.withArgs("category" to it.category)
                            )
                        }

                        is HomeScreenEvents.GenerateAiContent -> {
                            viewModel.generateAiAssistantContent(it.prompt)
                        }
                    }
                }
            )
        }
        composable(
            route = "${Screens.Listing.id}?country={country}&category={category}",
            arguments = listOf(
                navArgument("country") { nullable = true; type = NavType.StringType },
                navArgument("category") { nullable = true; type = NavType.StringType }
            )
        ) { backStackEntry ->
            val country = backStackEntry.arguments?.getString("country")
            val category = backStackEntry.arguments?.getString("category")
            var title: String = "News"
            if (country?.isNotEmpty() == true) {
                title = when (country) {
                    "in" -> "India"
                    "us" -> "USA"
                    "uk" -> "UK"
                    else -> "Country News"
                }
            }
            if (category?.isNotEmpty() == true) {
                title = category
            }
            val viewModel =
                hiltViewModel<NewsListViewModel, NewsListViewModel.NewsListViewModelFactory> { factory ->
                    factory.create(country, category)
                }
            val newsPagingItems = if (country?.isNotEmpty() == true) {
                viewModel.countryStates?.collectAsLazyPagingItems()
            } else {
                viewModel.categoryState?.collectAsLazyPagingItems()
            }
            NewsList(navController, title, newsPagingItems)
        }
    }
}