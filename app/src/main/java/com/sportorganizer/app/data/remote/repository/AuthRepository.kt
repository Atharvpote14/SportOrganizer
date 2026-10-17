
package com.sportorganizer.app.data.remote.repository

import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import com.sportorganizer.app.data.remote.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

object AuthRepository {

    suspend fun signUp(
        fullName: String,
        email: String,
        password: String
    ) {
        supabase.auth.signUpWith(Email) {
            this.email = email.trim()
            this.password = password
            data = buildJsonObject {
                put("display_name", JsonPrimitive(fullName.trim()))
            }
        }
    }

    suspend fun signIn(
        email: String,
        password: String
    ) {
        supabase.auth.signInWith(Email) {
            this.email = email.trim()
            this.password = password
        }
    }

    suspend fun signOut() {
        supabase.auth.signOut()
    }
}
