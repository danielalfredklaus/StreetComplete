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

package ch.uzh.ifi.accesscomplete;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

import java.util.concurrent.FutureTask;

import javax.inject.Inject;

import de.westnordost.countryboundaries.CountryBoundaries;
import de.westnordost.osmfeatures.FeatureDictionary;
import ch.uzh.ifi.accesscomplete.data.download.tiles.DownloadedTilesDao;
import ch.uzh.ifi.accesscomplete.settings.ResurveyIntervalsUpdater;
import ch.uzh.ifi.accesscomplete.util.CrashReportExceptionHandler;

public class AccessCompleteApplication extends Application {

	@Inject
	FutureTask<CountryBoundaries> countryBoundariesFuture;
	@Inject
	FutureTask<FeatureDictionary> featuresDictionaryFuture;
	@Inject
	CrashReportExceptionHandler crashReportExceptionHandler;
	@Inject
	ResurveyIntervalsUpdater resurveyIntervalsUpdater;
	@Inject
	DownloadedTilesDao downloadedTilesDao;
	@Inject
	SharedPreferences prefs;

	private static final String PRELOAD_TAG = "Preload";

	@Override
	public void onCreate() {
		super.onCreate();

		Injector.INSTANCE.initializeApplicationComponent(this);
		Injector.INSTANCE.getApplicationComponent().inject(this);

		crashReportExceptionHandler.install();

		preload();

		Prefs.Theme theme = Prefs.Theme.valueOf(prefs.getString(Prefs.THEME_SELECT, "AUTO"));
		AppCompatDelegate.setDefaultNightMode(theme.appCompatNightMode);

		resurveyIntervalsUpdater.update();

		String lastVersion = prefs.getString(Prefs.LAST_VERSION_DATA, null);
		if (!BuildConfig.VERSION_NAME.equals(lastVersion)) {
			prefs.edit().putString(Prefs.LAST_VERSION_DATA, BuildConfig.VERSION_NAME).apply();
			if (lastVersion != null) {
				onNewVersion();
			}
		}
	}

	/**
	 * Load some things in the background that are needed later
	 */
	private void preload() {
		Log.i(PRELOAD_TAG, "Preloading data");

		// country boundaries are necessary latest for when a quest is opened
		new Thread(() -> {
			countryBoundariesFuture.run();
			Log.i(PRELOAD_TAG, "Loaded country boundaries");
		}).start();

		// names dictionary is necessary when displaying an element that has no name or
		// when downloading the place name quest
		new Thread(() -> {
			featuresDictionaryFuture.run();
			Log.i(PRELOAD_TAG, "Loaded features dictionary");
		}).start();
	}

	private void onNewVersion() {
		// on each new version, invalidate quest cache
		downloadedTilesDao.removeAll();
	}
}
