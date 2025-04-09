package com.hotel_ng.app.service.impl;

import com.hotel_ng.app.dto.LoginUserDto;
import com.hotel_ng.app.dto.RegisterUserDto;
import com.hotel_ng.app.dto.ResponseDto;
import com.hotel_ng.app.dto.UserDto;
import com.hotel_ng.app.entity.Client;
import com.hotel_ng.app.enums.UserRole;
import com.hotel_ng.app.mappers.BookingMapper;
import com.hotel_ng.app.mappers.UserMapper;
import com.hotel_ng.app.repository.UserRepository;
import com.hotel_ng.app.security.utils.JwtUtils;
import com.hotel_ng.app.service.interfaces.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Slf4j
@RequiredArgsConstructor
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserServiceImpl userService;

    private Client CLIENT_PREPARED;
    private Client CLIENT_MODIFIED;
    private UserDto CLIENT_PREPARED_DTO;

    private RegisterUserDto REGISTER_USER_PREPARED;
    private LoginUserDto LOGIN_USER_PREPARED;

    private String MESSAGE_SUCCESS_REGISTER;
    private String MESSAGE_CONFLICT_REGISTER;

    private String MESSAGE_SUCCESS_LOGIN;
    private String MESSAGE_ERROR_LOGIN;

    @BeforeEach
    void setUp() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        CLIENT_PREPARED = Client.builder()
                .id(1L)
                .fullName("cliente prueba")
                .email("cliente@cliente.com")
                .numberPhone("618429871")
                .role(UserRole.ROLE_USER)
                .password(passwordEncoder.encode("password"))
                .build();

        CLIENT_MODIFIED = Client.builder()
                .id(1L)
                .fullName("cliente modificado")
                .email("cliente@modificado.com")
                .numberPhone("618429871")
                .role(UserRole.ROLE_USER)
                .password(passwordEncoder.encode("password"))

                .build();

        CLIENT_PREPARED_DTO = UserDto.builder()
                .id(1L)
                .fullName("cliente prueba")
                .email("cliente@cliente.com")
                .numberPhone("618429871")
                .role(UserRole.ROLE_USER.name())
                .bookings(null)
                .build();

        REGISTER_USER_PREPARED = RegisterUserDto.builder()
                .fullName("cliente prueba")
                .email("cliente@cliente.com")
                .numberPhone("618429871")
                .password(passwordEncoder.encode("password"))
                .build();

        LOGIN_USER_PREPARED = LoginUserDto.builder()
                .email("cliente@cliente.com")
                .password("password")
                .build();

        MESSAGE_SUCCESS_REGISTER = "Usuario registrado con éxito";
        MESSAGE_CONFLICT_REGISTER = "Error al registrar usuario: " + CLIENT_PREPARED_DTO.getEmail() + " ya existe";

        MESSAGE_SUCCESS_LOGIN = "Usuario logueado con éxito";
        MESSAGE_ERROR_LOGIN = "Error al iniciar sesión: Usuario no encontrado";
    }


    @Nested
    class RegisterClient {
        @Test
        void testRegisterClientSuccess() {
            when(userRepository.existsByEmail(CLIENT_PREPARED_DTO.getEmail())).thenReturn(false);
            when(userRepository.save(any(Client.class))).thenReturn(CLIENT_PREPARED);
            when(userMapper.mapUserEntityToUserDto(any(Client.class)
            )).thenReturn(CLIENT_PREPARED_DTO);

            ResponseDto result = userService.register(REGISTER_USER_PREPARED);

            verify(userRepository).save(any(Client.class));
            verify(userRepository, times(1)).save(any(Client.class));
            assertNotNull(result);
            assertEquals(CLIENT_PREPARED_DTO, result.getUser());
            assertEquals(MESSAGE_SUCCESS_REGISTER, result.getMessage());
        }

        @Test
        void testRegisterClientError() {

            when(userRepository.existsByEmail(CLIENT_PREPARED_DTO.getEmail())).thenReturn(true);
            ResponseDto result = userService.register(REGISTER_USER_PREPARED);

            verify(userRepository, times(0)).save(any(Client.class));
            assertEquals(HttpStatus.CONFLICT.value(), result.getStatusCode());
            assertEquals(MESSAGE_CONFLICT_REGISTER, result.getMessage());
        }
    }


    @Nested
    class LoginClient {

        @Test
        void testLoginUserSuccess() {
            when(userRepository.findByEmail(LOGIN_USER_PREPARED.getEmail())).thenReturn(Optional.of(CLIENT_PREPARED));
            when(authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            LOGIN_USER_PREPARED.getEmail(),
                            LOGIN_USER_PREPARED.getPassword())))
                    .thenReturn(null);
            when(jwtUtils.generateToken(any(UserDetails.class))).thenReturn("TOKEN-GENERADO");
            when(userMapper.mapUserEntityToUserDto(any(Client.class))).thenReturn(CLIENT_PREPARED_DTO);
            ResponseDto responseDto = userService.login(LOGIN_USER_PREPARED);

            verify(userRepository, times(1)).findByEmail(anyString());
            verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(jwtUtils, times(1)).generateToken(any(UserDetails.class));
            verify(userMapper, times(1)).mapUserEntityToUserDto(any(Client.class));

            assertEquals(HttpStatus.OK.value(), responseDto.getStatusCode());
            assertEquals(MESSAGE_SUCCESS_LOGIN, responseDto.getMessage());
            assertNotNull(responseDto);
        }

        @Test
        void testLoginUserError() {
            when(userRepository.findByEmail(LOGIN_USER_PREPARED.getEmail())).thenReturn(Optional.empty());

            ResponseDto responseDto = userService.login(LOGIN_USER_PREPARED);

            verify(userRepository, times(1)).findByEmail(anyString());
            verify(authenticationManager, times(0)).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(jwtUtils, times(0)).generateToken(any(UserDetails.class));

            assertEquals(HttpStatus.NOT_FOUND.value(), responseDto.getStatusCode());
            assertEquals(MESSAGE_ERROR_LOGIN, responseDto.getMessage());
            assertNotNull(responseDto);
        }
    }

    @Test
    void getAllUsers() {
        List<Client> listUsers = List.of(CLIENT_PREPARED);
        List<UserDto> listUsersDto = List.of(CLIENT_PREPARED_DTO);

        when(userRepository.findAll()).thenReturn(listUsers);
        when(userMapper.mapUserListEntityToUserDtoList(listUsers)).thenReturn(listUsersDto);

        ResponseDto response = userService.getAllUsers();

        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(1)).mapUserListEntityToUserDtoList(listUsers);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals("Lista de usuarios", response.getMessage());
        assertEquals(listUsersDto, response.getUserList());
    }

    @Test
    void getUserBookingHistory() {

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(CLIENT_PREPARED));
        when(userMapper.mapUserEntityToUserDtoWithBookingAndRoom(any(Client.class), any(BookingMapper.class))).thenReturn(CLIENT_PREPARED_DTO);

        ResponseDto responseDto = userService.getUserBookingHistory(String.valueOf(CLIENT_PREPARED.getId()));

        verify(userRepository, times(1)).findById(anyLong());
        verify(userMapper, times(1)).mapUserEntityToUserDtoWithBookingAndRoom(any(Client.class), any(BookingMapper.class));

        assertEquals(HttpStatus.OK.value(),responseDto.getStatusCode());
        assertEquals("¡Operación exitosa!", responseDto.getMessage());
        assertEquals(CLIENT_PREPARED_DTO,responseDto.getUser());
    }

    @Test
    void getUserById() {
    }

    @Test
    void getUserProfile() {
    }

    @Test
    void deleteUser() {
    }

    @Test
    void formUserQuestion() {
    }
}