package com.hotel_ng.app.service.interfaces;

import com.hotel_ng.app.dto.UserDto;

public interface EmailService {
    void sendEmail(UserDto userDto);
}
