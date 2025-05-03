package com.hotel_ng.app.service.interfaces;

import com.hotel_ng.app.dto.request.RequestAdminDTO;
import com.hotel_ng.app.dto.response.ResponseDTO;

public interface AdminService {
    ResponseDTO register(RequestAdminDTO requestAdminDTO);
    ResponseDTO login(RequestAdminDTO requestAdminDTO);
}
