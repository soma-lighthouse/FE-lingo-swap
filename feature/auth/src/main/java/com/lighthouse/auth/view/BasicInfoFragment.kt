package com.lighthouse.auth.view

import android.net.Uri
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.lighthouse.android.common_ui.R
import com.lighthouse.android.common_ui.base.BindingFragment
import com.lighthouse.android.common_ui.base.adapter.ItemDiffCallBack
import com.lighthouse.android.common_ui.base.adapter.ScrollSpeedLinearLayoutManager
import com.lighthouse.android.common_ui.base.adapter.SimpleListAdapter
import com.lighthouse.android.common_ui.util.ImageUtils
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.calSize
import com.lighthouse.android.common_ui.util.intentSerializable
import com.lighthouse.android.common_ui.util.isValidBirthday
import com.lighthouse.android.common_ui.util.isValidEmail
import com.lighthouse.android.common_ui.util.onCloseKeyBoard
import com.lighthouse.android.common_ui.util.setGone
import com.lighthouse.auth.databinding.FragmentBasicInfoBinding
import com.lighthouse.auth.databinding.InterestListTileBinding
import com.lighthouse.auth.viewmodel.AuthViewModel
import com.lighthouse.domain.entity.response.vo.CountryVO
import com.lighthouse.domain.entity.response.vo.InterestVO
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File


