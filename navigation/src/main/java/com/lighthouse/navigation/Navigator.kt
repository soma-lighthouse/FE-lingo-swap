package com.lighthouse.navigation

import androidx.navigation.NavController

class Navigator {
    lateinit var navController: NavController

    fun navigateToFlow(navigationFlow: NavigationFlow) = when (navigationFlow) {
        NavigationFlow.HomeFlow -> navController.navigate(MainNavGraphDirections.actionGlobalHomeFlow())
        NavigationFlow.ChatFlow -> navController.navigate(MainNavGraphDirections.actionGlobalChatFlow())
        NavigationFlow.BoardFlow -> navController.navigate(MainNavGraphDirections.actionGlobalBoardFlow())
        NavigationFlow.ProfileFlow -> navController.navigate(MainNavGraphDirections.actionGlobalProfileFlow())
    }
}