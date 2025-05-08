package com.hotel_ng.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hotel_ng.app.enums.Role;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminDTO {
    private Long id;
    private String email;
    private Role role;
}
