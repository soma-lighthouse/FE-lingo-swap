package com.lighthouse.auth.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.lighthouse.android.common_ui.base.BindingFragment
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.setVisible
import com.lighthouse.android.common_ui.util.toast
import com.lighthouse.auth.BuildConfig
import com.lighthouse.auth.R
import com.lighthouse.auth.databinding.FragmentLoginBinding
import com.lighthouse.auth.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BindingFragment<FragmentLoginBinding>(R.layout.fragment_login) {
    private val viewModel: AuthViewModel by viewModels()
    private val googleSignInClient: GoogleSignInClient by lazy { getGoogleClient() }

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
                Log.d("TESTING", account.email ?: "")
                viewModel.saveIdToken(idToken)
                getResult.value = null
                viewModel.postGoogleLogin()
            } catch (e: ApiException) {
                Log.d("TESTING", e.stackTraceToString())
            }
        }
    }

    private fun signInListener() {
        binding.btnSignIn.setOnClickListener {
            requestGoogleLogin()
        }
    }

    private fun requestGoogleLogin() {
        googleSignInClient.signOut()
        val signInClient = googleSignInClient.signInIntent
        resultLauncher.launch(signInClient)
    }

    private fun getGoogleClient(): GoogleSignInClient {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope("https://www.googleapis.com/auth/userinfo.email"))
            .requestIdToken(BuildConfig.GOOGLE_CLIENT_ID)
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(requireActivity(), googleSignInOptions)
    }

    private fun checkLoginResult() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.result.collect { result ->
                    when (result) {
                        UiState.Loading -> {
                            binding.pbLogin.setVisible()
                        }

                        is UiState.Success<*> -> {
                            handleLoginSuccess()
                        }

                        is UiState.Error<*> -> {
                            handleLoginFailure()
                            context.toast(result.message.toString())
                        }
                    }
                }
            }
        }
    }

    private fun handleLoginSuccess() {
        mainNavigator.navigateToMain(requireContext())
        requireActivity().finish()
    }

    private fun handleLoginFailure() {
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToInfoFragment())
        googleSignInClient.signOut()
    }
}
