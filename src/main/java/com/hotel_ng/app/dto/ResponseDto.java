package com.hotel_ng.app.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDto {

    private int statusCode;
    private String message;

    private String token;
    private String role;
    private String confirmationCode;

    private AdminDto admin;
    private UserDto user;
    private RoomDto room;
    private BookingDto booking;

    private List<UserDto> userList;
    private List<RoomDto> roomList;
    private List<BookingDto> bookingList;
    private List<String> errors;

    // Campos para paginación
    private Integer pageNumber;
    private Integer pageSize;
    private Long totalElements;
    private Integer totalPages;
}
