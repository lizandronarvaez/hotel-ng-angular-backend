package com.hotel_ng.app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import com.hotel_ng.app.dto.*;
import com.hotel_ng.app.service.interfaces.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users/auth")
@RequiredArgsConstructor
public class AuthClientController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ResponseDto> registerUser(@Valid @RequestBody RegisterUserDto loginUserDto) {
        ResponseDto response = userService.register(loginUserDto);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDto> loginUser(@Valid @RequestBody LoginUserDto loginUserDto) throws MethodArgumentNotValidException {
        ResponseDto response = userService.login(loginUserDto);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
