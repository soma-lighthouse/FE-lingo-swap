package com.lighthouse.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.lighthouse.android.common_ui.base.BindingActivity
import com.lighthouse.android.common_ui.dialog.showBlockingDialog
import com.lighthouse.android.common_ui.dialog.showUpdateDialog
import com.lighthouse.android.common_ui.util.InternetUtil
import com.lighthouse.auth.databinding.ActivityAuthBinding
import com.lighthouse.auth.viewmodel.AuthViewModel
import com.lighthouse.domain.constriant.LoginState
import com.lighthouse.domain.i18n.supportRegions.SupportRegions
import com.lighthouse.lighthousei18n.I18nManager
import com.lighthouse.swm_logging.SWMLogging
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject


@AndroidEntryPoint
class AuthActivity : BindingActivity<ActivityAuthBinding>(R.layout.activity_auth) {
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var loginState: LoginState
    private lateinit var navController: NavController

    @Inject
    lateinit var i18nManager: I18nManager

    @Inject
    lateinit var remoteConfig: FirebaseRemoteConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_auth) as NavHostFragment
        navController = navHostFragment.navController
        checkGooglePlayServices()
        login()
        initI18n()
        initLogging()
        checkRegister()
        viewModel.clearAllData()

        if (!InternetUtil.checkInternetConnection(this)) {
            showBlockingDialog(this)
        }
    }


    private fun checkRegister() {
        viewModel.getLoginStatus()
        viewModel.loginState.observe(this) {
            loginState = it
        }
    }


    private fun login() {
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                return if (::loginState.isInitialized) {
                    if (loginState == LoginState.LOGIN_SUCCESS) {
                        val intentMain =
                            mainNavigator.navigateToMain(
                                this@AuthActivity,
                                Pair("NewChat", false),
                                Pair("ChannelId", ""),
                                Pair("url", intent.getStringExtra("url") ?: "")
                            )
                        checkUpdate(intentMain)
                    }
                    content.viewTreeObserver.removeOnPreDrawListener(this)
                    true
                } else {
                    false
                }
            }
        })
    }

    private fun initLogging() {
        Log.d("LingoApplication", "initLogging: ${remoteConfig.getString("LIGHTHOUSE_BASE_URL")}")

        SWMLogging.init(
            appVersion = BuildConfig.CURRENT_VER,
            osNameAndVersion = "$ANDROID ${android.os.Build.VERSION.SDK_INT}",
            baseUrl = remoteConfig.getString("LOG_SERVER_URL"),
            serverPath = "log",
            token = "",
            modelName = android.os.Build.MODEL
        )
    }

    private fun initI18n() {
        val defaultRegion = Locale.getDefault()

        if (defaultRegion != Locale.KOREA) {
            i18nManager.saveSelectedRegion(SupportRegions.US, defaultRegion)
            i18nManager.saveTimezoneId(TimeZone.getDefault().id)
        } else {
            i18nManager.saveSelectedRegion(SupportRegions.KOREA, Locale.KOREA)
            i18nManager.saveTimezoneId("Asia/Seoul")
        }

        Log.d("i18n", defaultRegion.toString())
    }

    private fun checkGooglePlayServices() {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val status = googleApiAvailability.isGooglePlayServicesAvailable(applicationContext)
        if (status != ConnectionResult.SUCCESS) {
            val dialog = googleApiAvailability.getErrorDialog(this, status, -1)
            dialog?.setOnDismissListener { finish() }
            dialog?.show()
            googleApiAvailability.showErrorNotification(applicationContext, status)
        }
    }

    private fun checkUpdate(intent: Intent) {
        viewModel.error.observe(this) {
            if (it["error"] == "true") {
                showUpdateDialog(
                    this,
                    getString(com.lighthouse.android.common_ui.R.string.update_title),
                    getString(com.lighthouse.android.common_ui.R.string.update_body),
                    remoteConfig.getString("GOOGLE_PLAYSTORE")
                ) {
                    finish()
                }
            } else {
                startActivity(intent)
                finish()
            }
        }
    }

    companion object {
        private const val ANDROID = "Android"
    }
}