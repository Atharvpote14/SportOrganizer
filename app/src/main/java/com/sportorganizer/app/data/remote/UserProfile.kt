
package com.sportorganizer.app.data.remote

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
    val state: String? = null
)

data class UserCollegeAccess(
    val membership: CollegeMembership,
    val college: College?
)
