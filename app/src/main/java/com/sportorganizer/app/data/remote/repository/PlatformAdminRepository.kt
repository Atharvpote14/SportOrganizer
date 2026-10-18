package com.sportorganizer.app.data.remote.repository

import com.sportorganizer.app.data.remote.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.rpc
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

@Serializable
data class PlatformCollege(
    @SerialName("college_id") val collegeId: String,
    @SerialName("college_name") val collegeName: String,
    @SerialName("short_name") val shortName: String? = null,
    val city: String? = null,
    val state: String? = null,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("admin_count") val adminCount: Long = 0
)

/** Platform-wide operations are always re-authorized by Supabase/RLS or SECURITY DEFINER RPCs. */
object PlatformAdminRepository {
    suspend fun isCurrentUserPlatformAdmin(): Boolean {
        val userId = supabase.auth.currentUserOrNull()?.id ?: return false
        return supabase.from("platform_admins").select {
            filter { eq("user_id", userId) }
        }.decodeList<PlatformAdminRecord>().isNotEmpty()
    }

    suspend fun getColleges(): List<PlatformCollege> =
        supabase.postgrest.rpc(function = "get_platform_colleges").decodeList<PlatformCollege>()

    suspend fun assignInitialCollegeAdmin(collegeId: String, userId: String) {
        supabase.postgrest.rpc(
            function = "assign_initial_college_admin",
            parameters = buildJsonObject {
                put("p_college_id", JsonPrimitive(collegeId.trim()))
                put("p_user_id", JsonPrimitive(userId.trim()))
            }
        )
    }

    @Serializable
    private data class PlatformAdminRecord(@SerialName("user_id") val userId: String)
}
