package com.lighthouse.navigation

import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavOptions

fun buildDeepLink(destination: DeepLinkDestination) =
    NavDeepLinkRequest.Builder.fromUri(destination.address.toUri())
        .build()

fun NavController.deepLinkNavigateTo(
    deepLinkDestination: DeepLinkDestination,
    popUpTo: Boolean = false,
) {
    val builder = NavOptions.Builder()

    if (popUpTo) {
        builder.setPopUpTo(graph.startDestinationId, true)
    }
    builder.setEnterAnim(android.R.anim.fade_in)
    builder.setExitAnim(android.R.anim.fade_out)

    navigate(
        buildDeepLink(deepLinkDestination), builder.build()
    )
}

sealed class DeepLinkDestination(val address: String) {
    data class FromDetailToInterest(val isRegister: Boolean) :
        DeepLinkDestination("https://lingoswap.net/interests?isRegister=$isRegister")
}