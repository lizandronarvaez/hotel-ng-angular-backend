package com.hotel_ng.app.service.impl;

import java.util.List;

import com.hotel_ng.app.mappers.BookingMapper;
import com.hotel_ng.app.mappers.UserMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hotel_ng.app.dto.*;
import com.hotel_ng.app.entity.Client;
import com.hotel_ng.app.enums.UserRole;
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

    @Override
    public ResponseDto register(RegisterUserDto loginUserDto) {
        ResponseDto responseDto = new ResponseDto();

        try {
            if (userRepository.existsByEmail(loginUserDto.getEmail())) {
                throw new OurException(loginUserDto.getEmail() + " ya existe");
            }

            Client user = Client.builder()
                    .email(loginUserDto.getEmail())
                    .fullName(loginUserDto.getFullName())
                    .numberPhone(loginUserDto.getNumberPhone())
                    .password(passwordEncoder.encode(loginUserDto.getPassword()))
                    .role(UserRole.ROLE_USER)
                    .build();
            Client userSaved = userRepository.save(user);
            UserDto userDto = this.userMapper.mapUserEntityToUserDto(userSaved);

            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setMessage("Usuario registrado con éxito");
            responseDto.setUser(userDto);

        } catch (OurException e) {
            responseDto.setStatusCode(HttpStatus.CONFLICT.value());
            responseDto.setMessage("Error al registrar usuario: " + e.getMessage());

        } catch (Exception e) {
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setMessage("Error al registrar usuario: " + e.getMessage());
        }
        return responseDto;
    }

    @Override
    public ResponseDto login(LoginUserDto loginUserDto) {
        ResponseDto responseDto = new ResponseDto();

        try {
            var user = userRepository.findByEmail(loginUserDto.getEmail())
                    .orElseThrow(() -> new OurException("Usuario no encontrado"));

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginUserDto.getEmail(), loginUserDto.getPassword()));


            var token = jwtUtils.generateToken(user);

            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setMessage("Usuario logueado con éxito");
            responseDto.setUser(this.userMapper.mapUserEntityToUserDto(user));
            responseDto.setToken(token);
            responseDto.setRole(user.getRole().name());

        } catch (OurException e) {
            responseDto.setStatusCode(HttpStatus.NOT_FOUND.value());
            responseDto.setMessage("Error al iniciar sesión: " + e.getMessage());

        } catch (Exception e) {
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setMessage("Error realizar el login: " + e.getMessage());
        }
        return responseDto;
    }

    @Override
    public ResponseDto getAllUsers() {
        ResponseDto responseDto = new ResponseDto();

        try {
            List<Client> users = userRepository.findAll();
            List<UserDto> userDtos = this.userMapper.mapUserListEntityToUserDtoList(users);

            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setMessage("Lista de usuarios");
            responseDto.setUserList(userDtos);

        } catch (Exception e) {
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setMessage("Hubo un error en la petición: " + e.getMessage());
        }

        return responseDto;
    }

    @Override
    public ResponseDto getUserBookingHistory(String userId) {
        ResponseDto responseDto = new ResponseDto();

        try {
            Client user = userRepository.findById(Long.valueOf(userId))
                    .orElseThrow(() -> new OurException("Usuario no encontrado"));
            UserDto userDto = this.userMapper.mapUserEntityToUserDtoWithBookingAndRoom(user,bookingMapper);

            responseDto.setMessage("¡Operación exitosa!");
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setUser(userDto);

        } catch (OurException e) {
            responseDto.setStatusCode(HttpStatus.NOT_FOUND.value());
            responseDto.setMessage(e.getMessage());

        } catch (Exception e) {
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setMessage("Hubo un error en la petición: " + e.getMessage());
        }
        return responseDto;
    }

    @Override
    public ResponseDto getUserById(String userId) {
        ResponseDto responseDto = new ResponseDto();

        try {
            Client user = userRepository.findById(Long.valueOf(userId))
                    .orElseThrow(() -> new OurException("Usuario no encontrado"));
            UserDto userDto = this.userMapper.mapUserEntityToUserDto(user);

            responseDto.setMessage("Usuario encontrado");
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setUser(userDto);

        } catch (OurException e) {
            responseDto.setStatusCode(HttpStatus.NOT_FOUND.value());
            responseDto.setMessage(e.getMessage());

        } catch (Exception e) {
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setMessage("Hubo un error al realizar la operación: " + e.getMessage());
        }
        return responseDto;
    }

    @Override
    public ResponseDto getUserProfile(String email) {
        ResponseDto responseDto = new ResponseDto();

        try {
            Client user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new OurException("Usuario no encontrado"));
            UserDto userDto = this.userMapper.mapUserEntityToUserDto(user);

            responseDto.setMessage("Usuario encontrado");
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setUser(userDto);
            
        } catch (OurException e) {
            responseDto.setStatusCode(HttpStatus.NOT_FOUND.value());
            responseDto.setMessage(e.getMessage());

        } catch (Exception e) {
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setMessage("Hubo un error al realizar la operación: " + e.getMessage());
        }
        return responseDto;
    }

    @Override
    public ResponseDto deleteUser(String userId) {
        ResponseDto responseDto = new ResponseDto();

        try {
            userRepository.findById(Long.valueOf(userId))
                    .orElseThrow(() -> new OurException("Usuario no encontrado"));
            userRepository.deleteById(Long.valueOf(userId));

            responseDto.setMessage("Operación exitosa");
            responseDto.setStatusCode(HttpStatus.OK.value());

        } catch (OurException e) {
            responseDto.setStatusCode(HttpStatus.NOT_FOUND.value());
            responseDto.setMessage(e.getMessage());

        } catch (Exception e) {
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setMessage(e.getMessage());
        }
        return responseDto;
    }

}
