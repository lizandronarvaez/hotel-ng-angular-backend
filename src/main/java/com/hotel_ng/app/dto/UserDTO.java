package com.hotel_ng.app.dto;

import java.util.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {

    private Long id;
    private String email;
    private String fullName;
    private String numberPhone;
    private String role;
    private String message;

    @Builder.Default
    private List<BookingDTO> bookings = new ArrayList<>();
}
