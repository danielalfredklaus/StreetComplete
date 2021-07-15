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

package ch.uzh.ifi.accesscomplete.map

import android.content.res.Resources
import android.util.Log
import androidx.collection.LongSparseArray
import androidx.collection.forEach
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.mapzen.tangram.MapData
import com.mapzen.tangram.geometry.Point
import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.OsmQuest
import ch.uzh.ifi.accesscomplete.data.quest.*
import ch.uzh.ifi.accesscomplete.data.visiblequests.OrderedVisibleQuestTypesProvider
import ch.uzh.ifi.accesscomplete.ktx.getBitmapDrawable
import ch.uzh.ifi.accesscomplete.ktx.values
import ch.uzh.ifi.accesscomplete.map.tangram.Marker
import ch.uzh.ifi.accesscomplete.map.tangram.toLngLat
import ch.uzh.ifi.accesscomplete.reports.API.UzhQuest2
import ch.uzh.ifi.accesscomplete.reports.database.LoginState
import ch.uzh.ifi.accesscomplete.util.Tile
import ch.uzh.ifi.accesscomplete.util.TilesRect
import ch.uzh.ifi.accesscomplete.util.enclosingTilesRect
import ch.uzh.ifi.accesscomplete.util.minTileRect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/** Manages the layer of quest pins in the map view:
 *  Gets told by the QuestsMapFragment when a new area is in view and independently pulls the quests
 *  for the bbox surrounding the area from database and holds it in memory. */
