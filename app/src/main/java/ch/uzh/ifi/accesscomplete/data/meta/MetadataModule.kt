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

package ch.uzh.ifi.accesscomplete.data.meta

import android.content.res.AssetManager
import dagger.Module
import dagger.Provides
import de.westnordost.countryboundaries.CountryBoundaries
import de.westnordost.osmfeatures.AndroidFeatureDictionary
import de.westnordost.osmfeatures.FeatureDictionary
import java.util.concurrent.FutureTask
import javax.inject.Singleton

@Module
object MetadataModule {

	@Provides @Singleton fun countryInfos(assetManager: AssetManager, countryBoundaries: FutureTask<CountryBoundaries>): CountryInfos =
        CountryInfos(assetManager, countryBoundaries)

	@Provides @Singleton fun countryBoundariesFuture(assetManager: AssetManager): FutureTask<CountryBoundaries> =
        FutureTask { CountryBoundaries.load(assetManager.open("boundaries.ser")) }

    @Provides @Singleton fun featureDictionaryFuture(assetManager: AssetManager): FutureTask<FeatureDictionary> =
        FutureTask { AndroidFeatureDictionary.create(assetManager, "osmfeatures") }
}
