package com.hotel_ng.app.service.impl;

import com.hotel_ng.app.dto.LoginUserDto;
import com.hotel_ng.app.dto.RegisterUserDto;
import com.hotel_ng.app.dto.UserDto;
import com.hotel_ng.app.entity.Client;
import com.hotel_ng.app.enums.UserRole;
import com.hotel_ng.app.mappers.BookingMapper;
import com.hotel_ng.app.mappers.UserMapper;
import com.hotel_ng.app.repository.UserRepository;
import com.hotel_ng.app.security.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

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
    private EmailServiceImpl emailService;

    @InjectMocks
    private UserServiceImpl userService;

    private Client CLIENT_PREPARED;
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

            userService.register(REGISTER_USER_PREPARED);

            verify(userRepository).save(any(Client.class));
            verify(userRepository, times(1)).save(any(Client.class));
        }

        @Test
        void testRegisterClientError() {

            when(userRepository.existsByEmail(CLIENT_PREPARED_DTO.getEmail())).thenReturn(true);

            userService.register(REGISTER_USER_PREPARED);

            verify(userRepository, times(0)).save(any(Client.class));
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

            userService.login(LOGIN_USER_PREPARED);

            verify(userRepository, times(1)).findByEmail(anyString());
            verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(jwtUtils, times(1)).generateToken(any(UserDetails.class));
            verify(userMapper, times(1)).mapUserEntityToUserDto(any(Client.class));
        }

        @Test
        void testLoginUserError() {
            when(userRepository.findByEmail(LOGIN_USER_PREPARED.getEmail())).thenReturn(Optional.empty());

            userService.login(LOGIN_USER_PREPARED);

            verify(userRepository, times(1)).findByEmail(anyString());
            verify(authenticationManager, times(0)).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(jwtUtils, times(0)).generateToken(any(UserDetails.class));
        }
    }

    @Test
    void testGetAllUsers() {
        List<Client> listUsers = List.of(CLIENT_PREPARED);
        List<UserDto> listUsersDto = List.of(CLIENT_PREPARED_DTO);

        when(userRepository.findAll()).thenReturn(listUsers);
        when(userMapper.mapUserListEntityToUserDtoList(listUsers)).thenReturn(listUsersDto);

        userService.getAllUsers();

        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(1)).mapUserListEntityToUserDtoList(listUsers);
    }

    @Test
    void testGetUserBookingHistory() {

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(CLIENT_PREPARED));
        when(userMapper.mapUserEntityToUserDtoWithBookingAndRoom(any(Client.class), any(BookingMapper.class))).thenReturn(CLIENT_PREPARED_DTO);

        userService.getUserBookingHistory(String.valueOf(CLIENT_PREPARED.getId()));

        verify(userRepository, times(1)).findById(anyLong());
        verify(userMapper, times(1)).mapUserEntityToUserDtoWithBookingAndRoom(any(Client.class), isNull());
    }

    @Nested
    class GetUser {
        @Test
        void testGetUserById_Success() {
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(CLIENT_PREPARED));
            when(userMapper.mapUserEntityToUserDto(any(Client.class))).thenReturn(CLIENT_PREPARED_DTO);

            userService.getUserById(String.valueOf(CLIENT_PREPARED.getId()));

            verify(userRepository, times(1)).findById(anyLong());
            verify(userMapper, times(1)).mapUserEntityToUserDto(any(Client.class));

        }

        @Test
        void testGetUserById_Error() {
            when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

            userService.getUserById(String.valueOf(CLIENT_PREPARED.getId()));

            verify(userRepository, times(1)).findById(anyLong());
            verify(userMapper, times(0)).mapUserEntityToUserDto(any(Client.class));
        }
    }

    @Nested
    class DeleteUser {
        @Test
        void testDeleteUserById_Success() {
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(CLIENT_PREPARED));

            userService.deleteUser(String.valueOf(CLIENT_PREPARED.getId()));

            verify(userRepository, times(1)).findById(anyLong());
            verify(userRepository, times(1)).deleteById(anyLong());
        }

        @Test
        void testDeleteUserById_Error() {
            when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

            userService.deleteUser(String.valueOf(CLIENT_PREPARED.getId()));

            verify(userRepository, times(1)).findById(anyLong());
            verify(userRepository, times(0)).deleteById(anyLong());
        }
    }

    @Test
    void testFormUserQuestion() {
        doNothing().when(emailService).sendEmail(CLIENT_PREPARED_DTO);

        userService.formUserQuestion(CLIENT_PREPARED_DTO);

        verify(emailService, atLeastOnce()).sendEmail(CLIENT_PREPARED_DTO);
    }
}