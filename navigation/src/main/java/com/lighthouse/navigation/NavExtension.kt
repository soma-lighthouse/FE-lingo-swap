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
    class FromFilterToLanguageLevel(start: Int = 1) :
        DeepLinkDestination("lingoSwap://languageLevel/startDestination?start=${start}")

    data class FromProfileToMyQuestions(
        val baseUrl: String,
        val remainingPath: String,
    ) : DeepLinkDestination("https://lingoswap.net/profile/myQuestions?remainingPath=${remainingPath}&baseUrl=${baseUrl}")

    data class FromMyQuestionsToLanguage(
        val baseUrl: String,
        val remainingPath: String,
    ) : DeepLinkDestination("https://lingoswap.net/profile/myQuestions/filter?remainingPath=${remainingPath}&baseUrl=${baseUrl}")

    data object Unknown : DeepLinkDestination("lingoSwap://unknown")

}

fun findClassByPath(path: String, baseUrl: String, remainingPath: String): DeepLinkDestination {
    return when {
        path.startsWith("lingoSwap://languageLevel/startDestination") -> DeepLinkDestination.FromFilterToLanguageLevel()
        path == "https://lingoswap.net/profile/myQuestions" -> DeepLinkDestination.FromProfileToMyQuestions(
            baseUrl,
            remainingPath
        )

        path == "https://lingoswap.net/profile/myQuestions/filter" -> DeepLinkDestination.FromMyQuestionsToLanguage(
            baseUrl,
            remainingPath
        )

        else -> DeepLinkDestination.Unknown
    }
}
