package com.lighthouse.auth.view

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.lighthouse.auth.R
import com.lighthouse.auth.util.hasPermissions

private const val PERMISSIONS_REQUEST_CODE = 10
val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.CAMERA)

class PermissionsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (hasPermissions(requireContext())) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            Navigation.findNavController(
                requireActivity(),
                R.id.fragment_container
            ).navigate(
                PermissionsFragmentDirections.actionPermissionsToCamera()
            )
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Toast.makeText(
                    requireContext(),
                    "Permission request granted",
                    Toast.LENGTH_LONG
                ).show()
                Navigation.findNavController(
                    requireActivity(),
                    R.id.fragment_container
                ).navigate(
                    PermissionsFragmentDirections.actionPermissionsToCamera()
                )
            } else {
                Toast.makeText(
                    requireContext(),
                    "Permission request denied",
                    Toast.LENGTH_LONG
                ).show()
            }
        }


}