@AndroidEntryPoint
class BasicInfoFragment :
    BindingFragment<FragmentBasicInfoBinding>(com.lighthouse.auth.R.layout.fragment_basic_info),
    ImagePickerDialog.CameraDialogListener {
    private val viewModel: AuthViewModel by activityViewModels()
    private val interestList = mutableListOf<InterestVO>()
    private lateinit var interestAdapter: SimpleListAdapter<InterestVO, InterestListTileBinding>
    private var imageCapture: ImageCapture? = null
    private var selectedCountry: CountryVO? = null

    //    private lateinit var outputDirectory: File
    private var preview: Preview? = null

    //    private lateinit var cameraExecutor: ExecutorService
    private lateinit var imagePicker: ImagePickerDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideKeyboard()
        initSpinner()
        initInterest()
        initCountry()
        initNext()
        initAdapter()
        observeCountry()
        initChip()
        initCalender()
        initCamera()
        observeImage()
    }

    private fun initCamera() {
        binding.ivCamera.setOnClickListener {
            getImagePicker()
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

    private fun observeImage() {
        getResult.observe(viewLifecycleOwner) {
            val result = Uri.parse(it.data.toString())
            val fileName = getFileExtensionFromUri(result)
            val file = File(fileName)
            try {
                Glide.with(this).load(result).fitCenter()
                    .placeholder(R.drawable.placeholder) // Placeholder image while loading
                    .error(R.drawable.question) // Image to display if loading fails
                    .override(calSize(200f)).into(binding.ivProfileImg)

                viewLifecycleOwner.lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.getPreSignedURL(file.name).collect { url ->
                            when (url) {
                                is UiState.Success<*> -> {
                                    viewModel.registerInfo.profileImage = url.data.toString()
                                    viewModel.profilePath = result.toString()
                                }

                                is UiState.Loading -> {
                                    Log.d("PICTURE", "uploading!")
                                }

                                else -> {
                                    delay(5000)
                                    viewModel.getPreSignedURL(file.name)
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

    private fun getFileExtensionFromUri(uri: Uri): String? {
        return if (uri.scheme == "content") {
            val mimeType = requireContext().contentResolver.getType(uri)
            "$uri.${mimeType?.substringAfterLast('/')}"
        } else {
            uri.toString()
        }
    }

    private fun hideKeyboard() {
        binding.etName.onCloseKeyBoard(requireContext())
        binding.etBirthday.onCloseKeyBoard(requireContext())
        binding.etEmail.onCloseKeyBoard(requireContext())
        binding.etIntroduction.onCloseKeyBoard(requireContext())
    }

    private fun initCalender() {
        binding.btnCalendar.setOnClickListener {
            val datePickerFragment = DatePickerFragment()
            val supportFragment = requireActivity().supportFragmentManager

            supportFragment.setFragmentResultListener(
                "Birthday", viewLifecycleOwner
            ) { resultKey, bundle ->
                if (resultKey == "Birthday") {
                    val date = bundle.getString("Birthday")
                    binding.etBirthday.setText(date.toString())
                }
            }
            datePickerFragment.show(supportFragment, "datePicker")
        }

    }

    private fun initAdapter() {
        interestAdapter = makeAdapter()
        val linearLayoutManager = ScrollSpeedLinearLayoutManager(context, 8f)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvInterest.layoutManager = linearLayoutManager
        binding.rvInterest.adapter = interestAdapter
    }

    private val genderEng = listOf(
        "TMP", "MALE", "FEMALE", "RATHER_NOT_SAY"
    )


    private fun initNext() {
        binding.btnNext.setOnClickListener {
            if (validateInputs()) {
                viewModel.registerInfo.apply {
                    name = binding.etName.text.toString()
                    email = binding.etEmail.text.toString()
                    birthday = binding.etBirthday.text.toString()
                    gender = genderEng[binding.spinnerGender.selectedItemPosition]
                    region = selectedCountry?.code
                    preferredInterests = interestList
                    description = binding.etIntroduction.text.toString()
                }
                findNavController().navigate(BasicInfoFragmentDirections.actionInfoFragmentToLanguageFragment())
            }
        }
    }

    private fun validateInputs(): Boolean {
        val emailIsValid = binding.etEmail.text.toString().isValidEmail()
        setErrorAndBackground(
            binding.etEmail, emailIsValid, getString(R.string.invalid_email)
        )

        val birthdayIsValid = binding.etBirthday.text.toString().isValidBirthday()
        setErrorAndBackground(
            binding.etBirthday, birthdayIsValid, getString(R.string.invalid_birthday)
        )

        val nameIsEmpty = binding.etName.text.toString().isEmpty()
        setErrorAndBackground(
            binding.etName, !nameIsEmpty, getString(R.string.invalid_null)
        )

        val genderIsEmpty =
            binding.spinnerGender.selectedItem.toString() == getString(R.string.gender)
        setErrorAndBackground(
            binding.spinnerGender, !genderIsEmpty, getString(R.string.invalid_null)
        )

        val nationIsEmpty = binding.btnNation.text.toString().isEmpty()
        setErrorAndBackground(
            binding.btnNation, !nationIsEmpty, getString(R.string.invalid_null)
        )

        val interestIsEmpty = interestList.isEmpty()
        setErrorAndBackground(
            binding.collapseInterest, !interestIsEmpty, getString(R.string.invalid_null)
        )

        return emailIsValid && birthdayIsValid && !nameIsEmpty && !genderIsEmpty && !nationIsEmpty && !interestIsEmpty
    }

    private fun setErrorAndBackground(
        view: View,
        isValid: Boolean,
        errorMessage: String,
    ) {
        if (view is EditText) {
            if (!isValid) {
                view.error = errorMessage
                view.setBackgroundResource(R.drawable.error_box)
                view.requestFocus()
            } else {
                view.error = null
                view.setBackgroundResource(R.drawable.edit_box)
            }
        } else {
            if (!isValid) {
                view.setBackgroundResource(R.drawable.error_box)
                view.requestFocus()
            } else {
                view.setBackgroundResource(R.drawable.edit_box)
            }
        }
    }


    private fun initSpinner() {
        val arrayList = arrayListOf(
            resources.getString(R.string.gender),
            resources.getString(R.string.male),
            resources.getString(R.string.female),
            resources.getString(R.string.rather_not_tell)
        )

        val arrayAdapter = ArrayAdapter(
            requireContext(), R.layout.spinner_item, arrayList
        ).apply {
            setDropDownViewResource(com.google.android.material.R.layout.support_simple_spinner_dropdown_item)
        }

        binding.spinnerGender.adapter = arrayAdapter
    }

    private fun initInterest() {
        binding.btnInterest.setOnClickListener {
            val intent = mainNavigator.navigateToInterest(requireContext())
            resultLauncher.launch(intent)
        }
    }

    private fun initCountry() {
        binding.btnNation.setOnClickListener {
            val intent =
                mainNavigator.navigateToCountry(requireContext(), Pair("multiSelect", false))
            resultLauncher.launch(intent)
        }
    }

    private fun observeCountry() {
        getResult.observe(viewLifecycleOwner) {
            var result = it.getSerializableExtra("CountryList") as? List<CountryVO>

            if (result != null) {
                selectedCountry = result.first()
                binding.btnNation.text =
                    result.first().name
            }
        }
    }

    private fun initChip() {
        getResult.observe(viewLifecycleOwner) {
            val result = it.intentSerializable("InterestList", HashMap::class.java)
            if (result != null) {
                binding.tvInterestNull.setGone()
                interestList.clear()
                for ((key, value) in result) {
                    interestList.add(InterestVO(key as String, value as List<String>))
                }

                interestAdapter.submitList(interestList)
            }
        }
    }

    private fun makeAdapter() =
        SimpleListAdapter<InterestVO, InterestListTileBinding>(diffCallBack = ItemDiffCallBack(
            onContentsTheSame = { old, new -> old == new },
            onItemsTheSame = { old, new -> old.category == new.category }),
            layoutId = com.lighthouse.auth.R.layout.interest_list_tile,
            onBindCallback = { viewHolder, item ->
                val binding = viewHolder.binding
                binding.tvInterestTitle.text = item.category
                binding.btnInterest.setOnClickListener { _ ->
                    if (binding.chipInterest.visibility == View.VISIBLE) {
                        TransitionManager.beginDelayedTransition(
                            binding.collapseInterest, AutoTransition()
                        )
                        binding.chipInterest.visibility = View.GONE
                        binding.btnInterest.animate().rotation(0f).start()
                    } else {
                        TransitionManager.beginDelayedTransition(
                            binding.collapseInterest, AutoTransition()
                        )
                        binding.chipInterest.visibility = View.VISIBLE
                        binding.btnInterest.animate().rotation(180f).start()
                    }
                }
                val inflater = LayoutInflater.from(binding.root.context)

                item.interest.forEach {
                    val chip = inflater.inflate(
                        R.layout.home_chip, binding.chipInterest, false
                    ) as Chip

                    chip.text = it
                    chip.isCloseIconVisible = false
                    chip.isCheckable = false
                    binding.chipInterest.addView(chip)
                }
            })

    override fun onDestroy() {
        super.onDestroy()
//        cameraExecutor.shutdown()
    }
}