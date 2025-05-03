package com.hotel_ng.app.service.impl;

import com.hotel_ng.app.mappers.AdminMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hotel_ng.app.dto.AdminDTO;
import com.hotel_ng.app.dto.request.RequestAdminDTO;
import com.hotel_ng.app.dto.response.ResponseDTO;
import com.hotel_ng.app.entity.Admin;
import com.hotel_ng.app.enums.Role;
import com.hotel_ng.app.exception.OurException;
import com.hotel_ng.app.repository.AdminRepository;
import com.hotel_ng.app.security.utils.JwtUtils;
import com.hotel_ng.app.service.interfaces.AdminService;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Service
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final AdminMapper adminMapper;

    @Value("${secret.authorizationAdmin}")
    private String CODE_AUTHORIZATION;

    @Override
    public ResponseDTO register(RequestAdminDTO requestAdminDTO) {
        ResponseDTO responseDTO = new ResponseDTO();

        try {

            if (adminRepository.existsByEmail(requestAdminDTO.getEmail())) {
                throw new OurException("Hubo un error al realizar la operación");
            }
            if (!requestAdminDTO.getCodeAuthorization().equals(CODE_AUTHORIZATION)) {
                throw new OurException("El código no es válido");
            }

            Admin admin = Admin.builder()
                    .email(requestAdminDTO.getEmail())
                    .password(passwordEncoder.encode(requestAdminDTO.getPassword()))
                    .role(Role.ROLE_ADMIN)
                    .build();

            Admin adminSaved = adminRepository.save(admin);
            AdminDTO adminDTO =adminMapper.mapAdminEntityToAdminDto(adminSaved);

            responseDTO.setStatusCode(HttpStatus.OK.value());
            responseDTO.setMessage("Cuenta administrador creada correctamente");
            responseDTO.setAdmin(adminDTO);

        } catch (OurException e) {
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            responseDTO.setMessage(e.getMessage());

        } catch (Exception e) {
            responseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDTO.setMessage("Hubo un error en la operación: " + e.getMessage());
        }
        return responseDTO;
    }

    @Override
    public ResponseDTO login(RequestAdminDTO requestAdminDTO) {
        ResponseDTO responseDTO = new ResponseDTO();

        try {
            var admin = adminRepository.findByEmail(requestAdminDTO.getEmail())
                    .orElseThrow(() -> new OurException("Usuario no encontrado"));

            if (!requestAdminDTO.getCodeAuthorization().equals(CODE_AUTHORIZATION)) {
                throw new OurException("El código no es válido");
            }

            if (!passwordEncoder.matches(requestAdminDTO.getPassword(), admin.getPassword())) {
                throw new OurException("Email o password incorrecto");
            }

            var token = jwtUtils.generateToken(admin);

            responseDTO.setStatusCode(HttpStatus.OK.value());
            responseDTO.setMessage("Haz accedido como administrador");
            responseDTO.setAdmin(adminMapper.mapAdminEntityToAdminDto(admin));
            responseDTO.setToken(token);
            responseDTO.setRole(admin.getRole().name());

        } catch (OurException e) {
            responseDTO.setStatusCode(HttpStatus.NOT_FOUND.value());
            responseDTO.setMessage("Error al iniciar sesión: " + e.getMessage());

        } catch (Exception e) {
            responseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDTO.setMessage("Error realizar el login: " + e.getMessage());
        }
        return responseDTO;
    }

}
