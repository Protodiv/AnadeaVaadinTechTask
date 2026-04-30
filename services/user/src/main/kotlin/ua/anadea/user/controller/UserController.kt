package ua.anadea.user.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import ua.anadea.user.dto.UserResponse
import ua.anadea.user.service.UserService
import java.util.*

@RestController
@RequestMapping("/api/v1/user")
class UserController(
    private val userService: UserService
) {

    @GetMapping("/userList")
    fun getAllUsers(): ResponseEntity<List<UserResponse>> {
        return ResponseEntity.ok(userService.getAllUsers())
    }

    @GetMapping("/user/{id}")
    fun getUser(@PathVariable id: UUID): ResponseEntity<UserResponse> {
        return ResponseEntity.ok(userService.getUserById(id))
    }

    @GetMapping("/me")
    fun getMe(@AuthenticationPrincipal jwt: Jwt): ResponseEntity<UserResponse> {
        return ResponseEntity.ok(userService.getMyUser(jwt))
    }
}
