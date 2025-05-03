package com.hotel_ng.app.controller;

import com.hotel_ng.app.dto.request.RequestLoginUserDTO;
import com.hotel_ng.app.dto.request.RequestRegisterUserDTO;
import com.hotel_ng.app.dto.response.ResponseDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import com.hotel_ng.app.service.interfaces.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users/auth")
@RequiredArgsConstructor
public class AuthUserController {

    private final UserService userService;

    @Tag(name = "Usuarios")
    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> registerUser(@Valid @RequestBody RequestRegisterUserDTO requestRegisterUserDTO) {
        ResponseDTO response = userService.register(requestRegisterUserDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @Tag(name = "Usuarios")
    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> loginUser(@Valid @RequestBody RequestLoginUserDTO requestLoginUserDTO) throws MethodArgumentNotValidException {
        ResponseDTO response = userService.login(requestLoginUserDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
