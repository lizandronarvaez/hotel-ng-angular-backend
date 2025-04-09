package com.hotel_ng.app.controller;

import com.hotel_ng.app.dto.UserDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.hotel_ng.app.dto.ResponseDto;
import com.hotel_ng.app.service.interfaces.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Tag(name = "Administradores")
    @GetMapping("/all-users")
    public ResponseEntity<ResponseDto> getAllUsers() {
        ResponseDto response = userService.getAllUsers();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @Tag(name = "Administradores")
    @GetMapping("/get-user/{userId}")
    public ResponseEntity<ResponseDto> getUserById(@PathVariable("userId") String userId) {
        ResponseDto response = userService.getUserById(userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @Tag(name = "Administradores")
    @GetMapping("/get-profile-user-info")
    public ResponseEntity<ResponseDto> getLoggedUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        ResponseDto response = userService.getUserProfile(email);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @Tag(name = "Administradores")
    @GetMapping("/get-user-bookings/{userId}")
    public ResponseEntity<ResponseDto> getHistoryBookingByUser(@PathVariable("userId") String userId) {
        ResponseDto response = userService.getUserBookingHistory(userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @Tag(name = "Administradores")
    @DeleteMapping("/delete-user/{userId}")
    public ResponseEntity<ResponseDto> deleteUser(@PathVariable("userId") String userId) {
        ResponseDto response = userService.deleteUser(userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @Tag(name = "Público")
    @PostMapping("/form-contact")
    public ResponseEntity<ResponseDto> receiveMessageUser(@RequestBody UserDto userDto) {

        ResponseDto response = userService.formUserQuestion(userDto);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
