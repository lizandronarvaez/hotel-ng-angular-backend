package com.hotel_ng.app.dto.response;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hotel_ng.app.dto.AdminDTO;
import com.hotel_ng.app.dto.BookingDTO;
import com.hotel_ng.app.dto.RoomDTO;
import com.hotel_ng.app.dto.UserDTO;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDTO {

    private int statusCode;
    private String message;

    private AdminDTO admin;
    private UserDTO user;
    private String token;
    private String role;

    private RoomDTO room;
    private BookingDTO booking;
    private String confirmationCode;
    private String totalNights;
    private String totalPriceNights;

    private List<UserDTO> userList;
    private List<RoomDTO> roomList;
    private List<BookingDTO> bookingList;
    private List<String> errors;

    // Campos para paginación
    private Integer pageNumber;
    private Integer pageSize;
    private Long totalElements;
    private Integer totalPages;
}
