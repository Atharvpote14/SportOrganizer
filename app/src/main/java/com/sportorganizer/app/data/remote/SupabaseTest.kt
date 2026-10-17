
package com.sportorganizer.app.data.remote

import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConnectionTest(
    val id: Long,
    val message: String
)

suspend fun fetchConnectionTest(): List<ConnectionTest> {
    return supabase
        .from("connection_test")
        .select()
        .decodeList<ConnectionTest>()
}
