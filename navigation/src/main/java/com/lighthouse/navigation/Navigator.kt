package com.lighthouse.navigation

import androidx.navigation.NavController

class Navigator {
    lateinit var navController: NavController

    fun navigateToFlow(navigationFlow: NavigationFlow) = when (navigationFlow) {
        NavigationFlow.HomeFlow -> navController.navigate(MainNavGraphDirections.actionGlobalHomeFlow())

        is NavigationFlow.ChatFlow -> navController.navigate(
            MainNavGraphDirections.actionGlobalChatFlow(
                navigationFlow.channelUrl, navigationFlow.path, navigationFlow.baseUrl
            )
        )

        NavigationFlow.BoardFlow -> navController.navigate(MainNavGraphDirections.actionGlobalBoardFlow())
        is NavigationFlow.ProfileFlow -> navController.navigate(
            MainNavGraphDirections.actionGlobalProfileFlow(
                navigationFlow.path, navigationFlow.baseUrl
            )
        )
    }
}