
package com.sportorganizer.app.profile

enum class UserRole(
    val displayName: String
) {
    COLLEGE_ADMIN("College Admin"),
    ORGANIZER("Organizer"),
    COACH("Coach"),
    SCOREKEEPER("Scorer"),
    STUDENT("Student / Spectator");

    companion object {
        fun fromDatabase(value: String?): UserRole {
            return when (value?.trim()?.lowercase()) {
                "college_admin" -> COLLEGE_ADMIN
                "organizer" -> ORGANIZER
                "coach" -> COACH
                "scorekeeper" -> SCOREKEEPER
                "student" -> STUDENT
                else -> STUDENT
            }
        }
    }
}