class QuestPinLayerManager @Inject constructor(
    private val questTypesProvider: OrderedVisibleQuestTypesProvider,
    private val resources: Resources,
    private val visibleQuestsSource: VisibleQuestsSource
): LifecycleObserver, VisibleQuestListener, CoroutineScope by CoroutineScope(Dispatchers.Default) {

    // draw order in which the quest types should be rendered on the map
    private val questTypeOrders: MutableMap<QuestType<*>, Int> = mutableMapOf()
    // all the (zoom 14) tiles that have been retrieved from DB into memory already
    private val retrievedTiles: MutableSet<Tile> = mutableSetOf()
    // last displayed rect of (zoom 14) tiles
    private var lastDisplayedRect: TilesRect? = null

    // quest group -> ( quest Id -> [point, ...] )
    private val quests: EnumMap<QuestGroup, LongSparseArray<Quest>> = EnumMap(QuestGroup::class.java)

    lateinit var mapFragment: MapFragment //Is a QuestsMapFragment

    val TAG = "QuestPinLayerManager"

    var dansLayer: MapData? = null
    set(value) {
        if(field === value) return
        field = value
        updateDansLayer()
    }
    var uzhQuestList: MutableList<UzhQuest2> = mutableListOf()
    var uzhMarkerList: MutableList<Marker> = mutableListOf()


    var questsLayer: MapData? = null
        set(value) {
            if (field === value) return
            field = value
            updateLayer()
        }

    /** Switch visibility of quest pins layer */
    var isVisible: Boolean = true
        set(value) {
            if (field == value) return
            field = value
            updateLayer()
        }

    init {
        visibleQuestsSource.addListener(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START) fun onStart() {
        /* When reentering the fragment, the database may have changed (quest download in
        * background or change in settings), so the quests must be pulled from DB again */
        initializeQuestTypeOrders()
        clear()
        onNewScreenPosition()

        mapFragment.markerViewModel.allMapMarkers.observe(mapFragment.viewLifecycleOwner,){ list ->
            if(list.isEmpty()){
                Log.d(TAG, "Observed Map Markers are emtpy, filling up")
                mapFragment.markerViewModel.fillMarkerListFromServer()
            } else {
                Log.d(TAG, "Received Marker List of size ${list.size}")
                clearDansStuff()
                updateDansLayer()
                Log.d(TAG, "Observed allMapMarkers not empty end")
            }
        }

        mapFragment.markerViewModel.loginResult.observe(mapFragment.viewLifecycleOwner,){ state->
            when(state){
                LoginState.SUCCESS-> {
                    Log.d(TAG, "Observed LoginState Success")
                }
                else -> {Log.d(TAG, "Observed $state")}//donothing
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP) fun onStop() {
        clear()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY) fun onDestroy() {
        questsLayer = null
        dansLayer = null
        visibleQuestsSource.removeListener(this)
        coroutineContext.cancel()
    }

    fun onNewScreenPosition() {
        val zoom = mapFragment.cameraPosition?.zoom ?: return
        if (zoom < TILES_ZOOM) return
        val displayedArea = mapFragment.getDisplayedArea() ?: return
        val tilesRect = displayedArea.enclosingTilesRect(TILES_ZOOM)
        if (lastDisplayedRect != tilesRect) {
            lastDisplayedRect = tilesRect
            updateQuestsInRect(tilesRect)
        }
    }

    override fun onUpdatedVisibleQuests(added: Collection<Quest>, removed: Collection<Long>, group: QuestGroup) {
        added.forEach { add(it, group) }
        removed.forEach { remove(it, group) }
        updateLayer()
        mapFragment.requestRender()
    }

    private fun updateQuestsInRect(tilesRect: TilesRect) {
        // area too big -> skip (performance)
        if (tilesRect.size > 4) {
            return
        }
        var tiles: List<Tile>
        synchronized(retrievedTiles) {
            tiles = tilesRect.asTileSequence().filter { !retrievedTiles.contains(it) }.toList()
        }
        val minRect = tiles.minTileRect() ?: return
        val bbox = minRect.asBoundingBox(TILES_ZOOM)
        val questTypeNames = questTypesProvider.get().map { it.javaClass.simpleName }
        launch(Dispatchers.IO) {
            visibleQuestsSource.getAllVisible(bbox, questTypeNames).forEach {
                add(it.quest, it.group)
            }
            updateLayer()
        }
        synchronized(retrievedTiles) { retrievedTiles.addAll(tiles) }
    }

    /**
     * Adds a quest mapped to it's Questgroup to the quests.
     */
    private fun add(quest: Quest, group: QuestGroup) {
        synchronized(quests) {
            if (quests[group] == null) quests[group] = LongSparseArray(256)
            quests[group]?.put(quest.id!!, quest)
        }
    }

    private fun remove(questId: Long, group: QuestGroup) {
        synchronized(quests) {
            quests[group]?.remove(questId)
        }
    }

    private fun clear() {
        synchronized(quests) {
            for (value in quests.values) {
                value.clear()
            }
        }
        synchronized(retrievedTiles) {
            retrievedTiles.clear()
        }
        questsLayer?.clear()
        lastDisplayedRect = null

        clearDansStuff()
    }

    private fun clearDansStuff(){
        dansLayer?.clear()
        uzhQuestList = mutableListOf()

    }

    /**
     * If visible, adds quest markers to the quest layer,
     * else it removes all markers
     */
    private fun updateLayer() {
        if (isVisible) {
            questsLayer?.setFeatures(getPoints())
        } else {
            questsLayer?.clear()
        }
        Log.d(TAG, "Layer updated")
    }

    private fun updateDansLayer() {
        if (isVisible) {
            dansLayer?.setFeatures(getUzhPoints())
        } else {
            dansLayer?.clear()
        }
        Log.d(TAG, "Daniels Layer updated")
    }

    private fun getPoints(): List<Point> {
        val result = mutableListOf<Point>()
        val tempResult = mutableListOf<Point>()
        synchronized(quests) {
            for ((group, questById) in quests) {
                val elementIdCount = mutableMapOf<Long, Int?>()
                questById.forEach { _, quest ->
                    if (quest is OsmQuest) {
                        if (elementIdCount[quest.elementId] == null) {
                            elementIdCount[quest.elementId] = 1
                        } else {
                            elementIdCount[quest.elementId] = elementIdCount[quest.elementId]?.plus(1)
                        }
                    }
                }

                val addedOsmElementId = mutableSetOf<Long>()
                questById.forEach { _, quest ->
                    val questIconName = resources.getResourceEntryName(quest.type.icon)
                    val positions = quest.markerLocations
                    val properties = mutableMapOf(
                        "type" to "point",
                        "kind" to questIconName,
                        "importance" to getQuestImportance(quest).toString(),
                        MARKER_QUEST_GROUP to group.name
                    )

                    val questCount = if (quest is OsmQuest) elementIdCount[quest.elementId]!! else 1
                    if (quest is OsmQuest && questCount > 1) {
                        if (!addedOsmElementId.contains(quest.elementId)) {
                            val multiQuestIconResource = getCorrectMultiQuestIconResource(questCount)
                            properties["kind"] = resources.getResourceEntryName(multiQuestIconResource)
                            properties[MARKER_ELEMENT_ID] = quest.elementId.toString()
                            val points = positions.map { position ->
                                Point(position.toLngLat(), properties)
                            }
                            result.addAll(points)

                            addedOsmElementId.add(quest.elementId)
                        }
                    } else {
                        properties[MARKER_QUEST_ID] = quest.id!!.toString()
                        val points = positions.map { position ->
                            Point(position.toLngLat(), properties)
                        }
                        tempResult.addAll(points)
                    }
                }
            }
        }
        result.addAll(tempResult)
        return result
    }

    private fun getUzhPoints(): List<Point>{
        val newList = mapFragment.markerViewModel.allMapMarkers.value ?: mutableListOf<UzhQuest2>()
        val result = mutableListOf<Point>()
        var counter = 7000
        newList.forEach{ quest ->

            //First we need to add markers, dont know where the map markers are added initially
            /*
            val marker = mapFragment.controller!!.addMarker()
            Log.d(TAG, "Added Marker ${marker.markerId}")
            marker.setPoint(quest.center)
            marker.setDrawOrder(counter++)
            //marker.setDrawable(R.drawable.ic_quest_road_construction)
            marker.setDrawable(mapFragment.resources.getBitmapDrawable(R.drawable.ic_quest_road_construction))
            //val dotWidth = dot.intrinsicWidth.toFloat().toDp(mapFragment.requireContext())
            //val dotHeight = dot.intrinsicHeight.toFloat().toDp(mapFragment.requireContext())
            marker.setStylingFromString(
                "{ style: 'points', color: 'white', size: [30px, 30px], collide: false, order: $counter }"
            )   // { style: 'points', color: 'white', size: [50px, 50px], order: 2000, collide: false } style: quest-selection
            uzhMarkerList.add(marker) */

            val questIconName = resources.getResourceEntryName(quest.type.icon)
            //Log.d(TAG, "Icon Name of Quest is $questIconName")
            val position = quest.markerLocations.first()
            val properties = mutableMapOf(
                "type" to "point",
                "kind" to questIconName,
                "importance" to getQuestImportance(quest).toString(),
                MARKER_QUEST_GROUP to "UZH",
                MARKER_QUEST_ID to quest.id.toString()
            )
            //properties[MARKER_ELEMENT_ID] = "0"
            val point = Point(position.toLngLat(), properties)
            //Log.d(TAG, "Adding Point $position with importance ${point.propertyArray?.get(5)} and id ${point.propertyArray?.get(9)}" )
            result.add(point)
        }
        uzhQuestList = newList
        //Log.d(TAG, "Added $uzhQuestList to Map")
        return result
    }

    private fun getCorrectMultiQuestIconResource(count: Int): Int {
        return when (count) {
            2 -> R.drawable.ic_multi_quest_2
            3 -> R.drawable.ic_multi_quest_3
            4 -> R.drawable.ic_multi_quest_4
            5 -> R.drawable.ic_multi_quest_5
            6 -> R.drawable.ic_multi_quest_6
            7 -> R.drawable.ic_multi_quest_7
            8 -> R.drawable.ic_multi_quest_8
            9 -> R.drawable.ic_multi_quest_9
            else -> R.drawable.ic_multi_quest_9_and_more
        }
    }

    fun findQuestsBelongingToOsmElementId(osmElementId: Long): List<Quest> {
        val result = mutableListOf<Quest>()
        synchronized(quests) {
            for ((_, questById) in quests) {
                result.addAll(
                    questById.values
                        .filterIsInstance<OsmQuest>() //TODO: Add UZH
                        .filter { quest -> quest.elementId == osmElementId })
            }
        }
        return result.sortedByDescending { getQuestImportance(it) }
    }

    private fun initializeQuestTypeOrders() {
        // this needs to be reinitialized when the quest order changes
        var order = 0
        for (questType in questTypesProvider.get()) {
            questTypeOrders[questType] = order++
        }
    }

    /** returns values from 0 to 100000, the higher the number, the more important */
    private fun getQuestImportance(quest: Quest): Int {
        val questTypeOrder = questTypeOrders[quest.type] ?: 0
        val freeValuesForEachQuest = 100000 / questTypeOrders.size
        /* quest ID is used to add values unique to each quest to make ordering consistent
           freeValuesForEachQuest is an int, so % freeValuesForEachQuest will fit into int */
        val hopefullyUniqueValueForQuest = ((quest.id?: 0) % freeValuesForEachQuest).toInt()
        return 100000 - questTypeOrder * freeValuesForEachQuest + hopefullyUniqueValueForQuest
    }

    companion object {
        const val MARKER_QUEST_ID = "quest_id"
        const val MARKER_QUEST_GROUP = "quest_group"
        const val MARKER_ELEMENT_ID = "element_id"
        private const val TILES_ZOOM = 14
    }
}
