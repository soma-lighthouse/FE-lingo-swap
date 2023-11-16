package com.lighthouse.navigation

sealed class NavigationFlow {
    object HomeFlow : NavigationFlow()
    data class ChatFlow(
        val channelUrl: String = "",
        val path: String = "",
        val baseUrl: String = ""
    ) : NavigationFlow()

    object BoardFlow : NavigationFlow()
    object ProfileFlow : NavigationFlow()
}