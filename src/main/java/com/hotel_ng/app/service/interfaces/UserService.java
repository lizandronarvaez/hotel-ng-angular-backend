package com.hotel_ng.app.service.interfaces;

import com.hotel_ng.app.dto.*;

public interface UserService {
    ResponseDto register(RegisterUserDto loginUserDto);

    ResponseDto login(LoginUserDto loginUserDto);

    ResponseDto getAllUsers();

    ResponseDto getUserBookingHistory(String userId);

    ResponseDto getUserById(String userId);

    ResponseDto getUserProfile(String email);

    ResponseDto deleteUser(String userId);
}
