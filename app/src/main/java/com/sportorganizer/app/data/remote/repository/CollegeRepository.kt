
package com.sportorganizer.app.data.remote.repository

import com.sportorganizer.app.data.remote.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val id: String,
    @SerialName("display_name")
    val displayName: String = "",
    @SerialName("avatar_url")
    val avatarUrl: String? = null
)

@Serializable
data class CollegeMembership(
    val id: String,
    @SerialName("college_id")
    val collegeId: String,
    @SerialName("user_id")
    val userId: String,
    val role: String,
    @SerialName("created_at")
    val createdAt: String? = null
)

@Serializable
data class College(
    val id: String,
    val name: String,
    @SerialName("short_name")
    val shortName: String? = null,
    val city: String? = null,
    val state: String? = null,
    @SerialName("is_active")
    val isActive: Boolean = true
)

object CollegeRepository {

    suspend fun getCurrentProfile(): UserProfile? {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: return null

        return supabase
            .from("profiles")
            .select {
                filter {
                    eq("id", userId)
                }
            }
            .decodeList<UserProfile>()
            .firstOrNull()
    }

    suspend fun getMyMemberships(): List<CollegeMembership> {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: return emptyList()

        return supabase
            .from("college_memberships")
            .select {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeList<CollegeMembership>()
    }

    suspend fun getActiveColleges(): List<College> {
        return supabase
            .from("colleges")
            .select {
                filter {
                    eq("is_active", true)
                }
            }
            .decodeList<College>()
    }
}
