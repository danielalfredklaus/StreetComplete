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
public class KryoSerializer implements Serializer
{
	// NEVER CHANGE THE ORDER OF THIS LIST. ALWAYS APPEND NEW CLASSES AT THE BOTTOM
	// IF CLASSES ARE DELETED, INSERT A PLACEHOLDER (i.e. Object.class) THERE
	private static final Class[] registeredClasses =
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


	private static final ThreadLocal<Kryo> kryo = new ThreadLocal<Kryo>()
	{
		@Override protected Kryo initialValue()
		{
			Kryo kryo = new Kryo();

			/* Kryo docs say that classes that are registered are serialized more space efficiently
	 		  (so it is not necessary that all classes that are serialized are registered here, but
	 		   it is better) */
			kryo.setRegistrationRequired(true);
			kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
			for(Class reg : registeredClasses)
			{
				kryo.register(reg);
			}
			return kryo;
		}
	};

	@Override public byte[] toBytes(Object object)
	{
		try (Output output = new Output(1024, -1))
		{
			kryo.get().writeObject(output, object);
			return output.toBytes();
		}
	}

	@Override public <T> T toObject(byte[] bytes, Class<T> type)
	{
		try (Input input = new Input(bytes))
		{
			return kryo.get().readObject(input, type);
		}
	}
}
