package com.hotel_ng.app.dto;

import java.math.BigDecimal;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hotel_ng.app.entity.ServiceRooms;
import com.hotel_ng.app.enums.RoomType;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RoomDTO {

    private Long id;

    private RoomType roomType;
    private BigDecimal roomPrice;
    private Integer roomMaxOfGuest;
    private String roomImageUrl;
    private String roomDescription;

    private List<ServiceRooms> serviceRooms;
    private List<BookingDTO> bookings;
}
