package com.hotel_ng.app.controller;

import com.hotel_ng.app.dto.request.RequestBookingDTO;
import com.hotel_ng.app.service.interfaces.BookingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hotel_ng.app.dto.response.ResponseDTO;
import com.hotel_ng.app.entity.Booking;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/bookings")
@RestController
public class BookingController {

    private final BookingService bookingService;

    //    todo: realizar cambio en este endpoint para realizar reservas de clientes
    @Tag(name = "Usuarios")
    @PostMapping("/new-reservation/book-room/{roomId}")
    public ResponseEntity<ResponseDTO> bookRoom(
            @PathVariable("roomId") Long roomId,
            @RequestBody RequestBookingDTO bookingRequest) {

        ResponseDTO response = bookingService.saveBooking(roomId, bookingRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @Tag(name = "Administradores")
    @GetMapping("/all")
    public ResponseEntity<ResponseDTO> getAllBookings() {
        ResponseDTO response = bookingService.getAllBookings();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // Buscar las reservas
    @Tag(name = "Público")
    @GetMapping("/get-by-booking-code/{bookingConfirmationCode}")
    public ResponseEntity<ResponseDTO> getBookingsByConfirmationCode(
            @PathVariable("bookingConfirmationCode") String bookingConfirmationCode) {
        ResponseDTO response = bookingService.getBookingByConfirmationCode(bookingConfirmationCode);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // CANCELAR una reserva
    @Tag(name = "Administradores")
    @Tag(name = "Usuarios")
    @DeleteMapping("/cancel-booking/{bookingId}")
    public ResponseEntity<ResponseDTO> cancelBooking(
            @PathVariable("bookingId") Long bookingId) {
        ResponseDTO response = bookingService.cancelBooking(bookingId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

}
