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

package ch.uzh.ifi.accesscomplete.util;

import androidx.annotation.NonNull;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

/** Iterate through several iterables of type T with just one iterator! */
public class MultiIterable<T> implements Iterable<T>
{
	private final Queue<Iterable<T>> queue;

	public MultiIterable()
	{
		queue = new ArrayDeque<>();
	}

	@NonNull @Override public Iterator<T> iterator()
	{
		return new MultiIterator<>(queue.iterator());
	}

	public void add(Iterable<T> iterable)
	{
		queue.add(iterable);
	}

	public static class MultiIterator<T> implements Iterator<T>
	{
		private final Iterator<Iterable<T>> it;
		private Iterator<T> currentIt;
		private T next;
		private boolean nextValid;

		private MultiIterator(Iterator<Iterable<T>> it)
		{
			this.it = it;
		}

		@Override public void remove()
		{
			throw new UnsupportedOperationException();
		}

		@Override public boolean hasNext()
		{
			if (!nextValid) nextValid = moveToNext();
			return nextValid;
		}

		@Override public T next()
		{
			if (!hasNext()) throw new NoSuchElementException();
			nextValid = false;
			return next;
		}

		private boolean moveToNext()
		{
			while(currentIt != null || it.hasNext())
			{
				if (currentIt == null)
				{
					currentIt = it.next().iterator();
				}
				else if (!currentIt.hasNext())
				{
					currentIt = null;
				}
				else
				{
					next = currentIt.next();
					return true;
				}
			}
			return false;
		}
	}
}


