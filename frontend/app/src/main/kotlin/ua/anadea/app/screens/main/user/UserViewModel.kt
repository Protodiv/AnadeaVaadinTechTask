package ua.anadea.app.screens.main.user

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ua.anadea.domain.data.UserRole
import ua.anadea.domain.repository.AdminRepository
import ua.anadea.domain.repository.UserRepository

class UserViewModel(
    private val userRepository: UserRepository,
    private val adminRepository: AdminRepository,
    private val viewModelScope: CoroutineScope
) {
    private val _state = MutableStateFlow(UserContract.State())
    val state = _state.asStateFlow()

    private val _effect = Channel<UserContract.Effect>()
    val effect = _effect.receiveAsFlow()

    private fun setEffect(effect: UserContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }

    fun onEvent(event: UserContract.Event) {
        when (event) {
            is UserContract.Event.LoadUser -> loadUserData(event.userId)
            is UserContract.Event.UpdateUser -> handleUpdateUser(event.name, event.email, event.role)
            is UserContract.Event.BackClicked -> setEffect(UserContract.Effect.NavigateBack)
        }
    }

    private fun loadUserData(userId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val me = userRepository.getMe()
                val targetUser = userRepository.getUser(userId)
                
                _state.value = _state.value.copy(
                    currentUser = me,
                    targetUser = targetUser,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
                setEffect(UserContract.Effect.ShowError(e.message ?: "Failed to load user data"))
            }
        }
    }

    private fun handleUpdateUser(name: String, email: String, role: UserRole) {
        val currentUser = _state.value.currentUser
        val targetUser = _state.value.targetUser
        if (currentUser?.role == UserRole.ADMIN && targetUser != null) {
            viewModelScope.launch {
                try {
                    val updatedUser = targetUser.copy(
                        name = name,
                        email = email,
                        role = role
                    )
                    adminRepository.updateUser(updatedUser)
                    _state.value = _state.value.copy(targetUser = updatedUser)
                    setEffect(UserContract.Effect.ShowNotification("User updated successfully"))
                } catch (e: Exception) {
                    setEffect(UserContract.Effect.ShowError(e.message ?: "Failed to update user"))
                }
            }
        } else {
            setEffect(UserContract.Effect.ShowNotification("Unauthorized: Admin role required"))
        }
    }
}
