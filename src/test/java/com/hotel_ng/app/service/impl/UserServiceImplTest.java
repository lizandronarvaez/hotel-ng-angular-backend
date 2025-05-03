package com.hotel_ng.app.service.impl;

import com.hotel_ng.app.dto.request.RequestFormQuestionDTO;
import com.hotel_ng.app.dto.request.RequestLoginUserDTO;
import com.hotel_ng.app.dto.request.RequestRegisterUserDTO;
import com.hotel_ng.app.dto.UserDTO;
import com.hotel_ng.app.entity.User;
import com.hotel_ng.app.enums.Role;
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

    private User USER_PREPARED;
    private UserDTO USER_PREPARED_DTO;

    private RequestRegisterUserDTO REGISTER_USER_PREPARED;
    private RequestLoginUserDTO LOGIN_USER_PREPARED;
    private RequestFormQuestionDTO REQUEST_FORM_QUESTION_PREPARED;

    static String MESSAGE_SUCCESS_REGISTER;
    static String MESSAGE_CONFLICT_REGISTER;

    static String MESSAGE_SUCCESS_LOGIN;
    static String MESSAGE_ERROR_LOGIN;

    @BeforeEach
    void setUp() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        USER_PREPARED = User.builder()
                .id(1L)
                .fullName("cliente prueba")
                .email("cliente@cliente.com")
                .numberPhone("618429871")
                .role(Role.ROLE_USER)
                .password(passwordEncoder.encode("password"))
                .build();

        USER_PREPARED_DTO = UserDTO.builder()
                .id(1L)
                .fullName("cliente prueba")
                .email("cliente@cliente.com")
                .numberPhone("12345678")
                .role(Role.ROLE_USER.name())
                .bookings(null)
                .build();

        REGISTER_USER_PREPARED = RequestRegisterUserDTO.builder()
                .fullName("cliente prueba")
                .email("cliente@cliente.com")
                .numberPhone("618429871")
                .password(passwordEncoder.encode("password"))
                .build();

        LOGIN_USER_PREPARED = RequestLoginUserDTO.builder()
                .email("cliente@cliente.com")
                .password("password")
                .build();

        REQUEST_FORM_QUESTION_PREPARED = RequestFormQuestionDTO.builder()
                .email("cliente@cliente.com")
                .fullName("Nombre de cliente")
                .numberPhone("12345678")
                .message("Esto es un mensaje de prueba")
                .build();

        MESSAGE_SUCCESS_REGISTER = "Usuario registrado con éxito";
        MESSAGE_CONFLICT_REGISTER = "Error al registrar usuario: " + USER_PREPARED_DTO.getEmail() + " ya existe";

        MESSAGE_SUCCESS_LOGIN = "Usuario logueado con éxito";
        MESSAGE_ERROR_LOGIN = "Error al iniciar sesión: Usuario no encontrado";
    }


    @Nested
    class RegisterUser {
        @Test
        void testRegisterUserSuccess() {
            when(userRepository.existsByEmail(USER_PREPARED_DTO.getEmail())).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(USER_PREPARED);
            when(userMapper.mapUserEntityToUserDto(any(User.class)
            )).thenReturn(USER_PREPARED_DTO);

            userService.register(REGISTER_USER_PREPARED);

            verify(userRepository).save(any(User.class));
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        void testRegisterUserError() {

            when(userRepository.existsByEmail(USER_PREPARED_DTO.getEmail())).thenReturn(true);

            userService.register(REGISTER_USER_PREPARED);

            verify(userRepository, times(0)).save(any(User.class));
        }
    }


    @Nested
    class LoginUser {

        @Test
        void testLoginUserSuccess() {
            when(userRepository.findByEmail(LOGIN_USER_PREPARED.getEmail())).thenReturn(Optional.of(USER_PREPARED));
            when(authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            LOGIN_USER_PREPARED.getEmail(),
                            LOGIN_USER_PREPARED.getPassword())))
                    .thenReturn(null);
            when(jwtUtils.generateToken(any(UserDetails.class))).thenReturn("TOKEN-GENERADO");
            when(userMapper.mapUserEntityToUserDto(any(User.class))).thenReturn(USER_PREPARED_DTO);

            userService.login(LOGIN_USER_PREPARED);

            verify(userRepository, times(1)).findByEmail(anyString());
            verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(jwtUtils, times(1)).generateToken(any(UserDetails.class));
            verify(userMapper, times(1)).mapUserEntityToUserDto(any(User.class));
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
        List<User> listUsers = List.of(USER_PREPARED);
        List<UserDTO> listUsersDto = List.of(USER_PREPARED_DTO);

        when(userRepository.findAll()).thenReturn(listUsers);
        when(userMapper.mapUserListEntityToUserDtoList(listUsers)).thenReturn(listUsersDto);

        userService.getAllUsers();

        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(1)).mapUserListEntityToUserDtoList(listUsers);
    }

    @Test
    void testGetUserBookingHistory() {

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(USER_PREPARED));
        when(userMapper.mapUserEntityToUserDtoWithBookingAndRoom(any(User.class), any(BookingMapper.class))).thenReturn(USER_PREPARED_DTO);

        userService.getUserBookingHistory(String.valueOf(USER_PREPARED.getId()));

        verify(userRepository, times(1)).findById(anyLong());
        verify(userMapper, times(1)).mapUserEntityToUserDtoWithBookingAndRoom(any(User.class), isNull());
    }

    @Nested
    class GetUser {
        @Test
        void testGetUserById_Success() {
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(USER_PREPARED));
            when(userMapper.mapUserEntityToUserDto(any(User.class))).thenReturn(USER_PREPARED_DTO);

            userService.getUserById(String.valueOf(USER_PREPARED.getId()));

            verify(userRepository, times(1)).findById(anyLong());
            verify(userMapper, times(1)).mapUserEntityToUserDto(any(User.class));

        }

        @Test
        void testGetUserById_Error() {
            when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

            userService.getUserById(String.valueOf(USER_PREPARED.getId()));

            verify(userRepository, times(1)).findById(anyLong());
            verify(userMapper, times(0)).mapUserEntityToUserDto(any(User.class));
        }
    }

    @Nested
    class DeleteUser {
        @Test
        void testDeleteUserById_Success() {
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(USER_PREPARED));

            userService.deleteUser(String.valueOf(USER_PREPARED.getId()));

            verify(userRepository, times(1)).findById(anyLong());
            verify(userRepository, times(1)).deleteById(anyLong());
        }

        @Test
        void testDeleteUserById_Error() {
            when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

            userService.deleteUser(String.valueOf(USER_PREPARED.getId()));

            verify(userRepository, times(1)).findById(anyLong());
            verify(userRepository, times(0)).deleteById(anyLong());
        }
    }

    @Test
    void testFormUserQuestion() {
        doNothing().when(emailService).sendEmail(REQUEST_FORM_QUESTION_PREPARED);

        userService.formUserQuestion(REQUEST_FORM_QUESTION_PREPARED);

        verify(emailService, atLeastOnce()).sendEmail(REQUEST_FORM_QUESTION_PREPARED);
    }
}