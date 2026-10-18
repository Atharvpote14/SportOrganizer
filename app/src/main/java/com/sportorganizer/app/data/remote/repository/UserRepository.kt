
package com.sportorganizer.app.data.remote.repository

import com.sportorganizer.app.data.remote.College
import com.sportorganizer.app.data.remote.CollegeMembership
import com.sportorganizer.app.data.remote.UserProfile
import com.sportorganizer.app.data.remote.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

object UserRepository {

    suspend fun getCurrentProfile(): UserProfile {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: throw IllegalStateException("No signed-in user found.")

        return supabase
            .from("profiles")
            .select {
                filter {
                    eq("id", userId)
                }
            }
            .decodeSingle<UserProfile>()
    }

    suspend fun getCurrentMemberships(): List<CollegeMembership> {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: throw IllegalStateException("No signed-in user found.")

        return supabase
            .from("college_memberships")
            .select {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeList<CollegeMembership>()
    }

    suspend fun getCollege(collegeId: String): College? {
        return supabase
            .from("colleges")
            .select {
                filter {
                    eq("id", collegeId)
                }
            }
            .decodeList<College>()
            .firstOrNull()
    }
}
