# About

AccessComplete is an **unreleased** fork of [StreetComplete](https://github.com/streetcomplete/StreetComplete) which is an easy-to-use Android editor for [OpenStreetMap (OSM)](https://www.openstreetmap.org). Both projects make use of a quest system that does not require any previous OSM knowledge in order make a contribution. Quests are automatically generated for OSM elements in the user's vicinity where some kind of survey is necessary. All available quests are displayed as markers on a map. To complete a quest, a user only needs to provide an answer to a simple question. Based on this answer, the app will then generate a meaningful OSM contribution using the user's OSM account.

Contrary to the original project, AccessComplete focuses on collecting (wheelchair related) accessibility data. Although it would have been possible to contribute quests that are related to such data directly to StreetComplete, certain things like barriers or construction sites cannot be covered by a quest-based system. This is due to the fact that it is unknown where information about them is still missing. In order to combine different data collection approaches in the future, the decision was made to create a forked version of StreetComplete.

In its current version, AccessComplete provides quests to map the surface material, smoothness quality, maximum incline, and minimum width of ways that are relevant for wheelchair routing. Furthermore, the app can be used to collect information about curb types on pedestrian crossings. Details about modifications to the original project can be found [below](#Modifications).

A big thank you belongs to [Tobias Zwick](https://github.com/westnordost/) and all the other contributors of StreetComplete for creating such an awesome and well-maintained open-source application.


# Modifications

AccessComplete is a fork of [StreetComplete v26.2](https://github.com/streetcomplete/StreetComplete/tree/v26.2). Although the architecture of the original project remains unchanged, a multitude of different modifications to its source code have been made:

* Rebranding of the project including its name, logo, and source code (e.g., changing the package names)
* Version reset to 1.0
* New tutorial
* Removal of all quests and achievements that were not directly related to accessibility topics.
* New quests to collect information about the smoothness quality, maximum incline, and minimum width of ways that are relevant for wheelchair routing.
* New quest for the collection of curb type information on pedestrian crossings
* Adaptation of the original surface quests to the new data collection goals
* Quest support for sidewalks that are mapped as a refinement of a street.
* Multi quest markers were added that show how many quests are available for a single OSM element and allow a user to pick a specific one.
* Implementation of an augmented reality (AR) measurement tool based on [Google's ARCore](https://developers.google.com/ar) and [Sceneform](https://developers.google.com/sceneform/develop) in order to measure the width of a way.
* Adjustments for external service dependencies such as the [OSM API](https://wiki.openstreetmap.org/wiki/API_v0.6) and [Jawg Maps](https://www.jawg.io) (see also [below](#Development))
* Support for Android API level < 24 was dropped due to adding ARCore as a dependency.
* GPLv3 license headers were added to all source code files in order to clarify the copyright.
* Reformatting of some parts of the source code.


# License

This software is released under the terms of the [GNU General Public License](http://www.gnu.org/licenses/gpl-3.0.html).


# Important Dependencies

* [Tangram-ES](https://github.com/tangrams/tangram-es/) for rendering the map
* [countryboundaries](https://github.com/westnordost/countryboundaries) for detecting, in which country a quest is (affects quest display, etc.)
* [osmapi](https://github.com/westnordost/osmapi) for communication with the OSM API
* [osmfeatures](https://github.com/westnordost/osmfeatures) to correctly refer to a feature by name
* [ARCore](https://developers.google.com/ar) for the AR measurement tool
* [Sceneform](https://developers.google.com/sceneform/develop) for the AR measurement tool


# Created for StreetComplete and Used By AccessComplete

* [NotesReview](https://github.com/ENT8R/NotesReview) by [@ENT8R](https://github.com/ENT8R) for reviewing notes with a specific keyword (here: `AccessComplete`)
* [streetcomplete-mapstyle](https://github.com/ENT8R/streetcomplete-mapstyle) by [@ENT8R](https://github.com/ENT8R) maintaining the mapstyle of StreetComplete
* [sc-photo-service](https://github.com/exploide/sc-photo-service) by [@exploide](https://github.com/exploide) allows AccessComplete to upload photos associated with OSM Notes
    * **Note**: An independent version of this service should be deployed if AccessComplete will ever be released.
* [sc-statistics-service](https://github.com/westnordost/sc-statistics-service) by [@westnordost](https://github.com/westnordost) aggregates and provides AccessComplete-related statistics about users.
    * **Note**: Minor adjustments to this service and an independent deployment are needed if AccessComplete will ever be released.


# Development

To build and test AccessComplete [download and install Android Studio](https://developer.android.com/studio/) which comes bundled with all the tools that are needed. Afterwards, checkout this repository, open it in Android Studio, and press the "play" button. Done!

## Configuration of External Services
AccessComplete inherits a few external services from StreetComplete that need to be configured correctly. This is described in the following.

### Jawg Maps
[Jawg Maps](https://www.jawg.io) is a vector tile service that provides the data for the map renderer. Non-commercial products are provided with 50'000 map views per month for free. However, an API Key must be obtained by [creating an account](https://www.jawg.io/en/pricing). The key must then be provided in the following Kotlin object: `ch.uzh.ifi.accesscomplete.map.MapModule`

### OSM API
To upload data in the name of a user via the [OSM API](https://wiki.openstreetmap.org/wiki/API_v0.6), the app must be registered as an OAuth consumer. For more information, see the [OSM Wiki](https://wiki.openstreetmap.org/wiki/OAuth). The registration details that result from this process must then be added to the Kotlin object: `ch.uzh.ifi.accesscomplete.data.user.UserModule`

### sc-statistics-service
As mentioned above, the [sc-statistics-service](https://github.com/westnordost/sc-statistics-service) is needed for the user statistics and must be deployed independently with some minor adjustments. The URL to this service is defined in the Kotlin object `ch.uzh.ifi.accesscomplete.data.user.UserModule`.

The following adjustments must be made before its deployment:
* In the file `get_statistics.php`, change the access restriction check for the HTTP user agent string from `'StreetComplete'` to `'AccessComplete'`.
* In the PHP class `ChangesetsParser`, exchange the string `'StreetComplete:quest_type'` with `'AccessComplete:quest_type'`.

Afterwards, the service can be deployed as described in the README of its source code.

### sc-photo-service
The [sc-photo-service](https://github.com/exploide/sc-photo-service) does not need to be adjusted because the version that StreetComplete uses still works for AccessComplete. However, if AccessComplete is ever released, an independent version of this service should be deployed. The URL can be adjusted in the class `ch.uzh.ifi.accesscomplete.ApplicationConstants`. The deployment process is described in the README of the service's source code.

### Banned Version Checker
AccessComplete inherits a mechanism from StreetComplete that can prevent banned versions of the app from contributing data to OSM (e.g, a version with a critical bug). This is simply done by checking a hosted text file that contains a list of banned versions of the app. Because the version history was reset and development from StreetComplete diverged, an URL of such a file should be provided if AccessComplete will ever be released. The URL can be specified in the Kotlin object `ch.uzh.ifi.accesscomplete.data.upload.UploadModule`.
