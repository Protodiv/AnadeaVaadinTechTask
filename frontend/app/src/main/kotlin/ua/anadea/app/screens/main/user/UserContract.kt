package ua.anadea.app.screens.main.user

import ua.anadea.domain.data.User
import ua.anadea.domain.data.UserRole

class UserContract {
    data class State(
        val currentUser: User? = null,
        val targetUser: User? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    )

    sealed class Event {
        data class LoadUser(val userId: String) : Event()
        data class UpdateUser(val name: String, val email: String, val role: UserRole) : Event()
        object BackClicked : Event()
    }

    sealed class Effect {
        data class ShowError(val message: String) : Effect()
        data class ShowNotification(val message: String) : Effect()
        object NavigateBack : Effect()
    }
}
