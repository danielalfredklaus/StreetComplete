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

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import org.objenesis.strategy.StdInstantiatorStrategy;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.inject.Singleton;

import de.westnordost.osmapi.map.data.Element;
import de.westnordost.osmapi.map.data.Fixed1E7LatLon;
import de.westnordost.osmapi.map.data.OsmLatLon;
import de.westnordost.osmapi.map.data.OsmRelationMember;
import de.westnordost.osmapi.notes.NoteComment;
import de.westnordost.osmapi.user.User;
import ch.uzh.ifi.accesscomplete.data.osm.changes.StringMapChanges;
import ch.uzh.ifi.accesscomplete.data.osm.changes.StringMapEntryAdd;
import ch.uzh.ifi.accesscomplete.data.osm.changes.StringMapEntryDelete;
import ch.uzh.ifi.accesscomplete.data.osm.changes.StringMapEntryModify;
import ch.uzh.ifi.accesscomplete.data.osm.splitway.SplitAtLinePosition;
import ch.uzh.ifi.accesscomplete.data.osm.splitway.SplitAtPoint;
import ch.uzh.ifi.accesscomplete.quests.LocalizedName;

@Singleton
public class KryoSerializer implements Serializer {

	// NEVER CHANGE THE ORDER OF THIS LIST. ALWAYS APPEND NEW CLASSES AT THE BOTTOM
	// IF CLASSES ARE DELETED, INSERT A PLACEHOLDER (i.e. Object.class) THERE
	private static final Class<?>[] registeredClasses =
			{
					HashMap.class,
					ArrayList.class,
					Fixed1E7LatLon.class,
					Element.Type.class,
					OsmRelationMember.class,
					StringMapChanges.class,
					StringMapEntryAdd.class,
					StringMapEntryDelete.class,
					StringMapEntryModify.class,
					NoteComment.class,
					NoteComment.Action.class,
					Date.class,
					User.class,
					boolean[].class,
					LocalizedName.class,
					OsmLatLon.class,
					SplitAtPoint.class,
					SplitAtLinePosition.class,
			};


	private static final ThreadLocal<Kryo> kryo = new ThreadLocal<Kryo>() {
		@Override
		protected Kryo initialValue() {
			Kryo kryo = new Kryo();

			/* Kryo docs say that classes that are registered are serialized more space efficiently
	 		  (so it is not necessary that all classes that are serialized are registered here, but
	 		   it is better) */
			kryo.setRegistrationRequired(true);
			kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
			for (Class<?> reg : registeredClasses) {
				kryo.register(reg);
			}
			return kryo;
		}
	};

	@Override
	public byte[] toBytes(Object object) {
		try (Output output = new Output(1024, -1)) {
			kryo.get().writeObject(output, object);
			return output.toBytes();
		}
	}

	@Override
	public <T> T toObject(byte[] bytes, Class<T> type) {
		try (Input input = new Input(bytes)) {
			return kryo.get().readObject(input, type);
		}
	}
}
