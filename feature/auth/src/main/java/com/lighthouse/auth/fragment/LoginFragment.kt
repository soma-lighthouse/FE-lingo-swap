package com.lighthouse.auth.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.lighthouse.android.common_ui.base.BindingFragment
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.setGone
import com.lighthouse.android.common_ui.util.setVisible
import com.lighthouse.auth.R
import com.lighthouse.auth.databinding.FragmentLoginBinding
import com.lighthouse.auth.viewmodel.AuthViewModel
import com.lighthouse.lighthousei18n.I18nManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BindingFragment<FragmentLoginBinding>(R.layout.fragment_login) {
    private val viewModel: AuthViewModel by activityViewModels()
    private val googleSignInClient: GoogleSignInClient by lazy { getGoogleClient() }

    private var login = false

    @Inject
    lateinit var remoteConfig: FirebaseRemoteConfig

    @Inject
    lateinit var i18nManager: I18nManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        signInListener()
        observeSignInResult()
        checkLoginResult()
    }

    private fun observeSignInResult() {
        getResult.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            val task = GoogleSignIn.getSignedInAccountFromIntent(it)
            try {
                val account = task.getResult(ApiException::class.java)

                val idToken = account.idToken ?: ""
                viewModel.saveIdToken(idToken)
                getResult.value = null
                login = true
                viewModel.saveUserInfo(
                    account.displayName ?: "",
                    account.photoUrl.toString(),
                    account.email ?: ""
                )
                viewModel.postGoogleLogin()
            } catch (e: ApiException) {
                Log.d("TESTING", e.stackTraceToString())
            }
        }
    }

    private fun signInListener() {
        binding.signInButton.setSize(SignInButton.SIZE_STANDARD)
        val btn = binding.signInButton.getChildAt(0) as TextView
        btn.text =
            requireContext().getString(com.lighthouse.android.common_ui.R.string.sign_in_google)

        binding.signInButton1.setOnClickListener {
            requestGoogleLogin()
        }
    }

    private fun requestGoogleLogin() {
        googleSignInClient.signOut()
        val signInClient = googleSignInClient.signInIntent
        resultLauncher.launch(signInClient)
    }

    private fun getGoogleClient(): GoogleSignInClient {
        val googleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(remoteConfig.getString("GOOGLE_CLIENT_ID"))
                .requestEmail()
                .requestProfile()
                .build()

        return GoogleSignIn.getClient(requireActivity(), googleSignInOptions)
    }

    private fun checkLoginResult() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.result.collect { result ->
                    Log.d("TESTING LOGIN", result.toString())
                    when (result) {
                        UiState.Loading -> {
                            if (login) {
                                binding.pbLogin.setVisible()
                                login = false
                            }
                        }

                        is UiState.Success<*> -> {
                            handleLoginSuccess()
                            binding.pbLogin.setGone()
                        }

                        is UiState.Error<*> -> {
                            handleLoginFailure()
                            binding.pbLogin.setGone()
                            clearResult()
                            Log.d("TESTING LOGIN", result.toString())
                        }
                    }
                }
            }
        }
    }

    private fun clearResult() {
        viewModel.clearResult()
    }

    private fun handleLoginSuccess() {
        val intent = mainNavigator.navigateToMain(
            requireContext(), Pair("NewChat", false), Pair("ChannelId", ""), Pair("url", "")
        )
        startActivity(intent)
        requireActivity().finish()
    }

    private fun handleLoginFailure() {
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToTermFragment())
        googleSignInClient.signOut()
    }
}
