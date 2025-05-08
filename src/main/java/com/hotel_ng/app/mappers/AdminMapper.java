package com.hotel_ng.app.mappers;

import com.hotel_ng.app.dto.AdminDTO;
import com.hotel_ng.app.entity.Admin;
import org.springframework.stereotype.Component;

@Component
public class AdminMapper {
    public AdminDTO mapAdminEntityToAdminDto(Admin admin) {
        return AdminDTO.builder()
                .id(admin.getId())
                .email(admin.getEmail())
                .role(admin.getRole())
                .build();
    }

}
