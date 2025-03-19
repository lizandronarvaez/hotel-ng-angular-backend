package com.hotel_ng.app.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hotel_ng.app.dto.AdminDto;
import com.hotel_ng.app.dto.AdminLoginDto;
import com.hotel_ng.app.dto.ResponseDto;
import com.hotel_ng.app.entity.Admin;
import com.hotel_ng.app.enums.UserRole;
import com.hotel_ng.app.exception.OurException;
import com.hotel_ng.app.repository.AdminRepository;
import com.hotel_ng.app.security.utils.JwtUtils;
import com.hotel_ng.app.service.interfaces.AdminService;
import com.hotel_ng.app.utils.Utils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    @Value("${secret.authorization-admin}")
    private String codeAuthorization;

    @Override
    public ResponseDto register(AdminLoginDto loginUserDto) {
        ResponseDto responseDto = new ResponseDto();

        try {

            if (adminRepository.existsByEmail(loginUserDto.getEmail())) {
                throw new OurException("Hubo un error al realizar la operación");
            }
            if (!loginUserDto.getCodeAuthorization().equals(codeAuthorization)) {
                throw new OurException("El código no es válido");
            }


            Admin admin = Admin.builder()
                    .email(loginUserDto.getEmail())
                    .password(passwordEncoder.encode(loginUserDto.getPassword()))
                    .role(UserRole.ROLE_ADMIN)
                    .build();

            Admin adminSaved = adminRepository.save(admin);
            AdminDto adminDto = Utils.mapAdminEntityToAdminDto(adminSaved);

            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setMessage("Cuenta administrador creada correctamente");
            responseDto.setAdmin(adminDto);

        } catch (OurException e) {
            responseDto.setStatusCode(HttpStatus.BAD_REQUEST.value());
            responseDto.setMessage(e.getMessage());

        } catch (Exception e) {
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setMessage("Hubo un error en la operación: " + e.getMessage());
        }
        return responseDto;
    }

    @Override
    public ResponseDto login(AdminLoginDto loginUserDto) {
        ResponseDto responseDto = new ResponseDto();

        try {
            var admin = adminRepository.findByEmail(loginUserDto.getEmail())
                    .orElseThrow(() -> new OurException("Usuario no encontrado"));

            if (!loginUserDto.getCodeAuthorization().equals(codeAuthorization)) {
                throw new OurException("El código no es válido");
            }
            if (!passwordEncoder.matches(loginUserDto.getPassword(), admin.getPassword())) {
                throw new OurException("Email o password incorrecto");
            }

            var token = jwtUtils.generateToken(admin);

            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setMessage("Haz accedido como administrador");
            responseDto.setAdmin(Utils.mapAdminEntityToAdminDto(admin));
            responseDto.setToken(token);
            responseDto.setRole(admin.getRole().name());

        } catch (OurException e) {
            responseDto.setStatusCode(HttpStatus.NOT_FOUND.value());
            responseDto.setMessage("Error al iniciar sesión: " + e.getMessage());

        } catch (Exception e) {
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setMessage("Error realizar el login: " + e.getMessage());
        }
        return responseDto;
    }

}
