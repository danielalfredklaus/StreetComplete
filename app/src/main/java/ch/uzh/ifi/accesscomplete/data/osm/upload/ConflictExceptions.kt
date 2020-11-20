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

package ch.uzh.ifi.accesscomplete.data.osm.upload

open class ConflictException @JvmOverloads constructor(
    message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause)

class ChangesetConflictException @JvmOverloads constructor(
    message: String? = null, cause: Throwable? = null) : ConflictException(message, cause)

open class ElementConflictException @JvmOverloads constructor(
    message: String? = null, cause: Throwable? = null) : ConflictException(message, cause)

/** Element conflict that concern all quests that have something to do with this element */
open class ElementIncompatibleException @JvmOverloads constructor(
    message: String? = null, cause: Throwable? = null) : ElementConflictException(message, cause)

class ElementDeletedException @JvmOverloads constructor(
    message: String? = null, cause: Throwable? = null) : ElementIncompatibleException(message, cause)
