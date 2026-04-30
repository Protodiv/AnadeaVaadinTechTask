package ua.anadea.app.screens.main.dashboard

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.DetachEvent
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.data.value.ValueChangeMode
import com.vaadin.flow.router.Route
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ua.anadea.app.MainLayout
import ua.anadea.app.screens.NavigationRoute
import ua.anadea.domain.data.User
import ua.anadea.domain.data.UserRole

@Route("dashboard", layout = MainLayout::class)
class DashboardScreen : KComposite(), KoinComponent {

    private val viewModel: DashboardViewModel by inject()
    private val screenScope: CoroutineScope by inject()

    private var effectJob: Job? = null
    private var stateJob: Job? = null

    private lateinit var userNameSpan: Span
    private lateinit var userEmailSpan: Span
    private lateinit var userRoleSpan: Span
    private lateinit var userGrid: Grid<User>
    private lateinit var searchField: TextField
    
    private lateinit var nameFiltersLayout: HorizontalLayout
    private lateinit var emailFiltersLayout: HorizontalLayout

    private val root = ui {
        verticalLayout {
            setSizeFull()
            style.set("position", "relative")

            horizontalLayout {
                width = "100%"
                justifyContentMode = FlexComponent.JustifyContentMode.BETWEEN
                alignItems = FlexComponent.Alignment.CENTER
                style.set("background-color", "var(--lumo-contrast-5pct)")
                style.set("padding", "10px 20px")

                horizontalLayout {
                    verticalLayout {
                        horizontalLayout {
                            span("Logged in as: ")
                            userNameSpan = span("")
                        }
                        horizontalLayout {
                            span("Email: ")
                            userEmailSpan = span("")
                            span(" | Role: ")
                            userRoleSpan = span("")
                        }
                    }
                }
            }

            // Search and Filters Section
            verticalLayout {
                style.set("padding", "10px 20px")
                
                // Search Row
                horizontalLayout {
                    alignItems = FlexComponent.Alignment.BASELINE
                    searchField = textField {
                        placeholder = "Search by name or email..."
                        width = "300px"
                        valueChangeTimeout = 1000
                        valueChangeMode = ValueChangeMode.EAGER
                        addValueChangeListener { event ->
                            viewModel.onEvent(DashboardContract.Event.SearchQueryChanged(event.value ?: ""))
                        }
                    }
                    button("Search", VaadinIcon.SEARCH.create()) {
                        addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                        onLeftClick {
                            viewModel.onEvent(DashboardContract.Event.PerformSearch)
                        }
                    }
                }

                // Name Filters Row
                horizontalLayout {
                    alignItems = FlexComponent.Alignment.CENTER
                    span("Name Filters")
                    button(icon = VaadinIcon.PLUS.create()) {
                        addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_ICON)
                        style.set("border-radius", "50%")
                        onLeftClick {
                            showAddFilterDialog("Name") { query ->
                                viewModel.onEvent(DashboardContract.Event.AddNameFilter(query))
                            }
                        }
                    }
                    nameFiltersLayout = horizontalLayout {}
                }

                horizontalLayout {
                    alignItems = FlexComponent.Alignment.CENTER
                    span("Email Filters")
                    button(icon = VaadinIcon.PLUS.create()) {
                        addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_ICON)
                        style.set("border-radius", "50%")
                        onLeftClick {
                            showAddFilterDialog("Email") { query ->
                                viewModel.onEvent(DashboardContract.Event.AddEmailFilter(query))
                            }
                        }
                    }
                    emailFiltersLayout = horizontalLayout {}
                }
            }

            h2("User Management") {
                style.set("margin-left", "20px")
            }

