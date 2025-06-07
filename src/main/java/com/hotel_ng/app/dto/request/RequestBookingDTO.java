package com.hotel_ng.app.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hotel_ng.app.dto.BookingDTO;
import com.hotel_ng.app.dto.UserDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestBookingDTO {

    private BookingDTO booking;
    private UserDTO client;

}
