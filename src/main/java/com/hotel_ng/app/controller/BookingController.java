package com.hotel_ng.app.controller;

import com.hotel_ng.app.service.interfaces.BookingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hotel_ng.app.dto.ResponseDto;
import com.hotel_ng.app.entity.Booking;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/bookings")
@RestController
public class BookingController {

    private final BookingService bookingService;

    @Tag(name = "Usuarios")
    @PostMapping("/new-reservation/book-room/{roomId}/{userId}")
    public ResponseEntity<ResponseDto> bookRoom(
            @PathVariable("roomId") Long roomId,
            @PathVariable("userId") Long userId,
            @RequestBody Booking bookingRequest) {

        ResponseDto response = bookingService.saveBooking(roomId, userId, bookingRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @Tag(name = "Administradores")
    @GetMapping("/all")
    public ResponseEntity<ResponseDto> getAllBookings() {
        ResponseDto response = bookingService.getAllBookings();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // Buscar las reservas
    @Tag(name = "Público")
    @GetMapping("/get-by-booking-code/{bookingConfirmationCode}")
    public ResponseEntity<ResponseDto> getBookingsByConfirmationCode(
            @PathVariable("bookingConfirmationCode") String bookingConfirmationCode) {
        ResponseDto response = bookingService.getBookingByConfirmationCode(bookingConfirmationCode);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // CANCELAR una reserva
    @Tag(name = "Administradores")
    @Tag(name = "Usuarios")
    @DeleteMapping("/cancel-booking/{bookingId}")
    public ResponseEntity<ResponseDto> cancelBooking(
            @PathVariable("bookingId") Long bookingId) {
        ResponseDto response = bookingService.cancelBooking(bookingId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

}
