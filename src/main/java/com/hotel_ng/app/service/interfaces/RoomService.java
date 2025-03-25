package com.hotel_ng.app.service.interfaces;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.hotel_ng.app.dto.ResponseDto;
import com.hotel_ng.app.enums.RoomType;

public interface RoomService {
    ResponseDto addNewRoom(MultipartFile roomImageUrl, RoomType roomType, BigDecimal roomPrice, String description,
            String roomMaxOfGuest);

    List<String> getAllRoomTypes();

    ResponseDto getAllRooms(Pageable pageable);

    ResponseDto deleteRoom(Long roomId);

    ResponseDto updateRoom(Long roomId, MultipartFile roomImageUrl, RoomType roomType, BigDecimal roomPrice,
            String description,
            String roomMaxOfGuest);

    ResponseDto getRoomById(Long roomId);

    ResponseDto getAvailableRoomsByDateAndType(LocalDate checkInDate, LocalDate checkOutDate, RoomType roomType);

    ResponseDto getAvailableRooms();
}
