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

package ch.uzh.ifi.accesscomplete.quests.surface

import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.view.image_select.Item
import ch.uzh.ifi.accesscomplete.quests.surface.Surface.*

enum class Surface(val item: Item<String>) {
    ASPHALT       (Item("asphalt",        R.drawable.surface_asphalt,       R.string.quest_surface_value_asphalt)),
    CONCRETE      (Item("concrete",       R.drawable.surface_concrete,      R.string.quest_surface_value_concrete)),
    FINE_GRAVEL   (Item("fine_gravel",    R.drawable.surface_fine_gravel,   R.string.quest_surface_value_fine_gravel)),
    PAVING_STONES (Item("paving_stones",  R.drawable.surface_paving_stones, R.string.quest_surface_value_paving_stones)),
    COMPACTED     (Item("compacted",      R.drawable.surface_compacted,     R.string.quest_surface_value_compacted)),
    DIRT          (Item("dirt",           R.drawable.surface_dirt,          R.string.quest_surface_value_dirt)),
    SETT          (Item("sett",           R.drawable.surface_sett,          R.string.quest_surface_value_sett)),
    // https://forum.openstreetmap.org/viewtopic.php?id=61042
    UNHEWN_COBBLESTONE (Item("unhewn_cobblestone", R.drawable.surface_cobblestone, R.string.quest_surface_value_unhewn_cobblestone)),
    GRASS_PAVER   (Item("grass_paver",    R.drawable.surface_grass_paver,   R.string.quest_surface_value_grass_paver)),
    WOOD          (Item("wood",           R.drawable.surface_wood,          R.string.quest_surface_value_wood)),
    METAL         (Item("metal",          R.drawable.surface_metal,         R.string.quest_surface_value_metal)),
    GRAVEL        (Item("gravel",         R.drawable.surface_gravel,        R.string.quest_surface_value_gravel)),
    PEBBLES       (Item("pebblestone",    R.drawable.surface_pebblestone,   R.string.quest_surface_value_pebblestone)),
    GRASS         (Item("grass",          R.drawable.surface_grass,         R.string.quest_surface_value_grass)),
    SAND          (Item("sand",           R.drawable.surface_sand,          R.string.quest_surface_value_sand));
}

fun List<Surface>.toItems() = this.map { it.item }

val PAVED_SURFACES = listOf(
    ASPHALT, CONCRETE, PAVING_STONES,
    SETT, UNHEWN_COBBLESTONE, GRASS_PAVER,
    WOOD, METAL
)

val UNPAVED_SURFACES = listOf(
    COMPACTED, FINE_GRAVEL, GRAVEL, PEBBLES
)

val GROUND_SURFACES = listOf(
    DIRT, GRASS, SAND
)
