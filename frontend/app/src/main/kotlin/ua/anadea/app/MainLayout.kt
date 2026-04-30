package ua.anadea.app

import com.github.mvysny.karibudsl.v10.KComposite
import com.github.mvysny.karibudsl.v10.verticalLayout
import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.DetachEvent
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.RouterLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ua.anadea.app.screens.NavigationRoute
import ua.anadea.data.token.TokenManager
import ua.anadea.domain.data.token.AuthState

class MainLayout : KComposite(), RouterLayout, KoinComponent {
    private val tokenManager: TokenManager by inject()
    private val scope: CoroutineScope by inject()
    private var authJob: Job? = null

    private val root = ui {
        verticalLayout {
            setSizeFull()
        }
    }

    override fun onAttach(attachEvent: AttachEvent?) {
        super.onAttach(attachEvent)
        val ui = UI.getCurrent()
        authJob = tokenManager.authState
            .onEach { state ->
                ui.access {
                    println("Auth state changed: $state")
                    if (state == AuthState.LoggedOut) {
                        println("Logged out")
                        ui.navigate(NavigationRoute.AuthRoute.Login.route)
                    }
                }
            }
            .launchIn(scope)
    }

    override fun onDetach(detachEvent: DetachEvent?) {
        super.onDetach(detachEvent)
        authJob?.cancel()
    }
}
