package com.hotel_ng.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminDTO {
    private Long id;
    private String email;
    private String role;
}
