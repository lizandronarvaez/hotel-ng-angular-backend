package com.hotel_ng.app.mappers;

import com.hotel_ng.app.dto.AdminDto;
import com.hotel_ng.app.entity.Admin;
import org.springframework.stereotype.Component;

@Component
public class AdminMapper {
    public AdminDto mapAdminEntityToAdminDto(Admin admin) {
        return AdminDto.builder()
                .id(admin.getId())
                .email(admin.getEmail())
                .role(admin.getRole().name())
                .build();
    }

}
