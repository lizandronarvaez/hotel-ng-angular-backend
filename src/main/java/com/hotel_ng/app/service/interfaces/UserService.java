package com.hotel_ng.app.service.interfaces;

import com.hotel_ng.app.dto.request.RequestFormQuestionDTO;
import com.hotel_ng.app.dto.request.RequestLoginUserDTO;
import com.hotel_ng.app.dto.request.RequestRegisterUserDTO;
import com.hotel_ng.app.dto.response.ResponseDTO;

public interface UserService {
    ResponseDTO register(RequestRegisterUserDTO loginUserDto);

    ResponseDTO login(RequestLoginUserDTO requestLoginUserDTO);

    ResponseDTO getAllUsers();

    ResponseDTO getUserBookingHistory(String userId);

    ResponseDTO getUserById(String userId);

    ResponseDTO getUserProfile(String email);

    ResponseDTO deleteUser(String userId);

    ResponseDTO formUserQuestion(RequestFormQuestionDTO requestFormQuestionDTO);
}
