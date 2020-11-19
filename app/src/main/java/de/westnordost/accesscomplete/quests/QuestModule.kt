package de.westnordost.accesscomplete.quests

import dagger.Module
import dagger.Provides
import de.westnordost.osmfeatures.FeatureDictionary
import de.westnordost.accesscomplete.data.osmnotes.notequests.OsmNoteQuestType
import de.westnordost.accesscomplete.data.quest.QuestTypeRegistry
import de.westnordost.accesscomplete.quests.incline.AddPathIncline
import de.westnordost.accesscomplete.quests.incline.AddPedestrianAccessibleStreetIncline
import de.westnordost.accesscomplete.quests.kerb_type.AddKerbType
import de.westnordost.accesscomplete.quests.smoothness.AddCyclewayPartSmoothness
import de.westnordost.accesscomplete.quests.smoothness.AddFootwayPartSmoothness
import de.westnordost.accesscomplete.quests.smoothness.AddPathSmoothness
import de.westnordost.accesscomplete.quests.smoothness.AddPedestrianAccessibleStreetSmoothness
import de.westnordost.accesscomplete.quests.surface.AddCyclewayPartSurface
import de.westnordost.accesscomplete.quests.surface.AddFootwayPartSurface
import de.westnordost.accesscomplete.quests.surface.AddPathSurface
import de.westnordost.accesscomplete.quests.surface.AddPedestrianAccessibleStreetSurface
import de.westnordost.accesscomplete.quests.width.AddCyclewayPartWidth
import de.westnordost.accesscomplete.quests.width.AddFootwayPartWidth
import de.westnordost.accesscomplete.quests.width.AddPathWidth
import de.westnordost.accesscomplete.quests.width.AddPedestrianAccessibleStreetWidth
import java.util.concurrent.FutureTask
import javax.inject.Singleton

@Module
object QuestModule {

    @Provides
    @Singleton
    fun questTypeRegistry(
        osmNoteQuestType: OsmNoteQuestType,
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
