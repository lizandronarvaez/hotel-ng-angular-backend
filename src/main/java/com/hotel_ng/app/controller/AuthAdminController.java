package com.hotel_ng.app.controller;

import com.hotel_ng.app.dto.request.RequestAdminDTO;
import com.hotel_ng.app.dto.response.ResponseDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hotel_ng.app.service.interfaces.AdminService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
public class AuthAdminController {

    private final AdminService adminService;

    @Tag(name = "Administradores")
    @PostMapping("/new-account")
    public ResponseEntity<ResponseDTO> registerAdmin(@Valid @RequestBody RequestAdminDTO requestAdminDTO) {
        ResponseDTO response = adminService.register(requestAdminDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @Tag(name = "Administradores")
    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> loginAdmin(@Valid @RequestBody RequestAdminDTO requestAdminDTO) {
        ResponseDTO response = adminService.login(requestAdminDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
