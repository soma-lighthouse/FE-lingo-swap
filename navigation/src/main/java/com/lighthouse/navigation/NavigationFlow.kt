package com.lighthouse.navigation

sealed class NavigationFlow {
    object HomeFlow : NavigationFlow()
    object ChatFlow : NavigationFlow()
    object BoardFlow : NavigationFlow()
    object ProfileFlow : NavigationFlow()
}