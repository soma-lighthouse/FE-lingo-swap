package com.lighthouse.profile.view

import android.net.Uri
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.widget.addTextChangedListener
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
import com.lighthouse.android.common_ui.util.UriUtil
import com.lighthouse.android.common_ui.util.calSize
import com.lighthouse.android.common_ui.util.disable
import com.lighthouse.android.common_ui.util.enable
import com.lighthouse.android.common_ui.util.intentSerializable
import com.lighthouse.android.common_ui.util.setGone
import com.lighthouse.android.common_ui.util.setVisible
import com.lighthouse.android.common_ui.util.toast
import com.lighthouse.domain.entity.request.RegisterInfoVO
import com.lighthouse.domain.entity.request.UploadInterestVO
import com.lighthouse.domain.entity.response.vo.ChannelVO
import com.lighthouse.domain.entity.response.vo.ProfileVO
import com.lighthouse.profile.R
import com.lighthouse.profile.databinding.FragmentDetailProfileBinding
import com.lighthouse.profile.viewmodel.ProfileViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

class DetailProfileFragment :
    BindingFragment<FragmentDetailProfileBinding>(R.layout.fragment_detail_profile),
    ImagePickerDialog.CameraDialogListener {
    private val viewModel: ProfileViewModel by activityViewModels()
    private lateinit var adapter: SimpleListAdapter<UploadInterestVO, InterestListTileBinding>

    private lateinit var imagePicker: ImagePickerDialog
    private lateinit var userProfile: ProfileVO

    private lateinit var imageUri: Uri

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
        observeCountryResult()
        observeInterestResult()
        observeLanguageResult()
        observeDescription()
        initImageObserve()
        initStartChatting()
    }

    private fun observeDescription() {
        binding.tvDescription.addTextChangedListener {
            viewModel.description = it.toString()
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        reloadInfo()
    }

    private fun reloadInfo() {
        initChip(binding.chipCountry, viewModel.selectedCountryName)
        adapter.submitList(viewModel.interestList)
        viewModel.imageUri?.let {
            binding.imageUrl = it.toString()
        }
        binding.tvDescription.setText(viewModel.description)
    }

    private fun observeCountryResult() {
        getResult.observe(viewLifecycleOwner) {
            val result =
                it.getStringArrayListExtra("CountryNameList")?.toMutableList() ?: mutableListOf()
            if (result.isNotEmpty()) {
                Log.d("TESTING COUNTRY", result.toString())
                viewModel.selectedCountryName = result
                viewModel.selectedCountryCode =
                    it.getStringArrayListExtra("CountryCodeList")?.toMutableList()
                        ?: mutableListOf()
                initChip(binding.chipCountry, viewModel.selectedCountryName)
            }
        }
    }

    private fun observeInterestResult() {
        getResult.observe(viewLifecycleOwner) {
            val result = it.intentSerializable("InterestList", HashMap::class.java)
            if (result != null) {
                val tmp = mutableListOf<UploadInterestVO>()
                for ((key, value) in result) {
                    tmp.add(UploadInterestVO(key as String, value as List<String>))
                }
                viewModel.interestList = tmp
                Log.d("TESTING", viewModel.interestList.toString())
                observeInterestCode(
                    it.intentSerializable(
                        "InterestListCode",
                        HashMap::class.java
                    )!!
                )
                adapter.submitList(viewModel.interestList)
            }
        }
    }

    private fun observeInterestCode(codes: HashMap<*, *>) {
        val tmp = mutableListOf<UploadInterestVO>()
        for ((key, value) in codes) {
            tmp.add(UploadInterestVO(key as String, value as List<String>))
        }

        viewModel.interestListCode = tmp
    }

    private fun observeLanguageResult() {
        viewModel.languageList.observe(viewLifecycleOwner) { value ->
            Log.d("TESTING LANGUAGE", value.toString())
            initChip(binding.chipLanguage, value.map { c -> "${c.name}/LV${c.level}" })
        }
    }

    private fun initInterest() {
        binding.clickInterest2.setOnClickListener {
            val hash = hashMapOf<String, List<String>>()

            viewModel.interestList.forEach {
                hash[it.category] = it.interests
            }
            Log.d("TESTING INTEREST", hash.toString())
            val intent = mainNavigator.navigateToInterest(
                requireContext(),
                Pair("SelectedList", hash),
            )
            resultLauncher.launch(intent)
        }
    }

    private fun initCountry() {
        binding.clickCountry.setOnClickListener {
            val intent = mainNavigator.navigateToCountry(
                requireContext(),
                Pair("multiSelect", true),
                Pair("SelectedList", viewModel.selectedCountryName)
            )
            resultLauncher.launch(intent)
        }
    }

    private fun initLanguage() {
        binding.clickLanguage.setOnClickListener {
            findNavController().navigate(
                DetailProfileFragmentDirections.actionDetailFragmentToLanguageFragment(
                    Constant.PROFILE
                )
            )
        }
    }


    private fun initProfile() {
        if (!viewModel.isMe) {
            binding.tbProfile.menu.clear()
        } else {
            binding.bottomRectangle.setGone()
            binding.btnStart.setGone()
        }

        if (viewModel.editMode) {
            initEdit()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.detail.collect {
                    render(it)
                }
            }
        }
        if (!viewModel.editMode) {
            viewModel.getProfileDetail(viewModel.userId)
        }
    }

    private fun initBack() {
        binding.btnBack.setOnClickListener {
            requireActivity().finish()
        }
    }

    private fun initImageObserve() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                observeRegister()
            }
        }
    }

    private fun initSave() {
        observeSave()
        binding.btnSave.setOnClickListener {
            if (validateInput()) {
                return@setOnClickListener
            }

            endEditMode()
            if (viewModel.filePath != "") {
                viewModel.uploadImg(viewModel.filePath!!)
            } else {
                viewModel.saveUserDetail()
            }
        }
    }

    private fun observeSave() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.register.collect {
                    if (it is UiState.Success<*> && it.data is Boolean) {
                        context.toast("Success")
                    }
                }
            }
        }
    }

    private suspend fun observeRegister() {
        viewModel.register.drop(1).collect {
            if (it is UiState.Success<*> && it.data is Boolean) {
                viewModel.saveUserDetail()
            }
        }
    }


    private fun endEditMode() {
        binding.editGroup.setGone()
        binding.interestGroup.setGone()
        binding.tbProfile.menu.add(
            0,
            R.id.item_edit,
            0,
            getString(com.lighthouse.android.common_ui.R.string.edit)
        )
        binding.tvDescription.disable()
        binding.interestFoldGroup.setVisible()
        viewModel.editMode = false
    }

    private fun validateInput(): Boolean {
        val interestIsEmpty = viewModel.interestList.isEmpty()
        setErrorAndBackground(
            binding.clickInterest2,
            interestIsEmpty,
        )

        val countryIsEmpty = viewModel.selectedCountryName.isEmpty()
        setErrorAndBackground(
            binding.clickCountry,
            countryIsEmpty,
        )

        val languageIsEmpty = viewModel.languageList.value.isNullOrEmpty()
        setErrorAndBackground(
            binding.clickLanguage,
            languageIsEmpty,
        )

        return interestIsEmpty || countryIsEmpty || languageIsEmpty
    }

    private fun setErrorAndBackground(view: View, isValidate: Boolean) {
        if (isValidate) {
            view.setBackgroundResource(com.lighthouse.android.common_ui.R.drawable.error_box)
            view.requestFocus()
        } else {
            view.setBackgroundResource(com.lighthouse.android.common_ui.R.drawable.edit_box)
        }
    }

    private fun initMenu() {
        binding.tbProfile.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.item_edit -> {
                    viewModel.editMode = true
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
        binding.editGroup.setVisible()
        binding.interestGroup.setVisible()
        binding.tbProfile.menu.clear()
        binding.interestFoldGroup.setGone()
        binding.tvDescription.enable()
    }


    private fun initFold() {
        binding.clickInterest.setOnClickListener { _ ->
            if (binding.cvInterest.visibility == View.VISIBLE) {
                TransitionManager.beginDelayedTransition(
                    binding.collapseInterest,
                    AutoTransition()
                )
                binding.cvInterest.visibility = View.GONE
                binding.btnFoldInterest.animate().rotation(0f).start()
            } else {
                TransitionManager.beginDelayedTransition(
                    binding.collapseInterest,
                    AutoTransition()
                )
                binding.cvInterest.visibility = View.VISIBLE
                binding.btnFoldInterest.animate().rotation(180f).start()
            }
        }
    }

    private fun render(uiState: UiState) {
        when (uiState) {
            is UiState.Loading -> {
                if (::userProfile.isInitialized) {
                    return
                }
                binding.pbDetailLoading.setVisible()
                binding.group1.setGone()
                binding.bottomRectangle.setGone()
                binding.btnStart.setGone()
            }

            is UiState.Success<*> -> {
                if (uiState.data is ProfileVO) {
                    binding.group1.setVisible()
                    userProfile = uiState.data as ProfileVO
                    if (!viewModel.editMode) {
                        viewModel.setList(userProfile)
                    }
                    storePrevInfo(userProfile)
                    initView()
                }
                if (!viewModel.isMe && !viewModel.chat) {
                    binding.bottomRectangle.setVisible()
                    binding.btnStart.setVisible()
                }
                binding.pbDetailLoading.setGone()
            }

            is UiState.Error<*> -> {
                handleException(uiState)
                binding.pbDetailLoading.setGone()
            }
        }
    }

    private fun storePrevInfo(data: ProfileVO) {
        val tmp = RegisterInfoVO(
            uuid = data.id,
            name = data.name,
            region = data.region.code,
            description = data.description,
            profileImageUri = data.profileImageUri,
            preferredCountries = data.countries.map { it.code },
            preferredInterests = data.interests.map {
                UploadInterestVO(it.category.code, it.interests.map { interest ->
                    interest.code
                })
            },
            languages = data.languages.map {
                mapOf("code" to it.code, "level" to it.level)
            }
        )

        viewModel.registerInfo = tmp
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
                                Pair("ChannelId", (it.data as ChannelVO).id)
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

    private fun initChip(chipGroup: ChipGroup, contentList: List<String>) {
        chipGroup.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())
        contentList.forEach {
            val chip = inflater.inflate(
                com.lighthouse.android.common_ui.R.layout.home_chip, chipGroup, false
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
        binding.imageUrl = userProfile.profileImageUri
        initChip(binding.chipCountry, userProfile.countries.map { it.name })
        initChip(binding.chipLanguage, userProfile.languages.flatMap {
            listOf("${it.name}/LV${it.level}")
        })

        adapter.submitList(userProfile.interests.map {
            UploadInterestVO(it.category.name, it.interests.map { interest -> interest.name })
        })

        viewModel.imageUri = Uri.parse(userProfile.profileImageUri)
    }

    private fun initCamera() {
        binding.clickProfile.setOnClickListener {
            getImagePicker()
        }
    }

    private fun observeImage() {
        observePresignUrl()
        getResult.observe(viewLifecycleOwner) {
            if (it.data == null) {
                return@observe
            }
            imageUri = Uri.parse(it.data.toString())
            val contentUri = Uri.parse(imageUri.toString())
            viewModel.filePath = UriUtil.getRealPath(requireContext(), contentUri) ?: ""

            Glide.with(this).load(imageUri).fitCenter()
                .placeholder(com.lighthouse.android.common_ui.R.drawable.placeholder)
                .error(com.lighthouse.android.common_ui.R.drawable.question)
                .override(calSize(200f))
                .into(binding.ivProfileImg)

            viewModel.getPreSignedUrl(getFileName(viewModel.filePath))
        }
    }

    private fun getFileName(uri: String): String {
        if (uri == "") {
            return ""
        }
        val fileName = uri.substringAfterLast("/")
        return "/${viewModel.userId}/${fileName}"
    }

    private fun observePresignUrl() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.upload.drop(1).collect {
                    val serverFileName = getFileName(viewModel.filePath)

                    when (it) {
                        is UiState.Success<*> -> {
                            if (it.data is String) {
                                viewModel.registerInfo.profileImageUri = serverFileName
                                viewModel.profileUrl = it.data.toString()
                                viewModel.profilePath = imageUri.toString()
                            }
                        }

                        is UiState.Loading -> {
                            Log.d("PICTURE", "uploading!")
                        }

                        else -> {
                            repeat(3) {
                                delay(5000)
                                viewModel.getPreSignedUrl(serverFileName)
                            }
                        }
                    }
                }
            }
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
}