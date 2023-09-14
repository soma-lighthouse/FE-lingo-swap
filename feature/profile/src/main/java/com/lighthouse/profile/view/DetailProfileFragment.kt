package com.lighthouse.profile.view

import android.net.Uri
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.lighthouse.android.common_ui.base.BindingFragment
import com.lighthouse.android.common_ui.base.adapter.ScrollSpeedLinearLayoutManager
import com.lighthouse.android.common_ui.base.adapter.SimpleListAdapter
import com.lighthouse.android.common_ui.base.adapter.makeAdapter
import com.lighthouse.android.common_ui.databinding.InterestListTileBinding
import com.lighthouse.android.common_ui.dialog.ImagePickerDialog
import com.lighthouse.android.common_ui.util.Constant
import com.lighthouse.android.common_ui.util.ImageUtils
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.calSize
import com.lighthouse.android.common_ui.util.setGone
import com.lighthouse.android.common_ui.util.setVisible
import com.lighthouse.android.common_ui.util.toast
import com.lighthouse.domain.entity.response.vo.InterestVO
import com.lighthouse.domain.entity.response.vo.LanguageVO
import com.lighthouse.domain.entity.response.vo.ProfileVO
import com.lighthouse.profile.R
import com.lighthouse.profile.databinding.FragmentDetailProfileBinding
import com.lighthouse.profile.viewmodel.ProfileViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class DetailProfileFragment :
    BindingFragment<FragmentDetailProfileBinding>(R.layout.fragment_detail_profile),
    ImagePickerDialog.CameraDialogListener {
    private val viewModel: ProfileViewModel by activityViewModels()
    private lateinit var adapter: SimpleListAdapter<InterestVO, InterestListTileBinding>
    private var interestList = listOf(
        InterestVO("여행", listOf("해변", "도시 여행"))
    )

    private var selectedCountryName = mutableListOf<String>()
    private var selectedCountryCode = mutableListOf<String>()

    private var languageList = listOf<LanguageVO>()

    private lateinit var imagePicker: ImagePickerDialog
    private lateinit var userProfile: ProfileVO

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initProfile()
        initBack()
        initFold()
        initMenu()
        initAdapter()
        initCountry()
        initLanguage()
        initInterest()
        initSave()
        initCamera()
        observeImage()
    }

    private fun initInterest() {
        binding.clickInterest.setOnClickListener {
            val hash = hashMapOf<String, List<String>>()

            interestList.forEach {
                hash[it.category] = it.interest
            }
            val intent = mainNavigator.navigateToInterest(
                requireContext(),
                Pair("SelectedList", hash),
            )
            resultLauncher.launch(intent)
        }
    }

    private fun initCountry() {
        binding.clickCountry.setOnClickListener {
            val intent =
                mainNavigator.navigateToCountry(
                    requireContext(),
                    Pair("multiSelect", true),
                    Pair("SelectedList", selectedCountryCode)
                )
            resultLauncher.launch(intent)
        }
    }

    private fun initLanguage() {
        binding.clickLanguage.setOnClickListener {
            findNavController().navigate(DetailProfileFragmentDirections.actionDetailFragmentToLanguageFragment())
        }
    }

    private fun initProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getProfileDetail(viewModel.userId)
                viewModel.detail.collect {
                    render(it)
                }
            }
        }
    }

    private fun initBack() {
        binding.btnBack.setOnClickListener {
            requireActivity().finish()
        }
    }

    private fun initSave() {
        binding.btnSave.setOnClickListener {
            viewModel.saveUserDetail()
            binding.profileGroup.setVisible()
        }
    }

    private fun initMenu() {
        binding.tbProfile.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.item_edit -> {
                    context.toast("edit")
                    initEdit()
                    true
                }

                else -> {
                    true
                }
            }
        }
    }

    private fun initEdit() {
        binding.btnEditCountry.setVisible()
        binding.editGroup.setVisible()
        binding.profileGroup.setGone()
    }

    private fun initFold() {
        binding.btnInterest.setOnClickListener { _ ->
            if (binding.cvInterest.visibility == View.VISIBLE) {
                TransitionManager.beginDelayedTransition(binding.collapseInterest, AutoTransition())
                binding.cvInterest.visibility = View.GONE
                binding.btnFoldInterest.animate().rotation(0f).start()
            } else {
                TransitionManager.beginDelayedTransition(binding.collapseInterest, AutoTransition())
                binding.cvInterest.visibility = View.VISIBLE
                binding.btnFoldInterest.animate().rotation(180f).start()
            }
        }
    }

    private fun render(uiState: UiState) {
        when (uiState) {
            is UiState.Loading -> {
                binding.pbDetailLoading.setVisible()
                binding.group1.setGone()
                binding.bottomRectangle.setGone()
                binding.btnSend.setGone()
            }

            is UiState.Success<*> -> {
                binding.group1.setVisible()
                binding.bottomRectangle.setVisible()
                binding.btnSend.setVisible()
                userProfile = uiState.data as ProfileVO
                initView()
                adapter.submitList(userProfile.interests)
                binding.pbDetailLoading.setGone()
            }

            is UiState.Error<*> -> {
                context.toast(uiState.message.toString())
                binding.pbDetailLoading.setGone()
            }
        }
    }

    private fun initChip(chipGroup: ChipGroup, contentList: List<String>) {
        val inflater = LayoutInflater.from(requireContext())
        contentList.forEach {
            val chip = inflater.inflate(
                com.lighthouse.android.common_ui.R.layout.home_chip,
                chipGroup,
                false
            ) as Chip

            chip.text = it
            chip.isCloseIconVisible = false
            chipGroup.addView(chip)
        }
    }

    private fun initAdapter() {
        adapter = makeAdapter()
        val layoutManager = ScrollSpeedLinearLayoutManager(requireContext(), 8f)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvInterest.layoutManager = layoutManager
        binding.rvInterest.adapter = adapter
    }

    private fun initView() {
        binding.item = userProfile

        initChip(binding.chipCountry, userProfile.countries)
        initChip(binding.chipLanguage, userProfile.languages.flatMap {
            listOf("${it.name}/LV${it.level}")
        })

        Glide.with(binding.ivProfileImg)
            .load(userProfile.profileImageUri)
            .placeholder(com.lighthouse.android.common_ui.R.drawable.placeholder)
            .override(calSize(200f))
            .into(binding.ivProfileImg)

        val flag = binding.root.context.resources.getIdentifier(
            userProfile.region, "drawable", binding.root.context.packageName
        )
        binding.ivFlag.setImageResource(flag)
        binding.ivFlag.layoutParams.width = calSize(Constant.PROFILE_FLAG_SIZE)
        binding.ivFlag.layoutParams.height = calSize(Constant.PROFILE_FLAG_SIZE)
        binding.ivFlag.requestLayout()
    }

    private fun initCamera() {
        binding.ivProfileImg.setOnClickListener {
            getImagePicker()
        }
    }

    private fun observeImage() {
        getResult.observe(viewLifecycleOwner) {
            if (it.data != null) {
                val result = Uri.parse(it.data.toString())
                val fileName = getFileExtensionFromUri(result)
                val file = File(fileName)

                val serverFileName = "/${viewModel.getUUID()}/${file.name}"
                try {
                    Glide.with(this).load(result).fitCenter()
                        .placeholder(com.lighthouse.android.common_ui.R.drawable.placeholder) // Placeholder image while loading
                        .error(com.lighthouse.android.common_ui.R.drawable.question) // Image to display if loading fails
                        .override(calSize(200f)).into(binding.ivProfileImg)

                    viewLifecycleOwner.lifecycleScope.launch {
                        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            viewModel.getPreSignedURL(serverFileName)
                            viewModel.detail.collect { url ->
                                when (url) {
                                    is UiState.Success<*> -> {
                                        viewModel.registerInfo.profileImageUri = serverFileName
                                        viewModel.profileUrl = url.data.toString()
                                        viewModel.profilePath = result.toString()
                                    }

                                    is UiState.Loading -> {
                                        Log.d("PICTURE", "uploading!")
                                    }

                                    else -> {
                                        delay(5000)
                                        viewModel.getPreSignedURL(serverFileName)
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("CAMERA ERROR", e.message.toString())
                }
            }
        }
    }

    private fun getImagePicker() {
        if (!::imagePicker.isInitialized) {
            imagePicker = ImagePickerDialog.newInstance()
        }
        if (imagePicker.isAdded) {
            imagePicker.setListener(this)
            imagePicker.show(
                requireActivity().supportFragmentManager, imagePicker.javaClass.simpleName
            )
        }
    }

    private fun getFileExtensionFromUri(uri: Uri): String {
        return if (uri.scheme == "content") {
            val mimeType = requireContext().contentResolver.getType(uri)
            "$uri.${mimeType?.substringAfterLast('/')}"
        } else {
            uri.toString()
        }
    }

    override fun openCamera() {
        val intent = mainNavigator.navigateToCamera(requireContext())
        resultLauncher.launch(intent)
    }

    override fun openGallery() {
        val intent = ImageUtils.newInstance().openGallery(requireActivity())
        resultLauncher.launch(intent)
    }

}