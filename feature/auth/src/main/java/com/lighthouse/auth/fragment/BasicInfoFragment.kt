package com.lighthouse.auth.fragment

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.lighthouse.android.common_ui.R
import com.lighthouse.android.common_ui.base.BindingFragment
import com.lighthouse.android.common_ui.base.adapter.SimpleListAdapter
import com.lighthouse.android.common_ui.databinding.InterestListTileBinding
import com.lighthouse.android.common_ui.dialog.ImagePickerDialog
import com.lighthouse.android.common_ui.util.DateTextWatcher
import com.lighthouse.android.common_ui.util.ImageUtils
import com.lighthouse.android.common_ui.util.UriUtil
import com.lighthouse.android.common_ui.util.calSize
import com.lighthouse.android.common_ui.util.onCloseKeyBoard
import com.lighthouse.auth.databinding.FragmentBasicInfoBinding
import com.lighthouse.auth.viewmodel.AuthViewModel
import com.lighthouse.domain.entity.response.vo.InterestVO
import com.lighthouse.lighthousei18n.I18nManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class BasicInfoFragment :
    BindingFragment<FragmentBasicInfoBinding>(com.lighthouse.auth.R.layout.fragment_basic_info),
    ImagePickerDialog.CameraDialogListener {
    private val viewModel: AuthViewModel by activityViewModels()
    private lateinit var interestAdapter: SimpleListAdapter<InterestVO, InterestListTileBinding>

    @Inject
    lateinit var i18nManager: I18nManager

    private lateinit var imagePicker: ImagePickerDialog
    private lateinit var imageUri: Uri


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideKeyboard()
        initNext()
        initCalender()
        initCamera()
        observeImage()
        initBack()
        initCountry()
        setUpBinding()

        binding.etBirthday.addTextChangedListener(DateTextWatcher(binding.etBirthday))
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
        getResult.observe(viewLifecycleOwner) {
            if (it.data == null) {
                return@observe
            }
            imageUri = Uri.parse(it.data.toString())
            val contentUri = Uri.parse(imageUri.toString())
            viewModel.filePath = UriUtil.getRealPath(requireContext(), contentUri) ?: ""

            Glide.with(this).load(imageUri).fitCenter().placeholder(R.drawable.placeholder)
                .error(R.drawable.question).override(calSize(200f))
                .into(binding.ivProfileImg)
        }
    }

    private fun hideKeyboard() {
        binding.etName.onCloseKeyBoard(requireContext())
        binding.etBirthday.onCloseKeyBoard(requireContext())
    }

    private fun initCalender() {
        binding.btnCalendar.setOnClickListener {
            val datePickerFragment = DatePickerFragment(i18nManager)
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

    private fun initNext() {
        viewModel.changes.observe(viewLifecycleOwner) {
            if (it == -3) {
                findNavController().navigate(BasicInfoFragmentDirections.actionBasicInfoFragmentToCountryFragment())
            }
        }
    }

    private fun initCountry() {
        binding.clickNation.setOnClickListener {
            mainNavigator.navigateToCountry(
                requireContext(), Pair("multiSelect", false)
            )
        }
    }
}
