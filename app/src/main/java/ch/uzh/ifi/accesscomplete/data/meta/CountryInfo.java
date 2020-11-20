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

public class CountryInfo implements Serializable, Cloneable
{
	public static final long serialVersionUID = 1L;

	// this value is not defined in the yaml file but it is the ISO language code part of the file name!
	// i.e. US for US-TX.yml
	String countryCode;

	List<String> lengthUnits;
	List<String> speedUnits;
	List<String> weightLimitUnits;
	List<String> popularSports;
	List<String> popularReligions;
	String firstDayOfWorkweek;
	Integer regularShoppingDays;
	Integer workweekDays;
	String additionalValidHousenumberRegex;
	List<String> officialLanguages;
	List<String> additionalStreetsignLanguages;
	Boolean isSlowZoneKnown;
	Boolean isLivingStreetKnown;
	List<String> orchardProduces;
	Boolean isAdvisorySpeedLimitKnown;
	Boolean isLeftHandTraffic;
	Integer mobileCountryCode;
	List<String> chargingStationOperators;
	List<String> clothesContainerOperators;
	List<String> atmOperators;
	Boolean isUsuallyAnyGlassRecycleableInContainers;

	public Boolean isUsuallyAnyGlassRecycleableInContainers() {
		return isUsuallyAnyGlassRecycleableInContainers;
	}

	public List<String> getChargingStationOperators() {
		return chargingStationOperators;
	}

	public List<String> getClothesContainerOperators() {
		return clothesContainerOperators;
	}

	public List<String> getAtmOperators() {
		return atmOperators;
	}

	public List<String> getLengthUnits()
	{
		return lengthUnits;
	}

	public List<String> getSpeedUnits()
	{
		return speedUnits;
	}

	public List<String> getWeightLimitUnits()
	{
		return weightLimitUnits;
	}

	public List<String> getPopularSports()
	{
		if(popularSports == null) return new ArrayList<>(1);
		return Collections.unmodifiableList(popularSports);
	}

	public List<String> getPopularReligions()
	{
		if(popularReligions == null) return new ArrayList<>(1);
		return Collections.unmodifiableList(popularReligions);
	}

	public String getFirstDayOfWorkweek()
	{
		return firstDayOfWorkweek;
	}

	public Integer getRegularShoppingDays()
	{
		return regularShoppingDays;
	}

	public Integer getWorkweekDays()
	{
		return workweekDays;
	}

	public boolean isSlowZoneKnown()
	{
		return isSlowZoneKnown;
	}

	public String getAdditionalValidHousenumberRegex()
	{
		return additionalValidHousenumberRegex;
	}

	public List<String> getOfficialLanguages()
	{
		if(officialLanguages == null) return new ArrayList<>(1);
		return Collections.unmodifiableList(officialLanguages);
	}

	public List<String> getAdditionalStreetsignLanguages()
	{
		if(additionalStreetsignLanguages == null) return new ArrayList<>(1);
		return Collections.unmodifiableList(additionalStreetsignLanguages);
	}

	public String getCountryCode()
	{
		return countryCode;
	}

	public Locale getLocale()
	{
		List<String> languages = getOfficialLanguages();
		if (!languages.isEmpty())
		{
			return new Locale(languages.get(0), countryCode);
		}
		return Locale.getDefault();
	}

	public boolean isLivingStreetKnown()
	{
		return isLivingStreetKnown;
	}

	public List<String> getOrchardProduces()
	{
		if(orchardProduces == null) return new ArrayList<>(1);
		return Collections.unmodifiableList(orchardProduces);
	}

	public boolean isAdvisorySpeedLimitKnown()
	{
		return isAdvisorySpeedLimitKnown;
	}

	public boolean isLeftHandTraffic()
	{
		return isLeftHandTraffic;
	}

	public Integer getMobileCountryCode()
	{
		return mobileCountryCode;
	}
}
