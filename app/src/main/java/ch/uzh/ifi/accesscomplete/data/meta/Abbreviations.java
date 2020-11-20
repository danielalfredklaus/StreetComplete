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

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Road abbreviations for all languages */
public class Abbreviations
{
	private Map<String, String> abbreviations;
	private final Locale locale;

	public Abbreviations(InputStream config, Locale locale)
	{
		this.locale = locale;
		try
		{
			parseConfig(config);
		}
		catch (YamlException e)
		{
			throw new RuntimeException(e);
		}
	}

	private void parseConfig(InputStream config) throws YamlException
	{
		abbreviations = new HashMap<>();

		YamlReader reader = new YamlReader(new InputStreamReader(config));
		Map map = (Map) reader.read();
		for(Object o : map.entrySet())
		{
			Map.Entry pair2 = (Map.Entry) o;
			String abbreviation = ((String)pair2.getKey()).toLowerCase(locale);
			String expansion = ((String) pair2.getValue()).toLowerCase(locale);

			if(abbreviation.endsWith("$"))
			{
				abbreviation = abbreviation.substring(0, abbreviation.length() - 1) + "\\.?$";
			}
			else
			{
				abbreviation += "\\.?";
			}

			if(abbreviation.startsWith("..."))
			{
				abbreviation = "(\\w*)" + abbreviation.substring(3);
				expansion = "$1" + expansion;
			}
			abbreviations.put(abbreviation, expansion);
		}
	}

	/**
	 *  @param word the word that might be an abbreviation for something
	 *  @param isFirstWord whether the given word is the first word in the name
	 *  @param isLastWord whether the given word is the last word in the name
	 *  @return the expansion of the abbreviation if word is an abbreviation for something,
	 *          otherwise null*/
	public String getExpansion(String word, boolean isFirstWord, boolean isLastWord)
	{
		for(Map.Entry<String, String> abbreviation : abbreviations.entrySet())
		{
			String pattern = abbreviation.getKey();

			Matcher m = getMatcher(word, pattern, isFirstWord, isLastWord);
			if(m == null || !m.matches()) continue;

			String replacement = abbreviation.getValue();
			String result = m.replaceFirst(replacement);

			return firstLetterToUppercase(result,locale);
		}
		return null;
	}

	private Matcher getMatcher(String word, String pattern, boolean isFirstWord, boolean isLastWord)
	{
		if(pattern.startsWith("^") && !isFirstWord) return null;
		if(pattern.endsWith("$") && !isLastWord) return null;

		int patternFlags = Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;
		Pattern p = Pattern.compile(pattern, patternFlags);
		Matcher m = p.matcher(word);

		if(pattern.endsWith("$") )
		{
			/* abbreviations that are marked to only appear at the end of the name do not
			   match with the first word the user is typing. I.e. if the user types "St. ", it will
			   not expand to "Street " because it is also the first and only word so far

			   UNLESS the word is actually concatenated, i.e. German "Königstr." is expanded to
			   "Königstraße" (but "Str. " is not expanded to "Straße") */
			if(isFirstWord)
			{
				boolean isConcatenated = m.matches() && m.groupCount() > 0 && !m.group(1).isEmpty();
				if(!isConcatenated)
					return null;
			}
		}

		return m;
	}

	public Locale getLocale()
	{
		return locale;
	}

	private static String firstLetterToUppercase(String word, Locale locale)
	{
		return word.substring(0,1).toUpperCase(locale) + word.substring(1);
	}

	/** @return whether any word in the given name matches with an abbreviation */
	public boolean containsAbbreviations(String name)
	{
		String[] words = name.split("[ -]+");
		for (int i=0; i<words.length; ++i)
		{
			String word = words[i];
			for (Map.Entry<String, String> abbreviation : abbreviations.entrySet())
			{
				String pattern = abbreviation.getKey();
				Matcher m = getMatcher(word, pattern, i==0, i==words.length - 1);
				if(m != null && m.matches())
					return true;
			}
		}
		return false;
	}
}
