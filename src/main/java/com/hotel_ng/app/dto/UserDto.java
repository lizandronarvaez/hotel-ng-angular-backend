package com.hotel_ng.app.dto;

import java.util.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    private Long id;
    private String email;
    private String fullName;
    private String numberPhone;
    private String role;
    private String message;

    @Builder.Default
    private List<BookingDto> bookings = new ArrayList<>();
}
