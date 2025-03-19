package com.hotel_ng.app.service.interfaces;

import com.hotel_ng.app.dto.*;

public interface AdminService {
    ResponseDto register(AdminLoginDto loginUserDto);
    ResponseDto login(AdminLoginDto loginUserDto);
}
