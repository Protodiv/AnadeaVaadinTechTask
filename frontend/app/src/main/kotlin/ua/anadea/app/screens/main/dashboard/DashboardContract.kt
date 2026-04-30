package ua.anadea.app.screens.main.dashboard

import ua.anadea.domain.data.User
import ua.anadea.domain.data.UserRole

sealed class UserFilter {
    data class ByName(val query: String) : UserFilter()
    data class ByEmail(val query: String) : UserFilter()
}

class DashboardContract {
    data class State(
        val currentUser: User? = null,
        val users: List<User> = emptyList(),
        val activeFilters: List<UserFilter> = emptyList(),
        val searchQuery: String = "",
        val isLoading: Boolean = false,
        val error: String? = null
    )

    sealed class Event {
        object LoadDashboard : Event()
        object RefreshUsers : Event()
        
        data class AddNameFilter(val query: String) : Event()
        data class AddEmailFilter(val query: String) : Event()
        data class RemoveFilter(val filter: UserFilter) : Event()
        
        data class SearchQueryChanged(val query: String) : Event()
        object PerformSearch : Event()
        
        data class DeleteUser(val userId: String) : Event()
        data class CreateUser(val name: String, val email: String, val password: String, val role: UserRole) : Event()
    }

    sealed class Effect {
        data class ShowError(val message: String) : Effect()
        data class ShowNotification(val message: String) : Effect()
        object NavigateToLogin : Effect()
        data class NavigateToUserDetails(val userId: String) : Effect()
    }
}
