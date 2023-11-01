package com.lighthouse.auth.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.lighthouse.android.common_ui.R
import com.lighthouse.android.common_ui.base.BindingFragment
import com.lighthouse.android.common_ui.base.adapter.SimpleListAdapter
import com.lighthouse.android.common_ui.databinding.InterestListTileBinding
import com.lighthouse.android.common_ui.dialog.ImagePickerDialog
import com.lighthouse.android.common_ui.util.ImageUtils
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.UriUtil
import com.lighthouse.android.common_ui.util.calSize
import com.lighthouse.android.common_ui.util.observeOnce
import com.lighthouse.android.common_ui.util.onCloseKeyBoard
import com.lighthouse.auth.databinding.FragmentBasicInfoBinding
import com.lighthouse.auth.view.makeInterestAdapter
import com.lighthouse.auth.viewmodel.AuthViewModel
import com.lighthouse.domain.entity.response.vo.CountryVO
import com.lighthouse.domain.entity.response.vo.InterestVO
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch


@AndroidEntryPoint
class BasicInfoFragment :
    BindingFragment<FragmentBasicInfoBinding>(com.lighthouse.auth.R.layout.fragment_basic_info),
    ImagePickerDialog.CameraDialogListener {
    private val viewModel: AuthViewModel by activityViewModels()
    private lateinit var interestAdapter: SimpleListAdapter<InterestVO, InterestListTileBinding>
    private var selectedCountry: CountryVO? = null
    private lateinit var imagePicker: ImagePickerDialog
    private lateinit var imageUri: Uri


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideKeyboard()
        initSpinner()
        initInterest()
        initCountry()
        initAdapter()
        initNext()
        initCalender()
        initCamera()
        observeImage()
        initBack()
        setUpBinding()
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkCountryUpdate(false)
        viewModel.checkInterestUpdate()
    }

    private fun setUpBinding() {
        binding.viewModel = viewModel
    }


    private fun initBack() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initCamera() {
        binding.colorOverlay.setOnClickListener {
            getImagePicker()
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


    private fun getImagePicker() {
        if (!::imagePicker.isInitialized) {
            imagePicker = ImagePickerDialog.newInstance()
        }
        if (!imagePicker.isAdded) {
            imagePicker.showDialog(requireContext(), this)
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

            Glide.with(this).load(imageUri).fitCenter().placeholder(R.drawable.placeholder)
                .error(R.drawable.question).override(calSize(200f)).into(binding.ivProfileImg)

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
                viewModel.result.drop(1).collect {
                    render(it)
                }
            }
        }
    }

    private suspend fun render(uiState: UiState) {
        val serverFileName = getFileName(viewModel.filePath)
        when (uiState) {
            is UiState.Success<*> -> {
                viewModel.registerInfo.profileImageUri = serverFileName
                viewModel.profileUrl = uiState.data.toString()
                viewModel.profilePath = imageUri.toString()
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
        interestAdapter = makeInterestAdapter(viewModel, false)
        binding.rvInterest.adapter = interestAdapter
        viewModel.selectedInterest.observe(viewLifecycleOwner) {
            interestAdapter.submitList(it)
        }
    }

    private fun initNext() {
        viewModel.changes.observeOnce(viewLifecycleOwner) {
            if (it == -3) {
                findNavController().navigate(BasicInfoFragmentDirections.actionInfoFragmentToLanguageFragment())
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
        binding.clickInterest.setOnClickListener {
            mainNavigator.navigateToInterest(
                requireContext()
            )
        }
    }

    private fun initCountry() {
        binding.btnNation.setOnClickListener {
            mainNavigator.navigateToCountry(
                requireContext(),
                Pair("multiSelect", false),
            )
        }
    }
}
