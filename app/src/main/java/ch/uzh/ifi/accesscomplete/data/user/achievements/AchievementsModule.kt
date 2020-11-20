package ch.uzh.ifi.accesscomplete.data.user.achievements

import dagger.Module
import dagger.Provides
import ch.uzh.ifi.accesscomplete.R
import javax.inject.Named

@Module
object AchievementsModule {

    @Provides
    @Named("Links")
    fun links(): List<Link> = links
    @Provides
    @Named("Achievements")
    fun achievements(): List<Achievement> = achievements
    @Provides
    @Named("QuestAliases")
    fun questAliases(): List<Pair<String, String>> = questAliases

    // list of quest synonyms (this alternate name is mentioned to aid searching for this code)
    private val questAliases = listOf(
        "AddAccessibleForPedestrians" to "AddProhibitedForPedestrians",
        "AddWheelChairAccessPublicTransport" to "AddWheelchairAccessPublicTransport",
        "AddWheelChairAccessToilets" to "AddWheelchairAccessToilets",
        "AddSidewalks" to "AddSidewalk",
        "AddTrafficSignalsBlindFeatures" to "AddTrafficSignalsVibration",
    )

    private val links = listOf(

        /* ---------------------------------------- Intro ----------------------------------------*/
        Link(
            "wiki",
            "https://wiki.openstreetmap.org",
            "OpenStreetMap Wiki",
            LinkCategory.INTRO,
            R.drawable.ic_link_wiki,
            R.string.link_wiki_description
        ),
        Link(
            "welcomemat",
            "https://welcome.openstreetmap.org",
            "Welcome Mat",
            LinkCategory.INTRO,
            R.drawable.ic_link_welcome_mat,
            R.string.link_welcome_mat_description
        ),
        Link(
            "learnosm",
            "https://learnosm.org/en/beginner/",
            "learnOSM",
            LinkCategory.INTRO,
            R.drawable.ic_link_learnosm,
            R.string.link_learnosm_description
        ),
        Link(
            "weeklyosm",
            "https://weeklyosm.eu/",
            "weeklyOSM",
            LinkCategory.INTRO,
            R.drawable.ic_link_weeklyosm,
            R.string.link_weeklyosm_description
        ),
        Link(
            "neis-one",
            "https://resultmaps.neis-one.org/",
            "ResultMaps",
            LinkCategory.INTRO,
            R.drawable.ic_link_neis_one,
            R.string.link_neis_one_description
        ),
        Link(
            "disaster.ninja",
            "https://disaster.ninja/live/#position=11,46;zoom=3",
            "disaster.ninja",
            LinkCategory.INTRO,
            R.drawable.ic_link_kontur,
            R.string.link_disaster_ninja_description
        ),

        /* --------------------------------------- Editors ---------------------------------------*/
        Link(
            "pic4review",
            "https://pic4review.pavie.info",
            "Pic4Review",
            LinkCategory.EDITORS,
            R.drawable.ic_link_pic4review,
            R.string.link_pic4review_description
        ),
        Link(
            "ideditor",
            "http://ideditor.com",
            "iD",
            LinkCategory.EDITORS,
            R.drawable.ic_link_ideditor,
            R.string.link_ideditor_description
        ),
        Link(
            "vespucci",
            "https://vespucci.io",
            "Vespucci",
            LinkCategory.EDITORS,
            R.drawable.ic_link_vespucci,
            R.string.link_vespucci_description
        ),
        Link(
            "josm",
            "https://josm.openstreetmap.de",
            "JOSM",
            LinkCategory.EDITORS,
            R.drawable.ic_link_josm,
            R.string.link_josm_description
        ),

        /* ---------------------------------------- Maps -----------------------------------------*/

        Link(
            "öpnvkarte",
            "https://öpnvkarte.de",
            "ÖPNVKarte",
            LinkCategory.MAPS,
            R.drawable.ic_link_opnvkarte,
            R.string.link_opnvkarte_description
        ),
        Link(
            "wheelmap",
            "https://wheelmap.org",
            "wheelmap.org",
            LinkCategory.MAPS,
            R.drawable.ic_link_wheelmap,
            R.string.link_wheelmap_description
        ),
        Link(
            "mapy_tactile",
            "https://hapticke.mapy.cz/?x=14.4343228&y=50.0652972&z=19&lang=en",
            "Mapy.cz Tactile",
            LinkCategory.MAPS,
            R.drawable.ic_link_mapy_tactile,
            R.string.link_mapy_tactile_description
        ),

        /* -------------------------------------- Showcase ---------------------------------------*/

        Link(
            "openrouteservice-wheelchair",
            "https://maps.openrouteservice.org/directions?b=3",
            "Openrouteservice (Wheelchair)",
            LinkCategory.SHOWCASE,
            R.drawable.ic_link_heigit,
            R.string.link_openrouteservice_wheelchair_description
        ),
        Link(
            "touch_mapper",
            "https://touch-mapper.org",
            "Touch Mapper",
            LinkCategory.SHOWCASE,
            R.drawable.ic_link_touch_mapper,
            R.string.link_touch_mapper_description
        ),


        /* -------------------------------------- Goodies ----------------------------------------*/
        Link(
            "umap",
            "https://umap.openstreetmap.fr",
            "uMap",
            LinkCategory.GOODIES,
            R.drawable.ic_link_umap,
            R.string.link_umap_description
        ),
        Link(
            "myosmatic",
            "https://print.get-map.org",
            "MyOSMatic",
            LinkCategory.GOODIES,
            R.drawable.ic_link_myosmatic,
            R.string.link_myosmatic_description
        ),
        Link(
            "show_me_the_way",
            "https://osmlab.github.io/show-me-the-way",
            "show-me-the-way",
            LinkCategory.GOODIES,
            R.drawable.ic_link_osmlab,
            R.string.link_show_me_the_way_description
        ),
        Link(
            "osm-haiku",
            "https://satellitestud.io/osm-haiku/app",
            "OpenStreetMap Haiku",
            LinkCategory.GOODIES,
            R.drawable.ic_link_haiku,
            R.string.link_osm_haiku_description
        ),
        Link(
            "thenandnow",
            "https://mvexel.github.io/thenandnow/",
            "OSM Then And Now ",
            LinkCategory.GOODIES,
            null,
            R.string.link_neis_one_description
        )
    )

