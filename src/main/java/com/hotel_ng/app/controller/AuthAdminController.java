package com.hotel_ng.app.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hotel_ng.app.dto.*;
import com.hotel_ng.app.service.interfaces.AdminService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
public class AuthAdminController {

    private final AdminService adminService;

    @Tag(name = "Administradores")
    @PostMapping("/new-account")
    public ResponseEntity<ResponseDto> registerAdmin(@Valid @RequestBody AdminLoginDto adminLoginDtoDto) {
        ResponseDto response = adminService.register(adminLoginDtoDto);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @Tag(name = "Administradores")
    @PostMapping("/login")
    public ResponseEntity<ResponseDto> loginAdmin(@Valid @RequestBody AdminLoginDto adminLoginDto) {
        ResponseDto response = adminService.login(adminLoginDto);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
