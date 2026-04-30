package ua.anadea.gateway.config

import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GatewayConfig(
    private val urlsConfig: UrlsConfig
) {
    @Bean
    fun routeLocator(builder: RouteLocatorBuilder): RouteLocator {
        return builder.routes()
            .route("auth") { r ->
                r.path("/api/v1/auth/**")
                    .uri(urlsConfig.authServiceUrl)
            }
            .route("user") { r ->
                r.path("/api/v1/user/**")
                    .uri(urlsConfig.userServiceUrl)
            }
            .build()
    }
}
