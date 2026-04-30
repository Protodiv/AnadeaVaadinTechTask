package ua.anadea.app.screens.auth.introduction

import com.github.mvysny.karibudsl.v10.KComposite
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.h1
import com.github.mvysny.karibudsl.v10.horizontalLayout
import com.github.mvysny.karibudsl.v10.onClick
import com.github.mvysny.karibudsl.v10.verticalLayout
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.router.Route
import ua.anadea.app.screens.NavigationRoute

@Route("")
class IntroductionScreen() : KComposite() {
    private val root = ui {
        verticalLayout {
            setSizeFull()
            justifyContentMode = FlexComponent.JustifyContentMode.CENTER
            alignItems = FlexComponent.Alignment.CENTER

            h1("Anadea Tech Task")

            horizontalLayout {
                button("Login") {
                    onClick {
                        UI.getCurrent().navigate(NavigationRoute.AuthRoute.Login.route)
                    }
                }
                button("Register") {
                    onClick {
                        UI.getCurrent().navigate(NavigationRoute.AuthRoute.Register.route)
                    }
                }
            }
        }
    }
}