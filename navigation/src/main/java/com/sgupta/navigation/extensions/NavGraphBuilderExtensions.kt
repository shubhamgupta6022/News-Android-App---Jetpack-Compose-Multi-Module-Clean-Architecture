package com.sgupta.navigation.extensions

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.paging.compose.collectAsLazyPagingItems
import com.sgupta.composite.home.HomeScreenViewModel
import com.sgupta.composite.home.NewsHomeScreen
import com.sgupta.composite.home.events.HomeScreenEvents
import com.sgupta.composite.listing.NewsList
import com.sgupta.composite.listing.NewsListViewModel
import com.sgupta.composite.search.SearchScreen
import com.sgupta.composite.search.SearchScreenViewModel
import com.sgupta.composite.search.events.SearchScreenEvents
import com.sgupta.composite.splash.SplashScreen
import com.sgupta.navigation.destinations.Home
import com.sgupta.navigation.destinations.Listing
import com.sgupta.navigation.destinations.Search
import com.sgupta.navigation.destinations.Splash

/**
 * Add splash screen to navigation graph
 */
fun NavGraphBuilder.addSplashScreen(navController: NavController) {
    composable(
        route = Splash.route
    ) {
        SplashScreen(
            onSplashFinished = {
                navController.navigate(Home.route) {
                    popUpTo(Splash.route) { inclusive = true }
                }
            }
        )
    }
}

/**
 * Add home screen to navigation graph
 */
fun NavGraphBuilder.addHomeScreen(navController: NavController) {
    composable(
        route = Home.route
    ) {
        val viewModel = hiltViewModel<HomeScreenViewModel>()

        NewsHomeScreen(
            state = viewModel.states,
            aIAssistantBottomSheetViewState = viewModel.aiAssistantBottomSheetStates,
            onEvent = { event ->
                when (event) {
                    is HomeScreenEvents.CountriesViewAllClicked -> {
                        navController.navigate(
                            Listing(country = event.id).createRoute()
                        )
                    }

                    is HomeScreenEvents.CategoryFilterClicked -> {
                        navController.navigate(
                            Listing(category = event.category).createRoute()
                        )
                    }

                    is HomeScreenEvents.GenerateAiContent -> {
                        viewModel.generateAiAssistantContent(event.prompt)
                    }

                    is HomeScreenEvents.SearchBarClicked -> {
                        navController.navigate(Search.route)
                    }
                }
            }
        )
    }
}

/**
 * Add listing screen to navigation graph
 */
fun NavGraphBuilder.addListingScreen(navController: NavHostController) {
    composable(
        route = Listing().route,
        arguments = Listing().arguments()
    ) { backStackEntry ->
        val destination = remember { Listing.fromNavBackStackEntry(backStackEntry) }

        val title = when {
            destination.country?.isNotEmpty() == true -> when (destination.country) {
                "in" -> "India"
                "us" -> "USA"
                "uk" -> "UK"
                else -> "Country News"
            }

            destination.category?.isNotEmpty() == true -> destination.category
            else -> "News"
        }

        val viewModel =
            hiltViewModel<NewsListViewModel, NewsListViewModel.NewsListViewModelFactory> { factory ->
                factory.create(destination.country, destination.category)
            }

        val newsPagingItems = if (destination.country?.isNotEmpty() == true) {
            viewModel.countryStates?.collectAsLazyPagingItems()
        } else {
            viewModel.categoryState?.collectAsLazyPagingItems()
        }

        NewsList(onBackClick = { navController.popBackStack() }, title, newsPagingItems)
    }
}

/**
 * Add search screen to navigation graph
 */
fun NavGraphBuilder.addSearchScreen(navController: NavHostController) {
    composable(
        route = Search.route
    ) {
        val viewModels = hiltViewModel<SearchScreenViewModel>()
        SearchScreen(
            state = viewModels.states,
            onBackClick = { navController.popBackStack() },
            onEvent = {
                viewModels.onEvent(it)
            }
        )
    }
}