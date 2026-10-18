
package com.sportorganizer.app.profile

import com.sportorganizer.app.profile.UserRole
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportorganizer.app.data.remote.repository.College
import com.sportorganizer.app.data.remote.repository.CollegeMembership
import com.sportorganizer.app.data.remote.repository.CollegeRepository
import com.sportorganizer.app.data.remote.repository.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = true,
    val profile: UserProfile? = null,
    val memberships: List<CollegeMembership> = emptyList(),
    val availableColleges: List<College> = emptyList(),
    val error: String? = null
) {
    val roles: Set<UserRole>
        get() = memberships
            .map { UserRole.fromDatabase(it.role) }
            .toSet()
}

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState(isLoading = true)

            try {
                val profile = CollegeRepository.getCurrentProfile()
                val memberships = CollegeRepository.getMyMemberships()
                val colleges = CollegeRepository.getActiveColleges()

                _uiState.value = ProfileUiState(
                    isLoading = false,
                    profile = profile,
                    memberships = memberships,
                    availableColleges = colleges
                )
            } catch (e: Exception) {
                _uiState.value = ProfileUiState(
                    isLoading = false,
                    error = e.message ?: "Unable to load your profile."
                )
            }
        }
    }
}
