package com.hotel_ng.app.controller;

import com.hotel_ng.app.service.interfaces.BookingService;
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

    // crear una nueva reserva
    @PostMapping("/new-reservation/book-room/{roomId}/{userId}")
    public ResponseEntity<ResponseDto> bookRoom(
            @PathVariable("roomId") Long roomId,
            @PathVariable("userId") Long userId,
            @RequestBody Booking bookingRequest) {

        ResponseDto response = bookingService.saveBooking(roomId, userId, bookingRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // listar todas las reservas
    @GetMapping("/all")
    public ResponseEntity<ResponseDto> getAllBookings() {
        ResponseDto response = bookingService.getAllBookings();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // Buscar las reservas
    @GetMapping("/get-by-booking-code/{bookingConfirmationCode}")
    public ResponseEntity<ResponseDto> getBookingsByConfirmationCode(
            @PathVariable("bookingConfirmationCode") String bookingConfirmationCode) {
        ResponseDto response = bookingService.getBookingByConfirmationCode(bookingConfirmationCode);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // CANCELAR una reserva
    @DeleteMapping("/cancel-booking/{bookingId}")
    public ResponseEntity<ResponseDto> cancelBooking(
            @PathVariable("bookingId") Long bookingId) {
        ResponseDto response = bookingService.cancelBooking(bookingId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

}
