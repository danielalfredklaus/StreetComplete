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

package ch.uzh.ifi.accesscomplete.view

import android.content.Context
import androidx.appcompat.R
import android.text.Editable
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatEditText


import ch.uzh.ifi.accesscomplete.data.meta.Abbreviations
import ch.uzh.ifi.accesscomplete.util.DefaultTextWatcher

/** An edit text that expands abbreviations automatically when finishing a word (via space, "-" or
 * ".") and capitalizes the first letter of each word that is longer than 3 letters.  */
class AutoCorrectAbbreviationsEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.editTextStyle)
    : AppCompatEditText(context, attrs, defStyleAttr) {

    var abbreviations: Abbreviations? = null

    init {
        imeOptions = EditorInfo.IME_ACTION_DONE or imeOptions

        inputType =
            EditorInfo.TYPE_CLASS_TEXT or
            EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS or
            EditorInfo.TYPE_TEXT_FLAG_CAP_SENTENCES

        addTextChangedListener(AbbreviationAutoCorrecter())

        setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                text?.let { autoCorrectTextAt(it, length()) }
            }
            false
        }
    }

    private fun autoCorrectTextAt(s: Editable, cursor: Int) {
        val abbrs = abbreviations ?: return

        val textToCursor = s.subSequence(0, cursor).trim().toString()
        val words = textToCursor.split("[ -]+".toRegex())
        // really no idea how the result of split can ever be empty, but it apparently happens sometimes #287
        if (words.isEmpty()) return
        val lastWordBeforeCursor = words[words.size - 1]

        val isFirstWord = words.size == 1
        val isLastWord = cursor == s.length

        val replacement = abbrs.getExpansion(lastWordBeforeCursor, isFirstWord, isLastWord)

        val wordStart = textToCursor.indexOf(lastWordBeforeCursor)
        if (replacement != null) {
            fixedReplace(s, wordStart, wordStart + lastWordBeforeCursor.length, replacement)
        } else if (lastWordBeforeCursor.length > 3) {
            val locale = abbrs.locale
            val capital = lastWordBeforeCursor.substring(0, 1).toUpperCase(locale)
            s.replace(wordStart, wordStart + 1, capital)
        }
    }

    private fun fixedReplace(s: Editable, replaceStart: Int, replaceEnd: Int, replaceWith: CharSequence) {
        // if I only call s.replace to replace the abbreviations with their respective expansions,
        // the caret seems to get confused. On my Android API level 19, if i.e. an abbreviation of
        // two letters is replaced with a word with ten letters, then I can not edit/delete the first
        // eight letters of the edit text anymore.
        // This method re-sets the text completely, so the caret and text length are also set anew

        val selEnd = selectionEnd
        text = s.replace(replaceStart, replaceEnd, replaceWith)
        val replaceLength = replaceEnd - replaceStart
        val addedCharacters = replaceWith.length - replaceLength
        setSelection(selEnd + addedCharacters)
    }

    private inner class AbbreviationAutoCorrecter : DefaultTextWatcher() {
        private var cursorPos: Int = 0
        private var lastTextLength: Int = 0
        private var addedText: Boolean = false

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            cursorPos = start + count
            addedText = lastTextLength < s.length
            lastTextLength = s.length
        }

        override fun afterTextChanged(s: Editable) {
            if (addedText) {
                val endedWord = s.subSequence(0, cursorPos).toString().matches(".+[ -.]+$".toRegex())
                if (endedWord) {
                    autoCorrectTextAt(s, cursorPos)
                }
            }
        }
    }
}
