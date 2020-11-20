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

package ch.uzh.ifi.accesscomplete.map

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.mapzen.tangram.SceneUpdate
import ch.uzh.ifi.accesscomplete.BuildConfig
import ch.uzh.ifi.accesscomplete.Prefs
import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.data.quest.QuestTypeRegistry
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.ceil
import kotlin.math.sqrt

/** From all the quest types, creates and saves a sprite sheet of quest type pin icons, provides
 *  the scene updates for tangram to access this sprite sheet  */
@Singleton class TangramQuestSpriteSheet @Inject constructor(
        private val context: Context,
        private val questTypeRegistry: QuestTypeRegistry,
        private val prefs: SharedPreferences
) {
    val sceneUpdates: List<SceneUpdate> by lazy {
        val isSpriteSheetCurrent = prefs.getInt(Prefs.QUEST_SPRITES_VERSION, 0) == BuildConfig.VERSION_CODE

        val spriteSheet =
            if (isSpriteSheetCurrent && !BuildConfig.DEBUG)
                prefs.getString(Prefs.QUEST_SPRITES, "")!!
            else
                createSpritesheet()

        createSceneUpdates(spriteSheet)
    }

    private fun createSpritesheet(): String {
        val questIconResIds = questTypeRegistry.all.map { it.icon }.toSortedSet()
        questIconResIds.add(R.drawable.ic_multi_quest_2)
        questIconResIds.add(R.drawable.ic_multi_quest_3)
        questIconResIds.add(R.drawable.ic_multi_quest_4)
        questIconResIds.add(R.drawable.ic_multi_quest_5)
        questIconResIds.add(R.drawable.ic_multi_quest_6)
        questIconResIds.add(R.drawable.ic_multi_quest_7)
        questIconResIds.add(R.drawable.ic_multi_quest_8)
        questIconResIds.add(R.drawable.ic_multi_quest_9)
        questIconResIds.add(R.drawable.ic_multi_quest_9_and_more)

        val spriteSheetEntries: MutableList<String> = ArrayList(questIconResIds.size)
        val questPin = ContextCompat.getDrawable(context, R.drawable.quest_pin)!!
        val iconSize = questPin.intrinsicWidth
        val questIconSize = 2 * iconSize / 3
        val questIconOffsetX = 56 * iconSize / 192
        val questIconOffsetY = 18 * iconSize / 192
        val sheetSideLength = ceil(sqrt(questIconResIds.size.toDouble())).toInt()
        val bitmapLength = sheetSideLength * iconSize
        val spriteSheet = Bitmap.createBitmap(bitmapLength, bitmapLength, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(spriteSheet)

        for ((i, questIconResId) in questIconResIds.withIndex()) {
            val x = i % sheetSideLength * iconSize
            val y = i / sheetSideLength * iconSize
            questPin.setBounds(x, y, x + iconSize, y + iconSize)
            questPin.draw(canvas)
            val questIcon = ContextCompat.getDrawable(context, questIconResId)!!
            val questX = x + questIconOffsetX
            val questY = y + questIconOffsetY
            questIcon.setBounds(questX, questY, questX + questIconSize, questY + questIconSize)
            questIcon.draw(canvas)
            val questIconName = context.resources.getResourceEntryName(questIconResId)
            spriteSheetEntries.add("$questIconName: [$x,$y,$iconSize,$iconSize]")
        }

        context.deleteFile(QUEST_ICONS_FILE)
        val spriteSheetIconsFile = context.openFileOutput(QUEST_ICONS_FILE, Context.MODE_PRIVATE)
        spriteSheet.compress(Bitmap.CompressFormat.PNG, 0, spriteSheetIconsFile)
        spriteSheetIconsFile.close()

        val questSprites = "{${spriteSheetEntries.joinToString(",")}}"

        prefs.edit {
            putInt(Prefs.QUEST_SPRITES_VERSION, BuildConfig.VERSION_CODE)
            putString(Prefs.QUEST_SPRITES, questSprites)
        }

        return questSprites
    }

    private fun createSceneUpdates(questSprites: String): List<SceneUpdate> = listOf(
        SceneUpdate("textures.quests.url", "file://${context.filesDir}/$QUEST_ICONS_FILE"),
        SceneUpdate("textures.quests.sprites", questSprites)
    )

    companion object {
        private const val QUEST_ICONS_FILE = "quests.png"
    }
}