    private val linksById = links.associateBy { it.id }

    private val achievements = listOf(

        Achievement(
            "first_edit",
            R.drawable.ic_achievement_first_edit,
            R.string.achievement_first_edit_title,
            R.string.achievement_first_edit_description,
            TotalSolvedQuests,
            { 1 },
            mapOf(),
            1
        ),

        Achievement(
            "surveyor",
            R.drawable.ic_achievement_surveyor,
            R.string.achievement_surveyor_title,
            R.string.achievement_surveyor_solved_X,
            TotalSolvedQuests,
            // levels: 10, 30, 60, 100, 150, 210, 280, 360, 450, 550, 660, 780, 910, 1050, ...
            { lvl -> (lvl + 1) * 10 },
            mapOf(
                /* Achievements rewarded for general activity should first cover introduction to OSM
                   and then most of all goodies and general (OSM) showcases */
                1 to links("wiki"), // most important link
                2 to links("welcomemat"),

                4 to links("show_me_the_way"),

                6 to links("myosmatic"),

                8 to links("osm-haiku"),

                10 to links("umap"),

                12 to links("thenandnow")
            )
        ),

        Achievement(
            "regular",
            R.drawable.ic_achievement_regular,
            R.string.achievement_regular_title,
            R.string.achievement_regular_description,
            DaysActive,
            // levels: 4, 8, 12, 16, 20, 24, 28, 32, 36, 40, ...
            { 4 },
            mapOf(
                /* Achievements rewarded for regular activity should focus mostly on introducing
                   user to the community and other editors. Introducing editors should be delayed
                   because users should not get sidetracked too early - best first show community
                   intro links */
                1 to links("weeklyosm"), // newspaper first
                2 to links("pic4review"), // mentioning it early because it is very easy to use
                3 to links("neis-one"), // who-is-around-me, leaderboards etc fits into "community intro"
                4 to links("ideditor"),
                5 to links("learnosm"), // learnosm mostly concerns itself with tutorials about how to use editors
                6 to links("disaster.ninja"),
                7 to links("vespucci", "josm") // together because both are full-featured-editors for each their platform
            )
        ),

        Achievement(
            "rare",
            R.drawable.ic_achievement_rare,
            R.string.achievement_rare_title,
            R.string.achievement_rare_solved_X,
            SolvedQuestsOfTypes(listOf(
                "AddWheelchairAccessToiletsPart",
                "AddWheelchairAccessOutside",
            )),
            // levels: 3, 9, 18, 30, 45, 63, ...
            { lvl -> (lvl + 1) * 3 },
            mapOf()
        ),

        Achievement(
            "pedestrian",
            R.drawable.ic_achievement_pedestrian,
            R.string.achievement_pedestrian_title,
            R.string.achievement_pedestrian_solved_X,
            SolvedQuestsOfTypes(
                listOf(
                    "AddHandrail",
                    "AddStepsIncline",
                    "AddStepsRamp",
                    "AddFootwayPartSurface",
                    "AddTrafficSignalsButton",
                    "AddPathSurface",
                    "AddCrossingType",
                    "AddProhibitedForPedestrians",
                    "AddSidewalk",
                    "AddCrossingIsland"
                )
            ),
            // levels: 10, 30, 60, 100, 150, 210, 280, 360, 450, 550, 660, 780, 910, 1050, ...
            { lvl -> (lvl + 1) * 10 },
            mapOf(
                1 to links("öpnvkarte")
            )
        ),

        Achievement(
            "blind",
            R.drawable.ic_achievement_blind,
            R.string.achievement_blind_title,
            R.string.achievement_blind_solved_X,
            SolvedQuestsOfTypes(
                listOf(
                    "AddTactilePavingCrosswalk",
                    "AddTrafficSignalsSound",
                    "AddTrafficSignalsVibration",
                    "AddTactilePavingBusStop",
                    "AddCrossingIsland"
                )
            ),
            // levels: 10, 30, 60, 100, 150, 210, 280, 360, 450, 550, 660, 780, 910, 1050, ...
            { lvl -> (lvl + 1) * 10 },
            mapOf(
                1 to links("touch_mapper"),
                2 to links("mapy_tactile")
            )
        ),

        Achievement(
            "wheelchair",
            R.drawable.ic_achievement_wheelchair,
            R.string.achievement_wheelchair_title,
            R.string.achievement_wheelchair_solved_X,
            SolvedQuestsOfTypes(
                listOf(
                    "AddKerbType",
                    "AddPathIncline",
                    "AddPedestrianAccessibleStreetIncline",
                    "AddCyclewayPartWidth",
                    "AddFootwayPartWidth",
                    "AddPathWidth",
                    "AddPedestrianAccessibleStreetWidth",
                    "AddCyclewayPartSurface",
                    "AddFootwayPartSurface",
                    "AddPathSurface",
                    "AddPedestrianAccessibleStreetSurface",
                    "AddPathSmoothness",
                    "AddPedestrianAccessibleStreetSmoothness",
                    "AddFootwayPartSmoothness",
                    "AddWheelchairAccessBusiness",
                    "AddWheelchairAccessOutside",
                    "AddWheelchairAccessPublicTransport",
                    "AddWheelchairAccessToilets",
                    "AddWheelchairAccessToiletsPart",
                    "AddFootwayPartSurface",
                    "AddPathSurface",
                    "AddStepsRamp",
                    "AddHandrail"
                )
            ),
            // levels: 10, 30, 60, 100, 150, 210, 280, 360, 450, 550, 660, 780, 910, 1050, ...
            { lvl -> (lvl + 1) * 10 },
            mapOf(
                1 to links("wheelmap"),
                2 to links("openrouteservice-wheelchair")
            )
        ),
    )

    private fun links(vararg linksKeys: String = emptyArray()): List<Link> =
        linksKeys.map { linksById.getValue(it) }
}
