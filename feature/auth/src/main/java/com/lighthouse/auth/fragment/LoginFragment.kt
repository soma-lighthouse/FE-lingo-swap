package com.lighthouse.auth.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.lighthouse.android.common_ui.base.BindingFragment
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.toast
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
        testing()
    }

    private fun observeSignInResult() {
        getResult.observe(viewLifecycleOwner) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it)
            try {
                val account = task.getResult(ApiException::class.java)

                val userName = account.givenName
                val id = account.id
                loginSuccess(userName, id)
            } catch (e: ApiException) {
                context.toast(e.toString())
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
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(requireActivity(), googleSignInOptions)
    }

    private fun loginSuccess(userName: String?, id: String?) {
        viewModel.saveUUID(id)
//        viewModel.postLogin(id, )
    }

    private fun testing() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.testing().collect {
                    render(it)
                }
            }
        }
    }

    private fun render(uiState: UiState) {
        when (uiState) {
            UiState.Loading -> {
                context.toast(uiState.toString())
            }

            is UiState.Success<*> -> {
                context.toast(uiState.toString())
            }

            is UiState.Error<*> -> {
                handleException(uiState)
            }
        }
    }
}