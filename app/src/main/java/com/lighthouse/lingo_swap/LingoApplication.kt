package com.lighthouse.lingo_swap

import android.app.Application
import android.util.Log
import com.lighthouse.android.chats.uikit.CustomFragmentFactory
import com.sendbird.android.SendbirdChat
import com.sendbird.android.exception.SendbirdException
import com.sendbird.android.handler.InitResultHandler
import com.sendbird.android.params.InitParams
import com.sendbird.uikit.SendbirdUIKit
import com.sendbird.uikit.adapter.SendbirdUIKitAdapter
import com.sendbird.uikit.interfaces.UserInfo
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LingoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initSendBirdUI()
        initSendBirdChat()
    }

    private fun initSendBirdChat() {
        SendbirdChat.init(
            InitParams(
                "DC52A85B-A985-4651-AF95-737C9A26CEDD",
                applicationContext,
                useCaching = true
            ),
            object : InitResultHandler {
                override fun onInitFailed(e: SendbirdException) {
                    Log.i(
                        "SendBirdInit",
                        "Called when initialize failed. SDK will still operate properly as if useLocalCaching is set to false."
                    )
                }

                override fun onInitSucceed() {
                    SendbirdChat.connect("ann") { user, e ->
                        if (user != null) {
                            if (e != null) {
                                // Proceed in offline mode with the data stored in the local database.
                                // Later, connection is made automatically.
                                // and can be notified through ConnectionHandler.onReconnectSucceeded().
                            } else {
                            }
                        } else {
                            // Handle error.
                        }
                    }
                    Log.i("SendBirdInit", "Called when initialization is completed.")
                }

                override fun onMigrationStarted() {
                    Log.i("SendBirdInit", "Called when there's an update in Sendbird server.")
                }
            }
        )
    }

    private fun initSendBirdUI() {
        SendbirdUIKit.init(object : SendbirdUIKitAdapter {
            override fun getAppId(): String {
                return "DC52A85B-A985-4651-AF95-737C9A26CEDD"
            }

            override fun getAccessToken(): String {
                return ""
            }

            override fun getUserInfo(): UserInfo {
                return object : UserInfo {
                    override fun getUserId(): String {
                        return "ann"
                    }

                    override fun getNickname(): String {
                        return "안현준"
                    }

                    override fun getProfileUrl(): String {
                        return ""
                    }
                }
            }

            override fun getInitResultHandler(): InitResultHandler {
                return object : InitResultHandler {
                    override fun onInitFailed(e: SendbirdException) {
                        // If DB migration fails, this method is called.
                    }

                    override fun onInitSucceed() {
                        // Init successful
                    }

                    override fun onMigrationStarted() {
                        // If DB migration is successful, this method is called and you can proceed to the next step.
                        // In the sample app, the `LiveData` class notifies you on the initialization progress
                        // And observes the `MutableLiveData<InitState> initState` value in `SplashActivity()`.
                        // If successful, the `LoginActivity` screen
                        // Or the `HomeActivity` screen will show.
                    }
                }
            }
        }, this)

        SendbirdUIKit.setUIKitFragmentFactory(CustomFragmentFactory())
    }
}

