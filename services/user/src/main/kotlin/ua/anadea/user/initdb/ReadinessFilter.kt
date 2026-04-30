package ua.anadea.user.initdb

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Component
class ReadinessFilter(
    private val dataInitializer: UserDataInitializer
) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (!dataInitializer.isInitialized()) {
            response.sendError(HttpStatus.SERVICE_UNAVAILABLE.value(), "Database not initialized yet")
            return
        }
        filterChain.doFilter(request, response)
    }
}