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

package ch.uzh.ifi.accesscomplete.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import java.io.IOException
import kotlin.math.min

/** Create a bitmap that is not larger than the given desired max width or height and that is
 *  rotated according to its EXIF information */
fun decodeScaledBitmapAndNormalize(imagePath: String, desiredMaxWidth: Int, desiredMaxHeight: Int): Bitmap? {
    var (width, height) = getImageSize(imagePath) ?: return null
    val maxWidth = min(width, desiredMaxWidth)
    val maxHeight = min(height, desiredMaxHeight)

    // Calculate the correct inSampleSize/resize value. This helps reduce memory use. It should be a power of 2
    // from: https://stackoverflow.com/questions/477572/android-strange-out-of-memory-issue/823966#823966
    var inSampleSize = 1
    while (width / 2 > maxWidth || height / 2 > maxHeight) {
        width /= 2
        height /= 2
        inSampleSize *= 2
    }

    val desiredScale = maxWidth.toFloat() / width

    // Decode with inSampleSize
    val options = BitmapFactory.Options().also {
        it.inJustDecodeBounds = false
        it.inDither = false
        it.inSampleSize = inSampleSize
        it.inScaled = false
        it.inPreferredConfig = Bitmap.Config.ARGB_8888
    }
    val sampledSrcBitmap = BitmapFactory.decodeFile(imagePath, options)

    // Resize & Rotate
    val matrix = getRotationMatrix(imagePath)
    matrix.postScale(desiredScale, desiredScale)
    val result = Bitmap.createBitmap( sampledSrcBitmap, 0, 0, sampledSrcBitmap.width, sampledSrcBitmap.height, matrix, true)

    if (result != sampledSrcBitmap) {
        sampledSrcBitmap.recycle()
    }
    return result
}

private fun getImageSize(imagePath: String): Size? {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(imagePath, options)
    val width = options.outWidth
    val height = options.outHeight
    if (width <= 0 || height <= 0) return null
    return Size(width, height)
}

private fun getRotationMatrix(imagePath: String): Matrix =
    try {
        ExifInterface(imagePath).rotationMatrix
    } catch (ignore: IOException) {
        Matrix()
    }

private data class Size(val width: Int, val height: Int)

val ExifInterface.rotationMatrix: Matrix
    get() {
        val orientation = getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale(-1f, 1f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                matrix.setRotate(180f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.setRotate(90f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                matrix.setRotate(-90f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(-90f)
        }
        return matrix
    }
