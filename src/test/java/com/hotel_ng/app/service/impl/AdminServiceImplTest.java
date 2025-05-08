package com.hotel_ng.app.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hotel_ng.app.dto.AdminDTO;
import com.hotel_ng.app.dto.request.RequestAdminDTO;
import com.hotel_ng.app.dto.response.ResponseDTO;
import com.hotel_ng.app.entity.Admin;
import com.hotel_ng.app.enums.Role;
import com.hotel_ng.app.mappers.AdminMapper;
import com.hotel_ng.app.repository.AdminRepository;
import com.hotel_ng.app.security.utils.JwtUtils;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {
    @Mock
    private AdminRepository adminRepository;

    @Mock
    private AdminMapper adminMapper;

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtils jwtUtils;


    @InjectMocks
    private AdminServiceImpl adminServiceImpl;

    private Admin ADMIN_PREPARED;
    private AdminDTO ADMIN_PREPARED_DTO;

    private RequestAdminDTO REQUEST_AUTH_PREPARED_DTO;
    private ResponseDTO responseDTO;

    @BeforeEach
    void setUp() {

        ADMIN_PREPARED = Admin.builder()
                .email("prueba@admin.es")
                .password("contraseñaADMIN")
                .role(Role.ROLE_ADMIN)
                .build();

        ADMIN_PREPARED_DTO = AdminDTO.builder()
                .id(1L)
                .email("prueba@admin.es")
                .role(Role.ROLE_ADMIN)
                .build();

        REQUEST_AUTH_PREPARED_DTO = RequestAdminDTO.builder()
                .email("prueba@admin.es")
                .password("contraseñaADMIN")
                .build();

        responseDTO = ResponseDTO.builder().statusCode(HttpStatus.OK.value())
                .message("Cuenta creada")
                .admin(ADMIN_PREPARED_DTO)
                .build();

    }

    @Nested
    class RegisterAdmin {
        private final static String CODE_AUTHORIZATION = "CODIGO_AUTORIZACION";
        RequestAdminDTO testDTO = RequestAdminDTO.builder()
                .email("prueba@admin.es")
                .password("contraseñaADMIN")
                .codeAuthorization(CODE_AUTHORIZATION)
                .build();

        @Test
        void testRegisterAdminSuccess() {
            ReflectionTestUtils.setField(adminServiceImpl, "CODE_AUTHORIZATION", CODE_AUTHORIZATION);

            when(adminRepository.existsByEmail("prueba@admin.es")).thenReturn(false);
            when(adminRepository.save(any(Admin.class))).thenReturn(ADMIN_PREPARED);
            when(adminMapper.mapAdminEntityToAdminDto(any())).thenReturn(ADMIN_PREPARED_DTO);


            adminServiceImpl.register(testDTO);

            verify(adminRepository, times(1)).existsByEmail(anyString());
            verify(adminRepository, times(1)).save(any(Admin.class));
            verify(adminMapper, times(1)).mapAdminEntityToAdminDto(any(Admin.class));
            verify(passwordEncoder, times(1)).encode(anyString());
        }

        @Test
        void testRegisterAdminError() {
            ReflectionTestUtils.setField(adminServiceImpl, "CODE_AUTHORIZATION", CODE_AUTHORIZATION);

            when(adminRepository.existsByEmail("prueba@admin.es")).thenReturn(true);

            adminServiceImpl.register(testDTO);

            verify(adminRepository, times(1)).existsByEmail(anyString());
            verify(adminRepository, times(0)).save(any(Admin.class));
            verify(adminMapper, times(0)).mapAdminEntityToAdminDto(any(Admin.class));
            verify(passwordEncoder, times(0)).encode(anyString());
        }
    }

    @Nested
    class LoginAdmin {
        private final static String CODE_AUTHORIZATION = "CODIGO_AUTORIZACION";

        @Test
        void testLoginSuccess() {
            when(adminRepository.findByEmail(anyString())).thenReturn(Optional.of(ADMIN_PREPARED));

            adminServiceImpl.login(REQUEST_AUTH_PREPARED_DTO);

            verify(adminRepository, times(1)).findByEmail(anyString());
        }

        @Test
        void testLoginError(){
            when(adminRepository.findByEmail(anyString())).thenReturn(Optional.empty());

            adminServiceImpl.login(REQUEST_AUTH_PREPARED_DTO);

            verify(adminRepository, times(1)).findByEmail(anyString());
        }
    }
}