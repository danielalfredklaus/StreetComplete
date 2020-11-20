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

package ch.uzh.ifi.accesscomplete.data.meta;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class CountryInfo implements Serializable, Cloneable {

	public static final long serialVersionUID = 1L;

	// this value is not defined in the yaml file but it is the ISO language code part of the file name!
	// i.e. US for US-TX.yml
	String countryCode;

	List<String> lengthUnits;
	List<String> speedUnits;
	String firstDayOfWorkweek;
	Integer workweekDays;
	List<String> officialLanguages;
	Boolean isSlowZoneKnown;
	Boolean isLivingStreetKnown;
	Boolean isAdvisorySpeedLimitKnown;
	Boolean isLeftHandTraffic;
	Integer mobileCountryCode;


	public List<String> getLengthUnits() {
		return lengthUnits;
	}

	public List<String> getSpeedUnits() {
		return speedUnits;
	}


	public String getFirstDayOfWorkweek() {
		return firstDayOfWorkweek;
	}

	public Integer getWorkweekDays() {
		return workweekDays;
	}

	public boolean isSlowZoneKnown() {
		return isSlowZoneKnown;
	}

	public List<String> getOfficialLanguages() {
		if (officialLanguages == null) return new ArrayList<>(1);
		return Collections.unmodifiableList(officialLanguages);
	}

	public String getCountryCode() {
		return countryCode;
	}

	public Locale getLocale() {
		List<String> languages = getOfficialLanguages();
		if (!languages.isEmpty()) {
			return new Locale(languages.get(0), countryCode);
		}
		return Locale.getDefault();
	}

	public boolean isLivingStreetKnown() {
		return isLivingStreetKnown;
	}

	public boolean isAdvisorySpeedLimitKnown() {
		return isAdvisorySpeedLimitKnown;
	}

	public boolean isLeftHandTraffic() {
		return isLeftHandTraffic;
	}

	public Integer getMobileCountryCode() {
		return mobileCountryCode;
	}
}
