package com.lighthouse.lingo_talk

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.messaging.FirebaseMessaging
import com.lighthouse.android.chats.uikit.channel.CustomChannel
import com.lighthouse.android.common_ui.base.BindingActivity
import com.lighthouse.android.common_ui.base.MyFirebaseMessagingService
import com.lighthouse.android.common_ui.util.PushUtils
import com.lighthouse.lingo_talk.databinding.ActivityMainBinding
import com.lighthouse.navigation.NavigationFlow
import com.lighthouse.navigation.Navigator
import com.lighthouse.navigation.ToFlowNavigatable
import com.sendbird.android.SendbirdChat
import com.sendbird.android.exception.SendbirdException
import com.sendbird.android.handler.InitResultHandler
import com.sendbird.android.params.InitParams
import com.sendbird.uikit.SendbirdUIKit
import com.sendbird.uikit.adapter.SendbirdUIKitAdapter
import com.sendbird.uikit.fragments.ChannelFragment
import com.sendbird.uikit.interfaces.UserInfo
import com.sendbird.uikit.interfaces.providers.ChannelFragmentProvider
import com.sendbird.uikit.model.configurations.UIKitConfig
import com.sendbird.uikit.providers.FragmentProviders
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity @Inject constructor() :
    BindingActivity<ActivityMainBinding>(R.layout.activity_main), ToFlowNavigatable {
    @Inject
    lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        initSendBirdUI()
        initSendBirdChat()

        FirebaseMessaging.getInstance().token.addOnSuccessListener { task ->

            SendbirdChat.registerPushToken(task) { status, e ->
                if (e != null) {
                    Log.d("MESSAGING", "onInitSucceed: $e")
                }

                // ...
            }

        }
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController



        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                com.lighthouse.android.home.R.id.homeFragment -> showBottomNavBar()
                com.lighthouse.android.home.R.id.filterFragment -> hideBottomNavBar()
                com.lighthouse.board.R.id.boardFragment -> showBottomNavBar()
                com.lighthouse.board.R.id.addFragment -> hideBottomNavBar()
                com.lighthouse.profile.R.id.myQuestionsFragment -> hideBottomNavBar()
                com.lighthouse.profile.R.id.profileFragment -> showBottomNavBar()
            }
        }

        navigator.navController = navController
        binding.bottomNav.setupWithNavController(navController)


        binding.bottomNav.setOnItemSelectedListener(null)

        var lastClickTime: Long = 0
        val delayMillis = 300 // Set your desired delay in milliseconds

        binding.bottomNav.setOnItemSelectedListener { item ->
            if (SystemClock.elapsedRealtime() - lastClickTime < delayMillis) {
                Log.d("TESTING DELAY", "Too fast!")
                return@setOnItemSelectedListener false
            }

            lastClickTime = SystemClock.elapsedRealtime()
            Log.d("TESTING DELAY", lastClickTime.toString())

            return@setOnItemSelectedListener item.onNavDestinationSelected(navController)
        }
        initChatting()
    }

    private fun initChatting() {
        val new = intent.getBooleanExtra("NewChat", false)
        if (new) {
            navigateToFlow(NavigationFlow.ChatFlow(intent.getStringExtra("ChannelId") ?: ""))
        }

    }

    override fun navigateToFlow(flow: NavigationFlow) {
        navigator.navigateToFlow(flow)
    }

    private fun hideBottomNavBar() {
        binding.bottomNav.visibility = View.GONE
    }

    private fun showBottomNavBar() {
        binding.bottomNav.visibility = View.VISIBLE
    }

    private fun initSendBirdChat() {
        SendbirdChat.init(
            InitParams(
                BuildConfig.SENDBIRD_APPLICATION_ID,
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
                    SendbirdChat.connect(getUUID()) { user, e ->
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
                return BuildConfig.SENDBIRD_APPLICATION_ID
            }

            override fun getAccessToken(): String {
                return ""
            }

            override fun getUserInfo(): UserInfo {
                return object : UserInfo {
                    override fun getUserId(): String {
                        return getUUID()
                    }

                    override fun getNickname(): String {
                        return getUserName()
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
        UIKitConfig.groupChannelConfig.enableTypingIndicator = true
        UIKitConfig.groupChannelConfig.enableReactions = false
        FragmentProviders.channel = ChannelFragmentProvider { url, args ->
            ChannelFragment.Builder(url).withArguments(args).setUseHeader(true)
                .setCustomFragment(CustomChannel()).build()
        }

        PushUtils.registerPushHandler(MyFirebaseMessagingService())
    }
}