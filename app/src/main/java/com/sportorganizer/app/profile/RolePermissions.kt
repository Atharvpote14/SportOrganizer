
package com.sportorganizer.app.profile

object RolePermissions {

    fun canManageCollege(role: UserRole): Boolean {
        return role == UserRole.COLLEGE_ADMIN
    }

    fun canManageTeams(role: UserRole): Boolean {
        return role == UserRole.COLLEGE_ADMIN ||
                role == UserRole.ORGANIZER ||
                role == UserRole.COACH
    }

    fun canManageTournaments(role: UserRole): Boolean {
        return role == UserRole.COLLEGE_ADMIN ||
                role == UserRole.ORGANIZER
    }

    fun canScoreMatches(role: UserRole): Boolean {
        return role == UserRole.COLLEGE_ADMIN ||
                role == UserRole.ORGANIZER ||
                role == UserRole.SCOREKEEPER
    }

    fun canViewLiveScores(role: UserRole): Boolean {
        return true
    }
}
