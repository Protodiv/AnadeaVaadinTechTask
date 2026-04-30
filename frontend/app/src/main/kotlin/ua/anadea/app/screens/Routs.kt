package ua.anadea.app.screens

sealed interface Route {
    val route: String
}

sealed class  NavigationRoute: Route {
    sealed class AuthRoute: NavigationRoute() {
        internal data object Dashboard : AuthRoute() {
            override val route: String = "dashboard"
        }
        internal data object Introduction : AuthRoute() {
            override val route: String = "" // MAIN start rout
        }
        internal data object Login : AuthRoute()  {
            override val route: String = "login"
        }
        internal data object Register : AuthRoute()  {
            override val route: String = "register"
        }
        internal data object UserDetails : AuthRoute() {
            override val route: String = "user"
        }
    }
}
