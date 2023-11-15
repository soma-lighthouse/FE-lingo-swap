package com.lighthouse.profile

import android.app.NotificationManager
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.lighthouse.android.common_ui.base.BindingFragment
import com.lighthouse.android.common_ui.base.MyFirebaseMessagingService
import com.lighthouse.android.common_ui.dialog.showOKDialog
import com.lighthouse.android.common_ui.util.PushUtils
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.toast
import com.lighthouse.profile.databinding.FragmentProfileBinding
import com.lighthouse.profile.viewmodel.ProfileViewModel
import com.sendbird.android.exception.SendbirdException
import com.sendbird.android.handler.PushRequestCompleteHandler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : BindingFragment<FragmentProfileBinding>(R.layout.fragment_profile) {
    private val viewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        initProfileDetail()
        initMyQuestions()
        initLogout()
        initToggle()
        observeError()
    }

    override fun onStart() {
        super.onStart()
        viewModel.getProfileDetail()
    }

    private fun initToggle() {
        binding.toggleNotification.setOnCheckedChangeListener { _, b ->
            viewModel.setNotification(b)
            if (b) {
                PushUtils.registerPushHandler(MyFirebaseMessagingService())
                return@setOnCheckedChangeListener
            }

            PushUtils.unRegisterPushHandler(object : PushRequestCompleteHandler {
                override fun onComplete(isRegistered: Boolean, token: String?) {
                    val notification =
                        requireContext().getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                    notification.cancelAll()
                }

                override fun onError(e: SendbirdException) {
                    viewModel.setNotification(!b)
                    context.toast(e.message.toString())
                }
            })
        }
    }

    private fun initLogout() {
        binding.clickLogout.setOnClickListener {
            showOKDialog(
                requireContext(),
                getString(com.lighthouse.android.common_ui.R.string.logout_title),
                getString(com.lighthouse.android.common_ui.R.string.logout_body)
            ) { d, _ ->
                d.dismiss()
                logout()
            }
        }
    }

    private fun initProfileDetail() {
        binding.cvProfile.setOnClickListener {
            mainNavigator.navigateToProfile(
                requireContext(),
                Pair("userId", viewModel.getUUID()),
                Pair("isMe", true),
                Pair("isChat", false)
            )
        }
    }

    private fun initMyQuestions() {
        binding.clickQuestion.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToMyQuestionsFragment())
        }
    }

    private fun observeError() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.error.collect {
                    if (it is UiState.Error<*>) {
                        handleException(it)
                    }
                }
            }
        }
    }
}