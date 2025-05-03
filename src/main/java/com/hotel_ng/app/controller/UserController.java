package com.hotel_ng.app.controller;

import com.hotel_ng.app.dto.request.RequestFormQuestionDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.hotel_ng.app.dto.response.ResponseDTO;
import com.hotel_ng.app.service.interfaces.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Tag(name = "Administradores")
    @GetMapping("/all-users")
    public ResponseEntity<ResponseDTO> getAllUsers() {
        ResponseDTO response = userService.getAllUsers();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @Tag(name = "Administradores")
    @GetMapping("/get-user/{userId}")
    public ResponseEntity<ResponseDTO> getUserById(@PathVariable("userId") String userId) {
        ResponseDTO response = userService.getUserById(userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @Tag(name = "Administradores")
    @GetMapping("/get-profile-user-info")
    public ResponseEntity<ResponseDTO> getLoggedUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        ResponseDTO response = userService.getUserProfile(email);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @Tag(name = "Administradores")
    @GetMapping("/get-user-bookings/{userId}")
    public ResponseEntity<ResponseDTO> getHistoryBookingByUser(@PathVariable("userId") String userId) {
        ResponseDTO response = userService.getUserBookingHistory(userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @Tag(name = "Administradores")
    @DeleteMapping("/delete-user/{userId}")
    public ResponseEntity<ResponseDTO> deleteUser(@PathVariable("userId") String userId) {
        ResponseDTO response = userService.deleteUser(userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @Tag(name = "Público")
    @PostMapping("/form-contact")
    public ResponseEntity<ResponseDTO> receiveMessageUser(@RequestBody RequestFormQuestionDTO formQuestionDTO) {

        ResponseDTO response = userService.formUserQuestion(formQuestionDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
