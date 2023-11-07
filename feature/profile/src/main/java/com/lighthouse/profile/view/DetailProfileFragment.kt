package com.lighthouse.profile.view

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.lighthouse.android.common_ui.base.BindingFragment
import com.lighthouse.android.common_ui.base.adapter.SimpleListAdapter
import com.lighthouse.android.common_ui.base.adapter.makeInterestAdapter
import com.lighthouse.android.common_ui.databinding.InterestListTileBinding
import com.lighthouse.android.common_ui.dialog.ImagePickerDialog
import com.lighthouse.android.common_ui.util.ImageUtils
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.UriUtil
import com.lighthouse.domain.entity.response.vo.ChannelVO
import com.lighthouse.domain.entity.response.vo.InterestVO
import com.lighthouse.profile.R
import com.lighthouse.profile.databinding.FragmentDetailProfileBinding
import com.lighthouse.profile.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

class DetailProfileFragment :
    BindingFragment<FragmentDetailProfileBinding>(R.layout.fragment_detail_profile),
    ImagePickerDialog.CameraDialogListener {
    private val viewModel: ProfileViewModel by activityViewModels()
    private lateinit var adapter: SimpleListAdapter<InterestVO, InterestListTileBinding>
    private lateinit var imagePicker: ImagePickerDialog
    private var first: Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        initProfile()
        initBack()
        initAdapter()
        initCountry()
        initInterest()
        initLanguage()
        initCamera()
        observeImage()
        initStartChatting()
        observeError()
    }

    override fun onResume() {
        super.onResume()
        if (!first) {
            viewModel.getDataFromLocal()
        }
    }

    private fun initInterest() {
        binding.clickInterest2.setOnClickListener {
            mainNavigator.navigateToInterest(
                requireContext(),
            )
        }
    }

    private fun initCountry() {
        binding.clickCountry.setOnClickListener {
            mainNavigator.navigateToCountry(
                requireContext(),
                Pair("multiSelect", true),
            )
        }
    }

    private fun initLanguage() {
        binding.clickLanguage.setOnClickListener {
            mainNavigator.navigateToLanguage(
                requireContext(),
                Pair("isRegister", false),
            )
        }
    }


    private fun initProfile() {
        if (!viewModel.isMe.get()) {
            binding.tbProfile.menu.clear()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.detail.collect {
                    render(it)
                }
            }
        }
        if (!viewModel.editMode) {
            viewModel.getProfileDetail()
        }
    }

    private fun initBack() {
        binding.btnBack.setOnClickListener {
            requireActivity().finish()
        }
    }

    private fun render(uiState: UiState) {
        if (uiState is UiState.Error<*>) {
            handleException(uiState)
        } else if (uiState is UiState.Success<*>) {
            if (uiState.data is List<*>) {
                adapter.submitList(uiState.data as List<InterestVO>)
                first = false
            }
        }
    }

    private fun initStartChatting() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.create.collect {
                    Log.d("TESTING COLLECT", it.toString())
                    if (it is UiState.Success<*> && it.data is ChannelVO) {
                        val intent =
                            mainNavigator.navigateToMain(
                                requireContext(),
                                Pair("NewChat", true),
                                Pair("ChannelId", (it.data as ChannelVO).id),
                                Pair("url", "")
                            )
                        startActivity(intent)
                        requireActivity().finish()
                    }
                }
            }
        }
        binding.btnStart.setOnClickListener {
            viewModel.createChannel()
        }
    }

    private fun initAdapter() {
        adapter = makeInterestAdapter(viewModel, false)
        binding.rvInterest.adapter = adapter
    }

    private fun initCamera() {
        binding.clickProfile.setOnClickListener {
            getImagePicker()
        }
    }

    private fun observeImage() {
        getResult.observe(viewLifecycleOwner) {
            if (it.data == null) {
                return@observe
            }
            viewModel.imageUri = Uri.parse(it.data.toString())
            val contentUri = Uri.parse(viewModel.imageUri.toString())
            viewModel.filePath = UriUtil.getRealPath(requireContext(), contentUri) ?: ""

            ImageUtils.newInstance()
                .setImage(binding.ivProfileImg, viewModel.imageUri.toString(), requireContext())

            viewModel.getPreSignedUrl()
        }
    }


    private fun getImagePicker() {
        if (!::imagePicker.isInitialized) {
            imagePicker = ImagePickerDialog.newInstance()
        }
        if (!imagePicker.isAdded) {
            imagePicker.showDialog(requireContext(), this)
        }
    }

    override fun openCamera() {
        val intent = mainNavigator.navigateToCamera(requireContext())
        resultLauncher.launch(intent)
    }

    override fun openGallery() {
        val intent = ImageUtils.newInstance().openGallery()
        resultLauncher.launch(intent)
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