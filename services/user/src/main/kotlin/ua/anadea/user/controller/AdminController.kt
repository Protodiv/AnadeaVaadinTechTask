package ua.anadea.user.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import ua.anadea.user.dto.CreateUserRequest
import ua.anadea.user.dto.UpdateUserRequest
import ua.anadea.user.dto.UserResponse
import ua.anadea.user.service.AdminService
import java.util.*

@RestController
@RequestMapping("/api/v1/user/admin")
@PreAuthorize("hasRole('ADMIN')")
class AdminController(
    private val adminService: AdminService
) {

    @PostMapping("/user")
    fun createUser(@RequestBody request: CreateUserRequest): ResponseEntity<UserResponse> {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createUser(request))
    }

    @PutMapping("/user/{id}")
    fun updateUser(@PathVariable id: UUID, @RequestBody request: UpdateUserRequest): ResponseEntity<UserResponse> {
        return ResponseEntity.ok(adminService.updateUser(id, request))
    }

    @DeleteMapping("/user/{id}")
    fun deleteUser(@PathVariable id: UUID): ResponseEntity<Void> {
        adminService.deleteUser(id)
        return ResponseEntity.noContent().build()
    }
}
