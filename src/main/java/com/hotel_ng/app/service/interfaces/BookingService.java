package com.hotel_ng.app.service.interfaces;

import com.hotel_ng.app.dto.ResponseDto;
import com.hotel_ng.app.entity.Booking;

public interface BookingService {

    ResponseDto cancelBooking(Long bookingId);

    ResponseDto getAllBookings();

    ResponseDto getBookingByConfirmationCode(String confirmationCode);

    ResponseDto saveBooking(Long roomId, Long userId, Booking bookingRequest);
}
