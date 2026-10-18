
package com.sportorganizer.app.data.remote.repository


import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.JsonPrimitive
import com.sportorganizer.app.data.remote.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CollegeJoinRequest(
    val id: String,
    @SerialName("college_id")
    val collegeId: String,
    @SerialName("user_id")
    val userId: String,
    val message: String? = null,
    val status: String = "pending",
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("reviewed_at")
    val reviewedAt: String? = null
)

object CollegeJoinRequestRepository {


    suspend fun getMyRequests(): List<CollegeJoinRequest> {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: throw IllegalStateException("Please sign in first.")

        return supabase
            .from("college_join_requests")
            .select {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeList<CollegeJoinRequest>()
    }


    suspend fun getPendingRequestsForMyColleges(): List<CollegeJoinRequest> {
        val myMemberships =
            com.sportorganizer.app.data.remote.supabase
                .from("college_memberships")
                .select {
                    filter {
                        eq("role", "college_admin")
                    }
                }
                .decodeList<CollegeMembership>()

        val adminCollegeIds = myMemberships.map { it.collegeId }

        if (adminCollegeIds.isEmpty()) {
            return emptyList()
        }

        return supabase
            .from("college_join_requests")
            .select {
                filter {
                    eq("status", "pending")
                    isIn("college_id", adminCollegeIds)
                }
            }
            .decodeList<CollegeJoinRequest>()
    }


    suspend fun approveRequest(requestId: String) {
        supabase.postgrest.rpc(
            function = "approve_college_join_request",
            parameters = buildJsonObject {
                put("p_request_id", JsonPrimitive(requestId))
            }
        )
    }

    suspend fun rejectRequest(requestId: String) {
        supabase.postgrest.rpc(
            function = "reject_college_join_request",
            parameters = buildJsonObject {
                put("p_request_id", JsonPrimitive(requestId))
            }
        )
    }


    suspend fun submitRequest(
        collegeId: String,
        message: String? = null
    ) {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: throw IllegalStateException("Please sign in first.")

        val request = buildMap<String, String?> {
            put("college_id", collegeId)
            put("user_id", userId)
            put("message", message?.trim()?.takeIf { it.isNotEmpty() })
        }

        supabase
            .from("college_join_requests")
            .insert(request)

    }

}
