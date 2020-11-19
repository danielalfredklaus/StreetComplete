package ch.uzh.ifi.countryboundaries

import de.westnordost.accesscomplete.data.quest.AllCountries
import de.westnordost.accesscomplete.data.quest.AllCountriesExcept
import de.westnordost.accesscomplete.data.quest.Countries
import de.westnordost.accesscomplete.data.quest.NoCountriesExcept
import de.westnordost.accesscomplete.ktx.containsAny
import de.westnordost.countryboundaries.CountryBoundaries
import de.westnordost.osmapi.map.data.BoundingBox
import de.westnordost.osmapi.map.data.LatLon

/** Whether the given position is in any of the given countries */
fun CountryBoundaries.isInAny(pos: LatLon, countries: Countries) = when(countries) {
    is AllCountries -> true
    is AllCountriesExcept -> !isInAny(pos, countries.exceptions)
    is NoCountriesExcept -> isInAny(pos, countries.exceptions)
}

/** Whether the given bounding box at least intersects with the given countries */
fun CountryBoundaries.intersects(bbox: BoundingBox, countries: Countries) = when(countries) {
    is AllCountries -> true
    is AllCountriesExcept -> !getContainingIds(bbox).containsAny(countries.exceptions)
    is NoCountriesExcept -> getIntersectingIds(bbox).containsAny(countries.exceptions)
}

fun CountryBoundaries.getContainingIds(bounds: BoundingBox): Set<String> = getContainingIds(
    bounds.minLongitude, bounds.minLatitude, bounds.maxLongitude, bounds.maxLatitude
)

fun CountryBoundaries.getIntersectingIds(bounds: BoundingBox): Set<String> = getIntersectingIds(
    bounds.minLongitude, bounds.minLatitude, bounds.maxLongitude, bounds.maxLatitude
)

fun CountryBoundaries.isInAny(pos: LatLon, ids: Collection<String>) = isInAny(
    pos.longitude, pos.latitude, ids
)

fun CountryBoundaries.isIn(pos: LatLon, id: String) = isIn(pos.longitude, pos.latitude, id)

fun CountryBoundaries.getIds(pos: LatLon): MutableList<String> = getIds(pos.longitude, pos.latitude)

