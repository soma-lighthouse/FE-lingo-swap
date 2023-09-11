package com.lighthouse.auth.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.lighthouse.android.common_ui.base.BindingFragment
import com.lighthouse.auth.BuildConfig
import com.lighthouse.auth.R
import com.lighthouse.auth.databinding.FragmentLoginBinding
import com.lighthouse.auth.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BindingFragment<FragmentLoginBinding>(R.layout.fragment_login) {
    private val viewModel: AuthViewModel by viewModels()
    private val googleSignInClient: GoogleSignInClient by lazy { getGoogleClient() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        signInListener()
        observeSignInResult()
    }

    private fun observeSignInResult() {
        getResult.observe(viewLifecycleOwner) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it)
            try {
                val account = task.getResult(ApiException::class.java)

                val idToken = account.idToken ?: ""
                Log.d("TESTING", "idToken: $idToken")
                viewModel.saveIdToken(idToken)
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
            .requestIdToken(BuildConfig.GOOGLE_CLIENT_ID)
            .build()

        return GoogleSignIn.getClient(requireActivity(), googleSignInOptions)
    }
}
