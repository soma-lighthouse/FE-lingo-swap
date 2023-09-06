package com.lighthouse.profile.view

import android.net.Uri
import android.os.Bundle
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
import com.lighthouse.profile.databinding.FragmentEditBinding
import com.lighthouse.profile.viewmodel.ProfileViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class EditFragment : BindingFragment<FragmentEditBinding>(R.layout.fragment_edit),
    ImagePickerDialog.CameraDialogListener {
    private val viewModel: ProfileViewModel by activityViewModels()
    private lateinit var adapter: SimpleListAdapter<InterestVO, InterestListTileBinding>
    private lateinit var imagePicker: ImagePickerDialog

    private var interestList = listOf(
        InterestVO("여행", listOf("해변", "도시 여행"))
    )

    private var selectedCountryName = mutableListOf<String>()
    private var selectedCountryCode = mutableListOf<String>()

    private var languageList = listOf<LanguageVO>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBack()
        initAdapter()
        initProfile()
        initSave()
        initCamera()
        observeImage()
        initInterest()
        initCountry()
        initLanguage()
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
            findNavController().navigate(DetailProfileFragmentDirections.actionDetailFragmentToEditFragment())
        }
    }

    private fun initCamera() {
        binding.ivProfileImg.setOnClickListener {
            getImagePicker()
        }
    }

    private fun initBack() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
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

    private fun render(uiState: UiState) {
        when (uiState) {
            is UiState.Loading -> {
                binding.pbDetailLoading.setVisible()
                binding.group1.setGone()
            }

            is UiState.Success<*> -> {
                binding.group1.setVisible()
                val result = uiState.data as ProfileVO
                initView(result)
                initList(result)
                initChip(binding.chipCountry, result.countries)
                initChip(binding.chipLanguage, result.languages.flatMap {
                    listOf("${it.name}/LV${it.level}")
                })
                adapter.submitList(result.interests)
                binding.pbDetailLoading.setGone()
            }

            is UiState.Error<*> -> {
                context.toast(uiState.message.toString())
                binding.pbDetailLoading.setGone()
            }
        }
    }

    private fun initList(result: ProfileVO) {
        interestList = result.interests.toMutableList()
        selectedCountryCode = result.countries.toMutableList()
        languageList = result.languages
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
        when (::imagePicker.isInitialized) {
            false -> imagePicker = ImagePickerDialog.newInstance()
            else -> {}
        }
        when (!imagePicker.isAdded) {
            true -> {
                imagePicker.setListener(this)
                imagePicker.show(
                    requireActivity().supportFragmentManager, imagePicker.javaClass.simpleName

                )
            }

            else -> {}
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

    private fun initSave() {
        binding.btnSave.setOnClickListener {
            viewModel.saveUserDetail()
        }
    }

    private fun initAdapter() {
        adapter = makeAdapter()
        val layoutManager = ScrollSpeedLinearLayoutManager(requireContext(), 8f)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvInterest.layoutManager = layoutManager
        binding.rvInterest.adapter = adapter
    }

    private fun initView(profile: ProfileVO) {
        binding.tvNameTitle.text = profile.name
        binding.tvProfileName.text = profile.name
        binding.tvNation.text = profile.region
        binding.tvDescription.setText(profile.description)

        Glide.with(binding.ivProfileImg)
            .load(profile.profileImageUri)
            .placeholder(com.lighthouse.android.common_ui.R.drawable.placeholder)
            .override(calSize(200f))
            .into(binding.ivProfileImg)

        val flag = binding.root.context.resources.getIdentifier(
            profile.region, "drawable", binding.root.context.packageName
        )
        binding.ivFlag.setImageResource(flag)
        binding.ivFlag.layoutParams.width = calSize(Constant.PROFILE_FLAG_SIZE)
        binding.ivFlag.layoutParams.height = calSize(Constant.PROFILE_FLAG_SIZE)
        binding.ivFlag.requestLayout()
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