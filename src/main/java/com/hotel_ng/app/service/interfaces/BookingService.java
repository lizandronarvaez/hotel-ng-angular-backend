package com.hotel_ng.app.service.interfaces;

import com.hotel_ng.app.dto.request.RequestBookingDTO;
import com.hotel_ng.app.dto.response.ResponseDTO;

public interface BookingService {

    ResponseDTO cancelBooking(Long bookingId);

    ResponseDTO getAllBookings();

    ResponseDTO getBookingByConfirmationCode(String confirmationCode);

    ResponseDTO saveBooking(Long roomId, RequestBookingDTO bookingRequest);
}
