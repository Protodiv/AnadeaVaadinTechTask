package ua.anadea.app.screens.main.dashboard

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ua.anadea.domain.data.User
import ua.anadea.domain.data.UserRole
import ua.anadea.domain.repository.AdminRepository
import ua.anadea.domain.repository.UserRepository

class DashboardViewModel(
    private val userRepository: UserRepository,
    private val adminRepository: AdminRepository,
    private val viewModelScope: CoroutineScope
) {
    private val _state = MutableStateFlow(DashboardContract.State())
    val state = _state.asStateFlow()

    private val _effect = Channel<DashboardContract.Effect>()
    val effect = _effect.receiveAsFlow()

    private fun setEffect(effect: DashboardContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }

    fun onEvent(event: DashboardContract.Event) {
        when (event) {
            is DashboardContract.Event.LoadDashboard -> loadDashboardData()
            is DashboardContract.Event.RefreshUsers -> loadUsers()
            is DashboardContract.Event.AddNameFilter -> addFilter(UserFilter.ByName(event.query))
            is DashboardContract.Event.AddEmailFilter -> addFilter(UserFilter.ByEmail(event.query))
            is DashboardContract.Event.RemoveFilter -> removeFilter(event.filter)
            is DashboardContract.Event.SearchQueryChanged -> {
                _state.value = _state.value.copy(searchQuery = event.query)
            }
            is DashboardContract.Event.PerformSearch -> performSearch()
            is DashboardContract.Event.DeleteUser -> deleteUser(event.userId)
            is DashboardContract.Event.CreateUser -> createUser(event.name, event.email, event.password, event.role)
        }
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val me = async { userRepository.getMe() }
                val users = async { userRepository.getAllUsers() }
                _state.value = _state.value.copy(
                    currentUser = me.await(),
                    users = applyFilters(users.await(), _state.value.activeFilters)
                )
                setEffect(DashboardContract.Effect.ShowNotification("Downloaded: ${_state.value.users.size} users"))

            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
                setEffect(DashboardContract.Effect.ShowError(e.message ?: "Failed to load dashboard data"))
            }
        }
    }

    private fun loadUsers() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                _state.value = _state.value.copy(
                    users = applyFilters(userRepository.getAllUsers(), _state.value.activeFilters)
                )
            } catch (e: Exception) {
                setEffect(DashboardContract.Effect.ShowError(e.message ?: "Failed to refresh users"))
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    private fun addFilter(filter: UserFilter) {
        if (_state.value.activeFilters.contains(filter)) return

        val newFilters = _state.value.activeFilters + filter
        _state.value = _state.value.copy(
            activeFilters = newFilters,
            users = applyFilters(_state.value.users, newFilters)
        )
    }

    private fun removeFilter(filter: UserFilter) {
        viewModelScope.launch {
            val newFilters = _state.value.activeFilters - filter
            _state.value = _state.value.copy(
                activeFilters = newFilters,
                users = applyFilters(userRepository.getAllUsers(), newFilters)
            )
        }
    }

    private fun performSearch() {
        val query = _state.value.searchQuery
        if (query.isBlank()) {
            setEffect(DashboardContract.Effect.ShowNotification("Please enter a name or email to search"))
            return
        }

        val foundUser = _state.value.users.find {
            it.name.equals(query, ignoreCase = true) || it.email.equals(query, ignoreCase = true)
        }

        if (foundUser != null) {
            setEffect(DashboardContract.Effect.NavigateToUserDetails(foundUser.id))
        } else {
            setEffect(DashboardContract.Effect.ShowNotification("User not found: $query"))
        }
    }

    private fun deleteUser(userId: String) {
        if (_state.value.currentUser?.id == userId) {
            setEffect(DashboardContract.Effect.ShowNotification("You cannot delete yourself!"))
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                adminRepository.deleteUser(userId)
                setEffect(DashboardContract.Effect.ShowNotification("User deleted successfully"))
                loadUsers()
            } catch (e: Exception) {
                setEffect(DashboardContract.Effect.ShowError(e.message ?: "Failed to delete user"))
            }
            finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    private fun createUser(name: String, email: String, password: String, role: UserRole) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                adminRepository.createUser(name, email, password, role)
                setEffect(DashboardContract.Effect.ShowNotification("User created successfully"))
                loadUsers()
            } catch (e: Exception) {
                setEffect(DashboardContract.Effect.ShowError(e.message ?: "Failed to create user"))
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    private fun applyFilters(users: List<User>, filters: List<UserFilter>): List<User> {
        if (filters.isEmpty()) return users
        
        return users.filter { user ->
            filters.all { filter ->
                when (filter) {
                    is UserFilter.ByName -> user.name.contains(filter.query, ignoreCase = true)
                    is UserFilter.ByEmail -> user.email.contains(filter.query, ignoreCase = true)
                }
            }
        }
    }

    private fun logout() {
        setEffect(DashboardContract.Effect.NavigateToLogin)
    }
}
