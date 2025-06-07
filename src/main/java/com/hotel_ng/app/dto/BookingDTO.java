package com.hotel_ng.app.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingDTO {

    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate checkInDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate checkOutDate;

    private String bookingCode;
    private String totalNights;
    private String totalPriceNights;

    private UserDTO userDto;
    private RoomDTO roomDto;
}
