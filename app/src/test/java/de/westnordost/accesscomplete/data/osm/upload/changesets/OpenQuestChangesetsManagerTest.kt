package de.westnordost.accesscomplete.data.osm.upload.changesets

import android.content.SharedPreferences
import ch.uzh.ifi.osmapi.map.MapDataWithGeometry
import de.westnordost.osmapi.map.data.Element
import de.westnordost.accesscomplete.ApplicationConstants
import de.westnordost.accesscomplete.any
import de.westnordost.accesscomplete.data.MapDataApi
import de.westnordost.accesscomplete.data.osm.changes.StringMapChangesBuilder
import de.westnordost.accesscomplete.data.osm.osmquest.OsmElementQuestType
import de.westnordost.accesscomplete.mock
import de.westnordost.accesscomplete.on
import de.westnordost.accesscomplete.quests.AbstractQuestAnswerFragment
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import java.util.*

class OpenQuestChangesetsManagerTest {

    private lateinit var questType: OsmElementQuestType<*>
    private lateinit var mapDataApi: MapDataApi
    private lateinit var openChangesetsDB: OpenChangesetsDao
    private lateinit var changesetAutoCloser: ChangesetAutoCloser
    private lateinit var manager: OpenQuestChangesetsManager
    private lateinit var prefs: SharedPreferences

    @Before fun setUp() {
        questType = TestQuestType()
        mapDataApi = mock()
        openChangesetsDB = mock()
        changesetAutoCloser = mock()
        prefs = mock()
        manager = OpenQuestChangesetsManager(mapDataApi, openChangesetsDB, changesetAutoCloser, prefs)
    }

    @Test fun `create new changeset if none exists`() {
        on(openChangesetsDB.get(any(), any())).thenReturn(null)
        on(mapDataApi.openChangeset(any())).thenReturn(123L)

        assertEquals(123L, manager.getOrCreateChangeset(questType, "my source"))

        verify(mapDataApi).openChangeset(any())
        verify(openChangesetsDB).put(any())
    }

    @Test fun `reuse changeset if one exists`() {
        on(openChangesetsDB.get(any(), any())).thenReturn(OpenChangeset("bla", "source", 123))

        assertEquals(123L, manager.getOrCreateChangeset(questType, "my source"))

        verify(mapDataApi, never()).openChangeset(any())
    }

    @Test fun `create correct changeset tags`() {
        on(openChangesetsDB.get(any(), any())).thenReturn(null)
        val locale = Locale.getDefault()
        Locale.setDefault(Locale("es", "AR"))

        manager.getOrCreateChangeset(questType, "my source")

        Locale.setDefault(locale)

        verify(mapDataApi).openChangeset(mapOf(
            "source" to "my source",
            "created_by" to ApplicationConstants.USER_AGENT,
            "comment" to "test me",
            "locale" to "es-AR",
            "StreetComplete:quest_type" to "TestQuestType"
        ))
        verify(openChangesetsDB).put(any())
    }

}

private class TestQuestType : OsmElementQuestType<String> {

    override fun getApplicableElements(mapData: MapDataWithGeometry) = emptyList<Element>()
    override fun getTitle(tags: Map<String, String>) = 0
    override fun isApplicableTo(element: Element):Boolean? = null
    override fun applyAnswerTo(answer: String, changes: StringMapChangesBuilder) {}
    override val icon = 0
    override fun createForm(): AbstractQuestAnswerFragment<String> = object : AbstractQuestAnswerFragment<String>() {}
    override val commitMessage = "test me"
}

