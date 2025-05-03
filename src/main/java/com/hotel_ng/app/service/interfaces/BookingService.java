package com.hotel_ng.app.service.interfaces;

import com.hotel_ng.app.dto.response.ResponseDTO;
import com.hotel_ng.app.entity.Booking;

public interface BookingService {

    ResponseDTO cancelBooking(Long bookingId);

    ResponseDTO getAllBookings();

    ResponseDTO getBookingByConfirmationCode(String confirmationCode);

    ResponseDTO saveBooking(Long roomId, Long userId, Booking bookingRequest);
}