            // User List
            userGrid = grid<User> {
                setSizeFull()
                addColumn(User::name).setHeader("Name").setSortable(true)
                addColumn(User::email).setHeader("Email").setSortable(true)
                addColumn { it.createdAt?.toString() ?: "N/A" }.setHeader("Created At").setSortable(true)
                addColumn { it.updatedAt?.toString() ?: "N/A" }.setHeader("Updated At").setSortable(true)
                
                addColumn(ComponentRenderer { user ->
                    HorizontalLayout().apply {
                        button(icon = VaadinIcon.TRASH.create()) {
                            addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY)
                            isVisible = viewModel.state.value.currentUser?.role == UserRole.ADMIN
                            onLeftClick {
                                viewModel.onEvent(DashboardContract.Event.DeleteUser(user.id))
                            }
                        }
                    }
                }).setHeader("Actions")
            }

            // FAB - Create User Button
            button(icon = VaadinIcon.PLUS.create()) {
                addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_LARGE)
                style.set("position", "fixed")
                style.set("bottom", "20px")
                style.set("right", "20px")
                style.set("border-radius", "50%")
                style.set("width", "60px")
                style.set("height", "60px")
                style.set("box-shadow", "0 4px 10px rgba(0, 0, 0, 0.3)")
                style.set("z-index", "100")
                
                onLeftClick {
                    showCreateUserDialog()
                }
            }
        }
    }

    private fun showCreateUserDialog() {
        val dialog = openDialog {
            headerTitle = "Create New User"
            verticalLayout {
                val nameInput = textField("Name") { focus() }
                val emailInput = textField("Email")
                val passwordInput = passwordField("Password")
                val roleCombo = comboBox<UserRole>("Role") {
                    setItems(*UserRole.values())
                    value = UserRole.USER
                }

                horizontalLayout {
                    button("Cancel") {
                        onLeftClick { this@openDialog.close() }
                    }
                    button("Create") {
                        addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                        onLeftClick {
                            if (nameInput.value.isNotBlank() && emailInput.value.isNotBlank() && passwordInput.value.isNotBlank()) {
                                viewModel.onEvent(DashboardContract.Event.CreateUser(
                                    nameInput.value,
                                    emailInput.value,
                                    passwordInput.value,
                                    roleCombo.value ?: UserRole.USER
                                ))
                                this@openDialog.close()
                            }
                        }
                    }
                }
            }
        }
        dialog.open()
    }

    private fun showAddFilterDialog(title: String, onAdd: (String) -> Unit) {
        val dialog = openDialog {
            headerTitle = "Add $title Filter"
            verticalLayout {
                val input = textField("Search $title") {
                    focus()
                }
                horizontalLayout {
                    button("Cancel") {
                        onLeftClick { this@openDialog.close() }
                    }
                    button("Add") {
                        addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                        onLeftClick {
                            if (input.value.isNotBlank()) {
                                onAdd(input.value)
                                this@openDialog.close()
                            }
                        }
                    }
                }
            }
        }
        dialog.open()
    }

    override fun onAttach(attachEvent: AttachEvent?) {
        super.onAttach(attachEvent)
        
        val ui = UI.getCurrent()
        
        effectJob = viewModel.effect
            .onEach { effect ->
                ui.access {
                    when (effect) {
                        is DashboardContract.Effect.ShowError -> Notification.show(effect.message)
                        is DashboardContract.Effect.NavigateToLogin -> ui.navigate(NavigationRoute.AuthRoute.Login.route)
                        is DashboardContract.Effect.ShowNotification -> Notification.show(effect.message)
                        is DashboardContract.Effect.NavigateToUserDetails -> {
                            ui.navigate("${NavigationRoute.AuthRoute.UserDetails.route}/${effect.userId}")
                        }
                    }
                }
            }
            .launchIn(screenScope)

        stateJob = viewModel.state
            .onEach { state ->
                ui.access {
                    state.currentUser?.let { user ->
                        userNameSpan.text = user.name
                        userEmailSpan.text = user.email
                        userRoleSpan.text = user.role.name
                    }
                    
                    if (searchField.value != state.searchQuery) {
                        searchField.value = state.searchQuery
                    }

                    userGrid.setItems(state.users)
                    userGrid.getDataProvider().refreshAll()
                    
                    updateFilterChips(state.activeFilters)
                }
            }
            .launchIn(screenScope)

        viewModel.onEvent(DashboardContract.Event.LoadDashboard)
    }

    private fun updateFilterChips(filters: List<UserFilter>) {
        nameFiltersLayout.removeAll()
        emailFiltersLayout.removeAll()
        
        filters.forEach { filter ->
            val layout = if (filter is UserFilter.ByName) nameFiltersLayout else emailFiltersLayout
            val query = when(filter) {
                is UserFilter.ByName -> filter.query
                is UserFilter.ByEmail -> filter.query
            }

            layout.horizontalLayout {
                style.set("background-color", "var(--lumo-primary-color-10pct)")
                style.set("border-radius", "16px")
                style.set("padding", "2px 10px")
                alignItems = FlexComponent.Alignment.CENTER
                
                span(query)
                button(icon = VaadinIcon.CLOSE_SMALL.create()) {
                    addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_SMALL)
                    onLeftClick {
                        viewModel.onEvent(DashboardContract.Event.RemoveFilter(filter))
                    }
                }
            }
        }
    }

    override fun onDetach(detachEvent: DetachEvent?) {
        super.onDetach(detachEvent)
        effectJob?.cancel()
        stateJob?.cancel()
    }
}
