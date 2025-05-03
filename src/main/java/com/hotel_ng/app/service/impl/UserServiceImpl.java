package com.hotel_ng.app.service.impl;

import java.util.List;

import com.hotel_ng.app.dto.request.RequestFormQuestionDTO;
import com.hotel_ng.app.dto.request.RequestLoginUserDTO;
import com.hotel_ng.app.dto.request.RequestRegisterUserDTO;
import com.hotel_ng.app.dto.response.ResponseDTO;
import com.hotel_ng.app.mappers.BookingMapper;
import com.hotel_ng.app.mappers.UserMapper;
import com.hotel_ng.app.service.interfaces.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hotel_ng.app.dto.*;
import com.hotel_ng.app.entity.User;
import com.hotel_ng.app.enums.Role;
import com.hotel_ng.app.exception.OurException;
import com.hotel_ng.app.repository.UserRepository;
import com.hotel_ng.app.security.utils.JwtUtils;
import com.hotel_ng.app.service.interfaces.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final BookingMapper bookingMapper;
    private final EmailService emailService;

    @Override
    public ResponseDTO register(RequestRegisterUserDTO requestRegisterUserDTO) {
        ResponseDTO responseDTO = new ResponseDTO();

        try {
            if (userRepository.existsByEmail(requestRegisterUserDTO.getEmail())) {
                throw new OurException(requestRegisterUserDTO.getEmail() + " ya existe");
            }

            User user = User.builder()
                    .email(requestRegisterUserDTO.getEmail())
                    .fullName(requestRegisterUserDTO.getFullName())
                    .numberPhone(requestRegisterUserDTO.getNumberPhone())
                    .password(passwordEncoder.encode(requestRegisterUserDTO.getPassword()))
                    .role(Role.ROLE_USER)
                    .build();

            User userSaved = userRepository.save(user);
            UserDTO userDTO = this.userMapper.mapUserEntityToUserDto(userSaved);

            responseDTO.setStatusCode(HttpStatus.OK.value());
            responseDTO.setMessage("Usuario registrado con éxito");
            responseDTO.setUser(userDTO);

        } catch (OurException e) {
            responseDTO.setStatusCode(HttpStatus.CONFLICT.value());
            responseDTO.setMessage("Error al registrar usuario: " + e.getMessage());

        } catch (Exception e) {
            responseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDTO.setMessage("Error al registrar usuario: " + e.getMessage());
        }
        return responseDTO;
    }

    @Override
    public ResponseDTO login(RequestLoginUserDTO requestLoginUserDTO) {
        ResponseDTO responseDTO = new ResponseDTO();

        try {
            var user = userRepository.findByEmail(requestLoginUserDTO.getEmail())
                    .orElseThrow(() -> new OurException("Usuario no encontrado"));

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestLoginUserDTO.getEmail(), requestLoginUserDTO.getPassword()));

            var token = jwtUtils.generateToken(user);

            responseDTO.setStatusCode(HttpStatus.OK.value());
            responseDTO.setMessage("Usuario logueado con éxito");
            responseDTO.setUser(this.userMapper.mapUserEntityToUserDto(user));
            responseDTO.setToken(token);
            responseDTO.setRole(user.getRole().name());

        } catch (OurException e) {
            responseDTO.setStatusCode(HttpStatus.NOT_FOUND.value());
            responseDTO.setMessage("Error al iniciar sesión: " + e.getMessage());

        } catch (Exception e) {
            responseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDTO.setMessage("Error realizar el login: " + e.getMessage());
        }
        return responseDTO;
    }

    @Override
    public ResponseDTO getAllUsers() {
        ResponseDTO responseDTO = new ResponseDTO();

        try {
            List<User> users = userRepository.findAll();
            List<UserDTO> usersDTO = this.userMapper.mapUserListEntityToUserDtoList(users);

            responseDTO.setStatusCode(HttpStatus.OK.value());
            responseDTO.setMessage("Lista de usuarios");
            responseDTO.setUserList(usersDTO);

        } catch (Exception e) {
            responseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDTO.setMessage("Hubo un error en la petición: " + e.getMessage());
        }

        return responseDTO;
    }

    @Override
    public ResponseDTO getUserBookingHistory(String userId) {
        ResponseDTO responseDTO = new ResponseDTO();

        try {
            User user = userRepository.findById(Long.valueOf(userId))
                    .orElseThrow(() -> new OurException("Usuario no encontrado"));
            UserDTO userDTO = this.userMapper.mapUserEntityToUserDtoWithBookingAndRoom(user, bookingMapper);

            responseDTO.setMessage("¡Operación exitosa!");
            responseDTO.setStatusCode(HttpStatus.OK.value());
            responseDTO.setUser(userDTO);

        } catch (OurException e) {
            responseDTO.setStatusCode(HttpStatus.NOT_FOUND.value());
            responseDTO.setMessage(e.getMessage());

        } catch (Exception e) {
            responseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDTO.setMessage("Hubo un error en la petición: " + e.getMessage());
        }
        return responseDTO;
    }

    @Override
    public ResponseDTO getUserById(String userId) {
        ResponseDTO responseDTO = new ResponseDTO();

        try {
            User user = userRepository.findById(Long.valueOf(userId))
                    .orElseThrow(() -> new OurException("Usuario no encontrado"));
            UserDTO userDto = this.userMapper.mapUserEntityToUserDto(user);

            responseDTO.setMessage("Usuario encontrado");
            responseDTO.setStatusCode(HttpStatus.OK.value());
            responseDTO.setUser(userDto);

        } catch (OurException e) {
            responseDTO.setStatusCode(HttpStatus.NOT_FOUND.value());
            responseDTO.setMessage(e.getMessage());

        } catch (Exception e) {
            responseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDTO.setMessage("Hubo un error al realizar la operación: " + e.getMessage());
        }
        return responseDTO;
    }

    @Override
    public ResponseDTO getUserProfile(String email) {
        ResponseDTO responseDTO = new ResponseDTO();

        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new OurException("Usuario no encontrado"));
            UserDTO userDto = this.userMapper.mapUserEntityToUserDto(user);

            responseDTO.setMessage("Usuario encontrado");
            responseDTO.setStatusCode(HttpStatus.OK.value());
            responseDTO.setUser(userDto);

        } catch (OurException e) {
            responseDTO.setStatusCode(HttpStatus.NOT_FOUND.value());
            responseDTO.setMessage(e.getMessage());

        } catch (Exception e) {
            responseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDTO.setMessage("Hubo un error al realizar la operación: " + e.getMessage());
        }
        return responseDTO;
    }

    @Override
    public ResponseDTO deleteUser(String userId) {
        ResponseDTO responseDTO = new ResponseDTO();

        try {
            userRepository.findById(Long.valueOf(userId))
                    .orElseThrow(() -> new OurException("Usuario no encontrado"));
            userRepository.deleteById(Long.valueOf(userId));

            responseDTO.setMessage("Operación exitosa");
            responseDTO.setStatusCode(HttpStatus.OK.value());

        } catch (OurException e) {
            responseDTO.setStatusCode(HttpStatus.NOT_FOUND.value());
            responseDTO.setMessage(e.getMessage());

        } catch (Exception e) {
            responseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDTO.setMessage(e.getMessage());
        }
        return responseDTO;
    }

    @Override
    public ResponseDTO formUserQuestion(RequestFormQuestionDTO formQuestionDTO) {
        emailService.sendEmail(formQuestionDTO);

        return ResponseDTO
                .builder()
                .message("¡Tu consulta ha sido recibida! Te contactaremos pronto.")
                .statusCode(HttpStatus.OK.value())
                .build();
    }

}
