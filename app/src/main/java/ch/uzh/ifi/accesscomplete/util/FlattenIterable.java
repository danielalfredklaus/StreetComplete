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
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

public class FlattenIterable<T> implements Iterable<T> {

	private final Queue<Iterable> queue;
	private final Class<T> clazz;

	public FlattenIterable(Class<T> clazz) {
		this.clazz = clazz;
		queue = new ArrayDeque<>();
	}

	@NonNull
	@Override
	public Iterator<T> iterator() {
		return new FlattenIterator<>(clazz, queue.iterator());
	}

	/**
	 * Add an iterable. The iterable must only contain elements of type T and iterables thereof
	 */
	public void add(Iterable iterable) {
		queue.add(iterable);
	}

	public static class FlattenIterator<T> implements Iterator<T> {
		private final Deque<Iterator> iteratorStack;
		private final Class<T> clazz;
		private T next;
		private boolean nextValid;

		private FlattenIterator(Class<T> clazz, Iterator<Iterable> it) {
			this.clazz = clazz;
			iteratorStack = new ArrayDeque<>();
			iteratorStack.addFirst(it);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean hasNext() {
			if (!nextValid) nextValid = moveToNext();
			return nextValid;
		}

		@Override
		public T next() {
			if (!hasNext()) throw new NoSuchElementException();
			nextValid = false;
			return next;
		}

		private boolean moveToNext() {
			while (!iteratorStack.isEmpty()) {
				Iterator it = iteratorStack.peekFirst();
				if (!it.hasNext()) {
					iteratorStack.removeFirst();
					continue;
				}
				Object peek = it.next();
				if (peek == null) {
					next = null;
					return true;
				} else if (clazz.isInstance(peek)) {
					next = (T) peek;
					return true;
				} else if (peek instanceof Iterable) {
					iteratorStack.addFirst(((Iterable) peek).iterator());
					continue;
				}
				throw new IllegalArgumentException("Only iterables of T and Ts are allowed in the input!");
			}
			next = null;
			return false;
		}
	}
}


