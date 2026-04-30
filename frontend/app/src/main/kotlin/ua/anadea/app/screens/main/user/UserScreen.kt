package ua.anadea.app.screens.main.user

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.DetachEvent
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.router.BeforeEvent
import com.vaadin.flow.router.HasUrlParameter
import com.vaadin.flow.router.Route
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ua.anadea.app.MainLayout
import ua.anadea.app.screens.NavigationRoute
import ua.anadea.domain.data.UserRole

@Route("user", layout = MainLayout::class)
class UserScreen : KComposite(), KoinComponent, HasUrlParameter<String> {

    private val viewModel: UserViewModel by inject()
    private val screenScope: CoroutineScope by inject()

    private var effectJob: Job? = null
    private var stateJob: Job? = null

    private lateinit var userNameSpan: Span
    private lateinit var userEmailSpan: Span
    private lateinit var userRoleSpan: Span
    private lateinit var userCreatedAtSpan: Span
    private lateinit var userUpdatedAtSpan: Span
    private lateinit var editButton: com.vaadin.flow.component.button.Button

    private var userId: String? = null

    private val root = ui {
        verticalLayout {
            setSizeFull()
            
            button(icon = VaadinIcon.ARROW_LEFT.create()) {
                addThemeVariants(ButtonVariant.LUMO_TERTIARY)
                onLeftClick {
                    viewModel.onEvent(UserContract.Event.BackClicked)
                }
            }

            verticalLayout {
                alignItems = FlexComponent.Alignment.CENTER
                justifyContentMode = FlexComponent.JustifyContentMode.CENTER
                
                h2("User Details")

                div {
                    className = "user-details-card"
                    style.set("background-color", "var(--lumo-contrast-5pct)")
                    style.set("padding", "20px")
                    style.set("border-radius", "8px")
                    style.set("max-width", "400px")
                    style.set("width", "100%")

                    verticalLayout {
                        span("Name: ") { userNameSpan = span("") }
                        span("Email: ") { userEmailSpan = span("") }
                        span("Role: ") { userRoleSpan = span("") }
                        span("Created At: ") { userCreatedAtSpan = span("") }
                        span("Updated At: ") { userUpdatedAtSpan = span("") }

                        editButton = button("Edit User") {
                            addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                            onLeftClick {
                                showEditUserDialog()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showEditUserDialog() {
        val user = viewModel.state.value.targetUser ?: return
        
        val dialog = openDialog {
            headerTitle = "Edit User"
            verticalLayout {
                val nameInput = textField("Name") { 
                    value = user.name
                    focus() 
                }
                val emailInput = textField("Email") {
                    value = user.email
                }
                val roleCombo = comboBox<UserRole>("Role") {
                    setItems(*UserRole.values())
                    value = user.role
                }

                horizontalLayout {
                    button("Cancel") {
                        onLeftClick { this@openDialog.close() }
                    }
                    button("Save") {
                        addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                        onLeftClick {
                            if (nameInput.value.isNotBlank() && emailInput.value.isNotBlank()) {
                                viewModel.onEvent(UserContract.Event.UpdateUser(
                                    nameInput.value,
                                    emailInput.value,
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

    override fun setParameter(event: BeforeEvent, parameter: String) {
        userId = parameter
        userId?.let { viewModel.onEvent(UserContract.Event.LoadUser(it)) }
    }

    override fun onAttach(attachEvent: AttachEvent?) {
        super.onAttach(attachEvent)
        
        val ui = UI.getCurrent()
        
        effectJob = viewModel.effect
            .onEach { effect ->
                ui.access {
                    when (effect) {
                        is UserContract.Effect.ShowError -> Notification.show(effect.message)
                        is UserContract.Effect.ShowNotification -> Notification.show(effect.message)
                        is UserContract.Effect.NavigateBack -> ui.navigate(NavigationRoute.AuthRoute.Dashboard.route)
                    }
                }
            }
            .launchIn(screenScope)

        stateJob = viewModel.state
            .onEach { state ->
                ui.access {
                    state.targetUser?.let { user ->
                        userNameSpan.text = user.name
                        userEmailSpan.text = user.email
                        userRoleSpan.text = user.role.name
                        userCreatedAtSpan.text = user.createdAt?.toString() ?: "N/A"
                        userUpdatedAtSpan.text = user.updatedAt?.toString() ?: "N/A"
                    }

                    editButton.isEnabled = state.currentUser?.role == UserRole.ADMIN
                }
            }
            .launchIn(screenScope)
    }

    override fun onDetach(detachEvent: DetachEvent?) {
        super.onDetach(detachEvent)
        effectJob?.cancel()
        stateJob?.cancel()
    }
}
