package com.hotel_ng.app.dto;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingDTO {

    private Long id;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    // private Integer maxOfGuest;
    private String bookingCode;

    private UserDTO userDto;
    private RoomDTO roomDto;
}
