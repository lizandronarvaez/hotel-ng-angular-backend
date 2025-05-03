package com.hotel_ng.app.service.interfaces;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.hotel_ng.app.dto.response.ResponseDTO;
import com.hotel_ng.app.enums.RoomType;

public interface RoomService {
    ResponseDTO addNewRoom(MultipartFile roomImageUrl, RoomType roomType, BigDecimal roomPrice, String description,
                           String roomMaxOfGuest);

    List<String> getAllRoomTypes();

    ResponseDTO getAllRooms(Pageable pageable);

    ResponseDTO deleteRoom(Long roomId);

    ResponseDTO updateRoom(Long roomId, MultipartFile roomImageUrl, RoomType roomType, BigDecimal roomPrice,
                           String description,
                           String roomMaxOfGuest);

    ResponseDTO getRoomById(Long roomId);

    ResponseDTO getAvailableRoomsByDateAndType(LocalDate checkInDate, LocalDate checkOutDate, RoomType roomType);

    ResponseDTO getAvailableRooms();
}
