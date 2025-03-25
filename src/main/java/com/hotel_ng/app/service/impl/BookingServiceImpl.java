package com.hotel_ng.app.service.impl;

import java.time.LocalDate;
import java.util.List;

import com.hotel_ng.app.mappers.BookingMapper;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.hotel_ng.app.dto.*;
import com.hotel_ng.app.entity.*;
import com.hotel_ng.app.exception.OurException;
import com.hotel_ng.app.repository.*;
import com.hotel_ng.app.service.interfaces.BookingService;
import com.hotel_ng.app.utils.Utils;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    @Override
    public ResponseDto saveBooking(Long roomId, Long userId, Booking bookingRequest) {
        ResponseDto responseDto = new ResponseDto();

        try {
            if (bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())) {
                throw new OurException("Debes realizar el check in antes del checkout");
            }
            if (bookingRequest.getCheckInDate().isBefore(LocalDate.now())) {
                throw new OurException("La fecha de check-in no puede ser anterior a hoy");
            }

            Room room = roomRepository.findById(roomId).orElseThrow(() -> new OurException("Habitación no encontrada"));
            Client user = userRepository.findById(userId).orElseThrow(() -> new OurException("Usuario no encontrado"));

            List<Booking> existingBookings = room.getBookings();
            if (!roomIsAvailable(bookingRequest, existingBookings)) {
                throw new OurException("Habitación no disponible");
            }

            bookingRequest.setRoom(room);
            bookingRequest.setUser(user);

            String bookingConfirmationCode = Utils.generateCodeBooking(10);
            bookingRequest.setConfirmationCode(bookingConfirmationCode);
            bookingRepository.save(bookingRequest);

            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setMessage("Operación exitosa");
            responseDto.setConfirmationCode(bookingConfirmationCode);

        } catch (OurException e) {
            if (e.getMessage().contains("no encontrada") || e.getMessage().contains("no encontrado")) {
                responseDto.setStatusCode(HttpStatus.NOT_FOUND.value());
            } else if (e.getMessage().contains("no disponible")) {
                responseDto.setStatusCode(HttpStatus.CONFLICT.value());
            } else {
                responseDto.setStatusCode(HttpStatus.BAD_REQUEST.value());
            }
            responseDto.setMessage(e.getMessage());

        } catch (Exception e) {
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setMessage("Hubo un error al realizar la operación: " + e.getMessage());
        }
        return responseDto;
    }

    @Override
    public ResponseDto getBookingByConfirmationCode(String confirmationCode) {
        ResponseDto responseDto = new ResponseDto();
        try {
            Booking booking = bookingRepository.findByConfirmationCode(confirmationCode)
                    .orElseThrow(() -> new OurException("Número de reserva no válido"));

            BookingDto bookingDto = bookingMapper.mapBookingEntityToBookingDtoWithRoom(booking, true);

            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setMessage("Operación exitosa");
            responseDto.setBooking(bookingDto);
        } catch (OurException e) {
            responseDto.setStatusCode(HttpStatus.NOT_FOUND.value());
            responseDto.setMessage(e.getMessage());
        } catch (Exception e) {
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setMessage("Hubo un error al realizar la operación: " + e.getMessage());
        }
        return responseDto;
    }

    @Override
    public ResponseDto getAllBookings() {
        ResponseDto responseDto = new ResponseDto();
        try {
            List<Booking> bookings = bookingRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
            List<BookingDto> listBookingDtos = bookingMapper.mapBookingListEntityToBookingDtoList(bookings);

            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setMessage("Operación exitosa");
            responseDto.setBookingList(listBookingDtos);

        } catch (Exception e) {
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setMessage("Hubo un error al realizar la operación: " + e.getMessage());
        }
        return responseDto;
    }

    @Override
    public ResponseDto cancelBooking(Long bookingId) {
        ResponseDto responseDto = new ResponseDto();
        try {
            bookingRepository.findById(bookingId).orElseThrow(() -> new OurException("No se encontró la reserva"));
            bookingRepository.deleteById(bookingId);

            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setMessage("Operación exitosa");
        } catch (OurException e) {
            responseDto.setStatusCode(HttpStatus.NOT_FOUND.value());
            responseDto.setMessage(e.getMessage());

        } catch (Exception e) {
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setMessage("Hubo un error al realizar la operación: " + e.getMessage());
        }
        return responseDto;
    }

    private boolean roomIsAvailable(Booking bookingRequest, List<Booking> existingBookings) {
        return existingBookings.stream()

                .noneMatch(existingBooking -> bookingRequest.getCheckInDate().equals(existingBooking.getCheckInDate())
                        || bookingRequest.getCheckOutDate().isBefore(existingBooking.getCheckOutDate())
                        || (bookingRequest.getCheckInDate().isAfter(existingBooking.getCheckInDate())
                                && bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate()))
                        || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckOutDate()))
                        || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckOutDate()))

                        || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckInDate()))

                        || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(bookingRequest.getCheckInDate())));
    }
}
