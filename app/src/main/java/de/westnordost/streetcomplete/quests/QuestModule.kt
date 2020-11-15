package de.westnordost.streetcomplete.quests

import dagger.Module
import dagger.Provides
import de.westnordost.osmfeatures.FeatureDictionary
import de.westnordost.streetcomplete.data.osmnotes.notequests.OsmNoteQuestType
import de.westnordost.streetcomplete.data.quest.QuestTypeRegistry
import de.westnordost.streetcomplete.quests.road_name.data.RoadNameSuggestionsDao
import de.westnordost.streetcomplete.quests.oneway_suspects.data.TrafficFlowSegmentsApi
import de.westnordost.streetcomplete.quests.oneway_suspects.data.WayTrafficFlowDao
import de.westnordost.streetcomplete.quests.incline.AddPathIncline
import de.westnordost.streetcomplete.quests.incline.AddPedestrianAccessibleStreetIncline
import de.westnordost.streetcomplete.quests.kerb_type.AddKerbType
import de.westnordost.streetcomplete.quests.smoothness.AddCyclewayPartSmoothness
import de.westnordost.streetcomplete.quests.smoothness.AddFootwayPartSmoothness
import de.westnordost.streetcomplete.quests.smoothness.AddPathSmoothness
import de.westnordost.streetcomplete.quests.smoothness.AddPedestrianAccessibleStreetSmoothness
import de.westnordost.streetcomplete.quests.surface.*
import de.westnordost.streetcomplete.quests.width.AddCyclewayPartWidth
import de.westnordost.streetcomplete.quests.width.AddFootwayPartWidth
import de.westnordost.streetcomplete.quests.width.AddPathWidth
import de.westnordost.streetcomplete.quests.width.AddPedestrianAccessibleStreetWidth
import java.util.concurrent.FutureTask
import javax.inject.Singleton

@Module
object QuestModule {

    @Provides
    @Singleton
    fun questTypeRegistry(
        osmNoteQuestType: OsmNoteQuestType,
        roadNameSuggestionsDao: RoadNameSuggestionsDao,
        trafficFlowSegmentsApi: TrafficFlowSegmentsApi,
        trafficFlowDao: WayTrafficFlowDao,
        featureDictionaryFuture: FutureTask<FeatureDictionary>
    ): QuestTypeRegistry = QuestTypeRegistry(listOf(

        // Notes
        osmNoteQuestType,

        // Kerb
        AddKerbType(),

        // Surface
        AddPathSurface(),
        AddPedestrianAccessibleStreetSurface(),
        AddFootwayPartSurface(),
        AddCyclewayPartSurface(),

        // Smoothness
        AddPathSmoothness(),
        AddPedestrianAccessibleStreetSmoothness(),
        AddFootwayPartSmoothness(),
        AddCyclewayPartSmoothness(),

        // Width
        AddPathWidth(),
        AddPedestrianAccessibleStreetWidth(),
        AddFootwayPartWidth(),
        AddCyclewayPartWidth(),

        // Incline
        AddPathIncline(),
        AddPedestrianAccessibleStreetIncline(),

//        AddCycleway(), // for any cyclist routers (and cyclist maps)
//        AddSidewalk(), // for any pedestrian routers
//        MarkCompletedHighwayConstruction(),
//        AddProhibitedForPedestrians(), // uses info from AddSidewalk quest, should be after it
//        AddStepsRamp(),
//        AddCyclewaySegregation(),
//        AddWheelchairAccessBusiness(featureDictionaryFuture), // used by wheelmap, OsmAnd, MAPS.ME
//        AddWheelchairAccessToilets(), // used by wheelmap, OsmAnd, MAPS.ME
//        AddWheelchairAccessToiletsPart(),
//        AddWheelchairAccessPublicTransport(),
//        AddWheelchairAccessOutside(),
//        AddTactilePavingCrosswalk(), // Paving can be completed while waiting to cross
//        AddTrafficSignalsSound(), // Sound needs to be done as or after you're crossing
//        AddTrafficSignalsVibration(),
//        AddTrafficSignalsButton()
    ))

    @Provides
    @Singleton
    fun osmNoteQuestType(): OsmNoteQuestType = OsmNoteQuestType()
}
