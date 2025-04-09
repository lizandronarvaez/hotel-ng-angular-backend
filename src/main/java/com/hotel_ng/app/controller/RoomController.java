package com.hotel_ng.app.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.hotel_ng.app.dto.ResponseDto;
import com.hotel_ng.app.enums.RoomType;
import com.hotel_ng.app.service.interfaces.RoomService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/rooms")
@RequiredArgsConstructor
@RestController
public class RoomController {

    private final RoomService roomService;

    @Tag(name = "Administradores")
    @PostMapping("/create-room")
    public ResponseEntity<ResponseDto> addNewRoom(
            @RequestParam(value = "roomImage", required = false) MultipartFile roomImage,
            @RequestParam(value = "roomType") RoomType roomType,
            @RequestParam(value = "roomPrice") BigDecimal roomPrice,
            @RequestParam(value = "roomDescription") String roomDescription,
            @RequestParam(value = "roomMaxOfGuest") String roomMaxOfGuest) {

        if (roomType == null || roomType.toString().isBlank()
                || roomPrice == null || roomPrice.toString().isBlank()
                || roomMaxOfGuest == null || roomMaxOfGuest.isBlank()
                || roomDescription == null || roomDescription.isBlank()) {

            ResponseDto response = new ResponseDto();
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Por favor los campos son obligatorios");
            return ResponseEntity.status(response.getStatusCode()).body(response);
        }
        ResponseDto response = roomService.addNewRoom(roomImage, roomType, roomPrice, roomDescription, roomMaxOfGuest);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @Tag(name = "Público")
    @GetMapping("/get-all-rooms")
    public ResponseEntity<ResponseDto> getAllRooms(Pageable pageable) {

        ResponseDto response = roomService.getAllRooms(pageable);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @Tag(name = "Público")
    @GetMapping("/get-room/{roomId}")
    public ResponseEntity<ResponseDto> getRoomById(@PathVariable("roomId") Long roomId) {
        ResponseDto response = roomService.getRoomById(roomId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @Tag(name = "Administradores")
    @PutMapping("/update-room/{roomId}")
    public ResponseEntity<ResponseDto> updateRoom(
            @PathVariable("roomId") Long roomId,
            @RequestParam(value = "roomImage", required = false) MultipartFile roomImage,
            @RequestParam(value = "roomType", required = false) RoomType roomType,
            @RequestParam(value = "roomPrice", required = false) BigDecimal roomPrice,
            @RequestParam(value = "roomDescription", required = false) String roomDescription,
            @RequestParam(value = "roomMaxOfGuest", required = false) String roomMaxOfGuest) {
        ResponseDto response = roomService.updateRoom(roomId, roomImage, roomType, roomPrice, roomDescription,
                roomMaxOfGuest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @Tag(name = "Administradores")
    @DeleteMapping("/delete-room/{roomId}")
    public ResponseEntity<ResponseDto> deleteRoom(@PathVariable("roomId") Long roomId) {

        ResponseDto response = roomService.deleteRoom(roomId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @Tag(name = "Público")
    @GetMapping("/get-types-rooms")
    public List<String> getTypesRooms() {
        return roomService.getAllRoomTypes();
    }

    @Tag(name = "Público")
    @GetMapping("/all-available-rooms")
    public ResponseEntity<ResponseDto> getAvailableRooms() {
        ResponseDto response = roomService.getAvailableRooms();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @Tag(name = "Público")
    //todo:implementar paginado para la búsqueda
    @GetMapping("/available-rooms-by-date-and-type")
    public ResponseEntity<ResponseDto> getAvailableRoomsByDateAndType(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
            @RequestParam String roomType) {

        if (checkInDate == null || checkOutDate == null || roomType == null) {
            ResponseDto response = new ResponseDto();
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Por favor los campos son obligatorios (checkInDate, checkOutDate, roomType)");
            return ResponseEntity.status(response.getStatusCode()).body(response);
        }

        if (checkInDate.isAfter(checkOutDate)) {
            ResponseDto response = new ResponseDto();
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("La fecha de check-in debe ser anterior a la fecha de check-out");
            return ResponseEntity.status(response.getStatusCode()).body(response);
        }

        // convierte el string a valore roomType
        RoomType roomTypeEnum = RoomType.valueOf(roomType.toUpperCase());
        ResponseDto response = roomService.getAvailableRoomsByDateAndType(checkInDate, checkOutDate, roomTypeEnum);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

}
