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

package ch.uzh.ifi.accesscomplete.quests.wheelchair_access

import de.westnordost.osmfeatures.FeatureDictionary
import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.OsmFilterQuestType
import ch.uzh.ifi.accesscomplete.data.osm.changes.StringMapChangesBuilder
import java.util.concurrent.FutureTask

class AddWheelchairAccessBusiness(
    private val featureDictionaryFuture: FutureTask<FeatureDictionary>
) : OsmFilterQuestType<String>()
{
    override val elementFilter = """
        nodes, ways, relations with
        (
         shop and shop !~ no|vacant
         or amenity = parking and parking = multi-storey
         or amenity = recycling and recycling_type = centre
         or tourism = information and information = office
         or """.trimIndent() +

        // The common list is shared by the name quest, the opening hours quest and the wheelchair quest.
        // So when adding other tags to the common list keep in mind that they need to be appropriate for all those quests.
        // Independent tags can by added in the "wheelchair only" tab.

        mapOf(
            "amenity" to arrayOf(
                // common
                "restaurant", "cafe", "ice_cream", "fast_food", "bar", "pub", "biergarten", "food_court", "nightclub", // eat & drink
                "cinema", "planetarium", "casino",                                                                     // amenities
                "townhall", "courthouse", "embassy", "community_centre", "youth_centre", "library",                    // civic
                "bank", "bureau_de_change", "money_transfer", "post_office", "marketplace", "internet_cafe",           // commercial
                "car_wash", "car_rental", "fuel",                                                                      // car stuff
                "dentist", "doctors", "clinic", "pharmacy", "veterinary",                                              // health
                "animal_boarding", "animal_shelter", "animal_breeding",                                                // animals

                // name & wheelchair only
                "theatre",                             // culture
                "conference_centre", "arts_centre",    // events
                "police", "ranger_station",            // civic
                "ferry_terminal",                      // transport
                "place_of_worship",                    // religious
                "hospital"                             // health care
            ),
            "tourism" to arrayOf(
                // common
                "zoo", "aquarium", "theme_park", "gallery", "museum",

                // name & wheelchair
                "attraction",
                "hotel", "guest_house", "motel", "hostel", "alpine_hut", "apartment", "resort", "camp_site", "caravan_site", "chalet", // accommodations

                // wheelchair only
                "viewpoint"

                // and tourism = information, see above
            ),
            "leisure" to arrayOf(
                // common
                "fitness_centre", "golf_course", "water_park", "miniature_golf", "bowling_alley",
                "amusement_arcade", "adult_gaming_centre", "tanning_salon",

                // name & wheelchair
                "sports_centre", "stadium", "marina"
            ),
            "office" to arrayOf(
                // common
                "insurance", "government", "travel_agent", "tax_advisor", "religion", "employment_agency",

                // name & wheelchair
                "lawyer", "estate_agent", "political_party", "therapist"
            ),
            "craft" to arrayOf(
                // common
                "carpenter", "shoemaker", "tailor", "photographer", "dressmaker",
                "electronics_repair", "key_cutter", "stonemason",

                // name & wheelchair
                "winery"
            )
        ).map { it.key + " ~ " + it.value.joinToString("|") }.joinToString("\n or ") +
        "\n) and !wheelchair and name"

    override val commitMessage = "Add wheelchair access"
    override val wikiLink = "Key:wheelchair"
    override val icon = R.drawable.ic_quest_wheelchair_shop
    override val defaultDisabledMessage = R.string.default_disabled_msg_go_inside

    override fun getTitle(tags: Map<String, String>) =
        if (hasFeatureName(tags) && !tags.containsKey("brand"))
            R.string.quest_wheelchairAccess_name_type_title
        else
            R.string.quest_wheelchairAccess_name_title

    override fun getTitleArgs(tags: Map<String, String>, featureName: Lazy<String?>): Array<String> {
        val name = tags["name"] ?: tags["brand"]
        return if (name != null) arrayOf(name,featureName.value.toString()) else arrayOf()
    }

    override fun createForm() = AddWheelchairAccessBusinessForm()

    override fun applyAnswerTo(answer: String, changes: StringMapChangesBuilder) {
        changes.add("wheelchair", answer)
    }

    private fun hasFeatureName(tags: Map<String, String>?): Boolean =
        tags?.let { featureDictionaryFuture.get().byTags(it).find().isNotEmpty() } ?: false
}
