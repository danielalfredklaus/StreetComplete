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

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import ch.uzh.ifi.accesscomplete.R;

public class AbbreviationsByLocale
{
	private final Context applicationContext;

	private final Map<String,Abbreviations> byLanguageAbbreviations = new HashMap<>();

	@Inject public AbbreviationsByLocale(Context applicationContext)
	{
		this.applicationContext = applicationContext;
	}

	public Abbreviations get(Locale locale)
	{
		String code = locale.toString();
		if(!byLanguageAbbreviations.containsKey(code))
		{
			byLanguageAbbreviations.put(code, load(locale));
		}
		return byLanguageAbbreviations.get(code);
	}

	private Abbreviations load(Locale locale)
	{
		InputStream is = getResources(locale).openRawResource(R.raw.abbreviations);
		return new Abbreviations(is, locale);
	}

	private Resources getResources(Locale locale)
	{
		Configuration configuration = new Configuration(applicationContext.getResources().getConfiguration());
		configuration.setLocale(locale);
		return applicationContext.createConfigurationContext(configuration).getResources();
	}
}
