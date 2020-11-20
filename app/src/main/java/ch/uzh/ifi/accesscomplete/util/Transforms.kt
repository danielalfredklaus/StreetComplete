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

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator


data class Transforms(
    val translationX: Float,
    val translationY: Float,
    val scaleX: Float,
    val scaleY: Float,
    val rotation: Float,
    val rotationX: Float,
    val rotationY: Float
) {
    constructor(view: View) : this(view.translationX, view.translationY, view.scaleX, view.scaleY, view.rotation, view.rotationX, view.rotationY)

    companion object {
        val IDENTITY = Transforms(0f,0f, 1f, 1f, 0f, 0f, 0f)
    }
}

fun View.getTransformationsTo(other: View, commonParent: ViewGroup): Transforms {
    val otherRect = Rect()
    other.getHitRect(otherRect)
    commonParent.offsetDescendantRectToMyCoords(other, otherRect)
    commonParent.offsetRectIntoDescendantCoords(this, otherRect)
    val rect = Rect()
    getHitRect(rect)

    val scaleX = otherRect.width().toFloat() / rect.width().toFloat()
    val scaleY = otherRect.height().toFloat() / rect.height().toFloat()
    val posX = otherRect.left - (1f - otherRect.width().toFloat() / rect.width().toFloat()) * pivotX
    val posY = otherRect.top - (1f - otherRect.height().toFloat() / rect.height().toFloat()) * pivotY
    val rotation = (other.globalRotation - globalRotation).normalizeDegrees(-180f)
    val rotationX = (other.globalRotationX - globalRotationX).normalizeDegrees(-180f)
    val rotationY = (other.globalRotationY - globalRotationY).normalizeDegrees(-180f)

    return Transforms(posX, posY, scaleX, scaleY, rotation, rotationX, rotationY)
}

val View.globalRotation: Float get() =
    rotation + parents.sumByDouble { it.rotation.toDouble() }.toFloat()

val View.globalRotationX: Float get() =
    rotationX + parents.sumByDouble { it.rotationX.toDouble() }.toFloat()

val View.globalRotationY: Float get() =
    rotationY + parents.sumByDouble { it.rotationY.toDouble() }.toFloat()

private val View.parents: Iterable<ViewGroup> get() = object : Iterable<ViewGroup> {
    override fun iterator(): Iterator<ViewGroup> = iterator<ViewGroup> {
        var p = parent
        while (p != null && p is ViewGroup) {
            yield(p)
            p = p.parent
        }
    }
}

fun View.applyTransforms(transforms: Transforms) {
    translationX = transforms.translationX
    translationY = transforms.translationY
    scaleX = transforms.scaleX
    scaleY = transforms.scaleY
    rotation = transforms.rotation
    rotationX = transforms.rotationX
    rotationY = transforms.rotationY
}

fun ViewPropertyAnimator.transforms(transforms: Transforms): ViewPropertyAnimator {
    translationX(transforms.translationX)
    translationY(transforms.translationY)
    scaleX(transforms.scaleX)
    scaleY(transforms.scaleY)
    rotation(transforms.rotation)
    rotationX(transforms.rotationX)
    rotationY(transforms.rotationY)
    return this
}

fun View.animateFrom(other: View, commonParent: ViewGroup): ViewPropertyAnimator {
    val currentTransforms = Transforms(this)
    val transforms = getTransformationsTo(other, commonParent)
    applyTransforms(transforms)
    return animate().transforms(currentTransforms)
}

fun View.animateTo(other: View, commonParent: ViewGroup): ViewPropertyAnimator {
    val transforms = getTransformationsTo(other, commonParent)
    return animate().transforms(transforms)
}
