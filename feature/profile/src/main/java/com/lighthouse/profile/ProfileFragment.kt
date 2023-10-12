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
import com.bumptech.glide.Glide
import com.lighthouse.android.common_ui.BR
import com.lighthouse.android.common_ui.base.BindingFragment
import com.lighthouse.android.common_ui.base.MyFirebaseMessagingService
import com.lighthouse.android.common_ui.base.adapter.ItemDiffCallBack
import com.lighthouse.android.common_ui.base.adapter.SimpleListAdapter
import com.lighthouse.android.common_ui.databinding.LanguageTabBinding
import com.lighthouse.android.common_ui.dialog.showOKDialog
import com.lighthouse.android.common_ui.util.Constant
import com.lighthouse.android.common_ui.util.PushUtils
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.calSize
import com.lighthouse.android.common_ui.util.setGone
import com.lighthouse.android.common_ui.util.setVisible
import com.lighthouse.android.common_ui.util.toast
import com.lighthouse.domain.entity.response.vo.ProfileVO
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
        initProfileDetail()
        initProfile()
        initMyQuestions()
        initLogout()
        initToggle()
    }

    private fun initToggle() {
        binding.toggleNotification.isChecked = viewModel.getNotification()

        binding.toggleNotification.setOnCheckedChangeListener { _, b ->
            if (b) {
                PushUtils.registerPushHandler(MyFirebaseMessagingService())
            } else {
                PushUtils.unRegisterPushHandler(object : PushRequestCompleteHandler {
                    override fun onComplete(isRegistered: Boolean, token: String?) {
                        val notification =
                            requireContext().getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                        notification.cancelAll()
                    }

                    override fun onError(e: SendbirdException) {
                        context.toast(e.message.toString())
                        binding.toggleNotification.isChecked = true
                    }
                })
            }

            viewModel.setNotification(b)
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

    private fun initProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getProfileDetail(viewModel.getUUID())
                viewModel.detail.collect {
                    render(it)
                }
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

    private fun render(uiState: UiState) {
        when (uiState) {
            is UiState.Loading -> {
                binding.pbProfile.setVisible()
            }

            is UiState.Success<*> -> {
                val data = uiState.data as ProfileVO
                renderProfile(data)
                binding.pbProfile.setGone()
            }

            is UiState.Error<*> -> {
                handleException(uiState)
                binding.pbProfile.setGone()
            }
        }
    }

    private fun renderProfile(data: ProfileVO) {
        Glide.with(binding.root.context)
            .load(data.profileImageUri)
            .into(binding.ivProfileImg)

        val langAdapter =
            SimpleListAdapter<String, LanguageTabBinding>(diffCallBack = ItemDiffCallBack<String>(
                onContentsTheSame = { old, new -> old == new },
                onItemsTheSame = { old, new -> old == new }),
                layoutId = com.lighthouse.android.common_ui.R.layout.language_tab,
                onBindCallback = { v, s ->
                    val binding = v.binding
                    binding.tvLanguage.text = s
                })

        val languages = data.languages.map {
            "${it.name}/Lv.${it.level}"
        }

        langAdapter.submitList(languages)

        val flag = binding.root.context.resources.getIdentifier(
            data.region.name, "drawable", binding.root.context.packageName
        )

        binding.rvLanguage.adapter = langAdapter

        binding.ivFlag.setImageResource(flag)
        binding.ivFlag.layoutParams.width = calSize(Constant.PROFILE_FLAG_SIZE)
        binding.ivFlag.layoutParams.height = calSize(Constant.PROFILE_FLAG_SIZE)
        binding.ivFlag.requestLayout()

        binding.tvDescription.text = data.description.ifEmpty {
            getString(com.lighthouse.android.common_ui.R.string.profile_description)
        }

        binding.setVariable(BR.item, data)
    }
}