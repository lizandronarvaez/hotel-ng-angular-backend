package com.hotel_ng.app.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RoomDto {
    private Long id;
    private String roomType;
    private BigDecimal roomPrice;
    private Integer roomMaxOfGuest;
    private String roomImageUrl;
    private String roomDescription;

    private List<ServiceRoomsDto> serviceRooms;
    private List<BookingDto> bookings;

}
