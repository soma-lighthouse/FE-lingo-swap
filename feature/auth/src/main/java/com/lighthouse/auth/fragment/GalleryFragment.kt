package com.lighthouse.auth.fragment

import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.lighthouse.android.common_ui.base.BindingFragment
import com.lighthouse.auth.R
import com.lighthouse.auth.databinding.FragmentGalleryBinding
import com.lighthouse.auth.util.padWithDisplayCutout
import com.lighthouse.auth.util.showImmersive
import java.io.File
import java.util.Locale

class GalleryFragment internal constructor() :
    BindingFragment<FragmentGalleryBinding>(R.layout.fragment_gallery) {

    private val args: GalleryFragmentArgs by navArgs()
    private lateinit var mediaList: MutableList<File>

    inner class MediaPagerAdapter : FragmentStateAdapter(this) {
        override fun getItemCount(): Int = mediaList.size

        override fun createFragment(position: Int): Fragment {
            return PhotoFragment.create(mediaList[position])
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val rootDirectory = File(args.rootDirectory)
        mediaList = rootDirectory.listFiles { file ->
            EXTENSION_WHITELIST.contains(file.extension.uppercase(Locale.ROOT))
        }?.sortedDescending()?.toMutableList() ?: mutableListOf()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (mediaList.isEmpty()) {
            view.findViewById<ImageButton>(R.id.delete_button).isEnabled = false
        }
        binding.photoViewPager.apply {
            offscreenPageLimit = 2
            adapter = MediaPagerAdapter()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            binding.cutoutSafeArea.padWithDisplayCutout()
        }

        binding.backButton.setOnClickListener {
            Navigation.findNavController(requireActivity(), R.id.fragment_container).navigateUp()
        }

        binding.deleteButton.setOnClickListener {
            mediaList.getOrNull(binding.photoViewPager.currentItem)?.let { mediaFile ->
                AlertDialog.Builder(view.context, android.R.style.Theme_Material_Dialog)
                    .setTitle(getString(com.lighthouse.android.common_ui.R.string.delete_title))
                    .setMessage(getString(com.lighthouse.android.common_ui.R.string.delete_dialog))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        mediaFile.delete()
                        MediaScannerConnection.scanFile(
                            view.context,
                            arrayOf(mediaFile.absolutePath),
                            null,
                            null
                        )
                        mediaList.removeAt(binding.photoViewPager.currentItem)
                        binding.photoViewPager.adapter?.notifyDataSetChanged()
                        // If all photos have been deleted, return to camera
                        if (mediaList.isEmpty()) {
                            Navigation.findNavController(requireActivity(), R.id.fragment_container)
                                .navigateUp()
                        }
                    }
                    .setNegativeButton(android.R.string.cancel, null)
                    .create().showImmersive()
            }
        }
    }
}