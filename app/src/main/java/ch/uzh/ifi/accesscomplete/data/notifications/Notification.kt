package ch.uzh.ifi.accesscomplete.data.notifications

import ch.uzh.ifi.accesscomplete.data.user.achievements.Achievement

sealed class Notification

data class OsmUnreadMessagesNotification(val unreadMessages: Int) : Notification()
data class NewAchievementNotification(val achievement: Achievement, val level: Int): Notification()
data class NewVersionNotification(val sinceVersion: String): Notification()
