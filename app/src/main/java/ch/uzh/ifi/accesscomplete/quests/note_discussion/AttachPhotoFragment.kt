/*
 * AccessComplete, an easy to use editor of accessibility related
 * OpenStreetMap data for Android.  This program is a fork of
 * StreetComplete (https://github.com/westnordost/StreetComplete).
 *
 * Copyright (C) 2016-2020 Tobias Zwick and contributors (StreetComplete authors)
 * Copyright (C) 2020 Sven Stoll (AccessComplete author)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ch.uzh.ifi.accesscomplete.quests.note_discussion

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.uzh.ifi.accesscomplete.ApplicationConstants.*
import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.data.osmnotes.deleteImages
import ch.uzh.ifi.accesscomplete.ktx.toast
import ch.uzh.ifi.accesscomplete.util.AdapterDataChangedWatcher
import ch.uzh.ifi.accesscomplete.util.decodeScaledBitmapAndNormalize
import kotlinx.android.synthetic.main.fragment_attach_photo.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class AttachPhotoFragment : Fragment() {

    val imagePaths: List<String> get() = noteImageAdapter.list
    private var photosListView: RecyclerView? = null
    private var hintView: TextView? = null

    private var currentImagePath: String? = null

    private lateinit var noteImageAdapter: NoteImageAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_attach_photo, container, false)

        val hasCamera = requireActivity().packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
        if (!hasCamera) {
            view.visibility = View.GONE
        }
        photosListView = view.findViewById(R.id.gridView)
        hintView = view.findViewById(R.id.photosAreUsefulExplanation)
        return view
    }

    private fun updateHintVisibility() {
        val isImagePathsEmpty = imagePaths.isEmpty()
        photosListView?.isGone = isImagePathsEmpty
        hintView?.isGone = !isImagePathsEmpty
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        takePhotoButton.setOnClickListener { takePhoto() }

        val paths: ArrayList<String>
        if (savedInstanceState != null) {
            paths = savedInstanceState.getStringArrayList(PHOTO_PATHS)!!
            currentImagePath = savedInstanceState.getString(CURRENT_PHOTO_PATH)
        } else {
            paths = ArrayList()
            currentImagePath = null
        }

        noteImageAdapter = NoteImageAdapter(paths, requireContext())
        gridView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        gridView.adapter = noteImageAdapter
        noteImageAdapter.registerAdapterDataObserver(AdapterDataChangedWatcher { updateHintVisibility() })
        updateHintVisibility()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArrayList(PHOTO_PATHS, ArrayList(imagePaths))
        outState.putString(CURRENT_PHOTO_PATH, currentImagePath)
    }

    private fun takePhoto() {
        if (!requestCameraPermission()) {
            AlertDialog.Builder(requireContext())
                .setMessage(R.string.camera_permission_warning)
                .setPositiveButton(R.string.retry) { _, _ -> requestCameraPermission() }
                .setCancelable(true)
                .show()
            return
        }

        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        activity?.packageManager?.let { packageManager ->
            if (takePhotoIntent.resolveActivity(packageManager) != null) {
                try {
                    val photoFile = createImageFile()
                    //Use FileProvider for getting the content:// URI, see: https://developer.android.com/training/camera/photobasics.html#TaskPath
                    val photoUri = FileProvider.getUriForFile(requireContext(), getString(R.string.fileprovider_authority), photoFile)
                    currentImagePath = photoFile.path
                    takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    startActivityForResult(takePhotoIntent, REQUEST_TAKE_PHOTO)
                } catch (e: IOException) {
                    Log.e(TAG, "Unable to create file for photo", e)
                    context?.toast(R.string.quest_leave_new_note_create_image_error)
                } catch (e: IllegalArgumentException) {
                    Log.e(TAG, "Unable to create file for photo", e)
                    context?.toast(R.string.quest_leave_new_note_create_image_error)
                }
            }
        }
    }

    private fun requestCameraPermission(): Boolean {
        val permissionStatus = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
        return if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            true
        } else {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
            false
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode != REQUEST_CAMERA_PERMISSION) {
            return
        }

        if (permissions.firstOrNull() != Manifest.permission.CAMERA) {
            return
        }

        if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
            takePhoto()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                try {
                    val path = currentImagePath!!
                    val bitmap = decodeScaledBitmapAndNormalize(path, ATTACH_PHOTO_MAXWIDTH, ATTACH_PHOTO_MAXHEIGHT)
                        ?: throw IOException()
                    val out = FileOutputStream(path)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, ATTACH_PHOTO_QUALITY, out)

                    noteImageAdapter.list.add(path)
                    noteImageAdapter.notifyItemInserted(imagePaths.size - 1)
                } catch (e: IOException) {
                    Log.e(TAG, "Unable to rescale the photo", e)
                    context?.toast(R.string.quest_leave_new_note_create_image_error)
                    removeCurrentImage()
                }

            } else {
                removeCurrentImage()
            }
            currentImagePath = null
        }
    }

    private fun removeCurrentImage() {
        currentImagePath?.let {
            val photoFile = File(it)
            if (photoFile.exists()) {
                photoFile.delete()
            }
        }
    }

    private fun createImageFile(): File {
        val directory = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFileName = "photo_" + System.currentTimeMillis() + ".jpg"
        val file = File(directory, imageFileName)
        if (!file.createNewFile()) throw IOException("Photo file with exactly the same name already exists")
        return file
    }

    fun deleteImages() {
        deleteImages(imagePaths)
    }

    companion object {

        private const val TAG = "AttachPhotoFragment"
        private const val REQUEST_TAKE_PHOTO = 1
        private const val REQUEST_CAMERA_PERMISSION = 100

        private const val PHOTO_PATHS = "photo_paths"
        private const val CURRENT_PHOTO_PATH = "current_photo_path"
    }
}
