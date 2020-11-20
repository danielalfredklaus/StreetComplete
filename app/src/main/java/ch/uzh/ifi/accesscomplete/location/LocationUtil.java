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

package ch.uzh.ifi.accesscomplete.location;

import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.provider.Settings;

import androidx.core.content.ContextCompat;

public class LocationUtil {

	public static boolean isLocationOn(Context context) {
		if (!hasLocationPermission(context)) return false;

		try {
			int locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
			return locationMode != Settings.Secure.LOCATION_MODE_OFF;

		} catch (Settings.SettingNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean hasLocationPermission(Context context) {
		return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
				== PackageManager.PERMISSION_GRANTED;
	}

	public static IntentFilter createLocationAvailabilityIntentFilter() {
		return new IntentFilter(MODE_CHANGED);
	}

	// because LocationManager.MODE_CHANGED is not defined before KitKat
	private static final String MODE_CHANGED = "android.location.MODE_CHANGED";
}
