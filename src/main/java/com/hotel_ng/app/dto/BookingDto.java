package com.hotel_ng.app.dto;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingDto {
    private Long id;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    // private Integer maxOfGuest;
    private String bookingCode;

    private UserDto userDto;
    private RoomDto roomDto;
}
