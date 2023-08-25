package com.lighthouse.auth.view

import android.content.Intent
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.lighthouse.android.common_ui.base.BindingFragment
import com.lighthouse.auth.R
import com.lighthouse.auth.databinding.FragmentGalleryBinding
import com.lighthouse.auth.util.padWithDisplayCutout
import com.lighthouse.auth.util.showImmersive
import de.hdodenhof.circleimageview.BuildConfig
import java.io.File
import java.util.Locale

class GalleryFragment internal constructor() :
    BindingFragment<FragmentGalleryBinding>(R.layout.fragment_gallery) {

    private val args: GalleryFragmentArgs by navArgs()
    private lateinit var mediaList: MutableList<File>

    inner class MediaPagerAdapter(fm: FragmentManager) :
        FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getCount(): Int = mediaList.size
        override fun getItem(position: Int): Fragment = PhotoFragment.create(mediaList[position])
        override fun getItemPosition(obj: Any): Int = POSITION_NONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        val rootDirectory = File(args.rootDirectory)
        mediaList = rootDirectory.listFiles { file ->
            EXTENSION_WHITELIST.contains(file.extension.uppercase(Locale.ROOT))
        }?.sortedDescending()?.toMutableList() ?: mutableListOf()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (mediaList.isEmpty()) {
            view.findViewById<ImageButton>(R.id.delete_button).isEnabled = false
            view.findViewById<ImageButton>(R.id.share_button).isEnabled = false
        }
        binding.photoViewPager.apply {
            offscreenPageLimit = 2
            adapter = MediaPagerAdapter(childFragmentManager)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            binding.cutoutSafeArea.padWithDisplayCutout()
        }

        binding.backButton.setOnClickListener {
            Navigation.findNavController(requireActivity(), R.id.fragment_container).navigateUp()
        }
        binding.shareButton.setOnClickListener {
            mediaList.getOrNull(binding.photoViewPager.currentItem)?.let { mediaFile ->
                val intent = Intent().apply {
                    val mediaType =
                        MimeTypeMap.getSingleton().getMimeTypeFromExtension(mediaFile.extension)
                    val uri = FileProvider.getUriForFile(
                        view.context,
                        BuildConfig.APPLICATION_ID + ".provider",
                        mediaFile
                    )
                    putExtra(Intent.EXTRA_STREAM, uri)
                    type = mediaType
                    action = Intent.ACTION_SEND
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                }
                startActivity(
                    Intent.createChooser(
                        intent,
                        getString(com.lighthouse.android.common_ui.R.string.share_hint)
                    )
                )
            }
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