package com.hotel_ng.app.service.impl;

import java.time.LocalDate;
import java.util.List;

import com.hotel_ng.app.dto.request.RequestBookingDTO;
import com.hotel_ng.app.dto.response.ResponseDTO;
import com.hotel_ng.app.enums.Role;
import com.hotel_ng.app.mappers.*;


import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    @Override
    public ResponseDTO saveBooking(Long roomId, RequestBookingDTO bookingRequest) {
        ResponseDTO responseDto = new ResponseDTO();
        try {
            LocalDate checkInDate = bookingRequest.getBooking().getCheckInDate();
            LocalDate checkOutDate = bookingRequest.getBooking().getCheckOutDate();

            if (checkOutDate.isBefore(checkInDate)) {
                throw new OurException("Debes realizar el check-in antes del check-out.");
            }

            if (checkInDate.isBefore(LocalDate.now())) {
                throw new OurException("La fecha de check-in no puede ser anterior a hoy.");
            }

            Room room = roomRepository.findById(roomId).orElseThrow(() -> new OurException("Habitación no encontrada"));
            User user = createUserIfNotExist(bookingRequest);

            List<Booking> existingBookings = room.getBookings();
            if (!roomIsAvailable(bookingRequest, existingBookings)) {
                throw new OurException("Habitación no disponible en las fechas programadas");
            }

            Booking booking = new Booking();
            booking.setCheckInDate(bookingRequest.getBooking().getCheckInDate());
            booking.setCheckOutDate(bookingRequest.getBooking().getCheckOutDate());
            booking.setTotalNights(bookingRequest.getBooking().getTotalNights());
            booking.setTotalPriceNights(bookingRequest.getBooking().getTotalPriceNights());
            booking.setRoom(room);
            booking.setUser(user);
            booking.setConfirmationCode(Utils.generateCodeBooking(10));

            bookingRepository.save(booking);

            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setMessage("Operación exitosa");
            responseDto.setConfirmationCode(booking.getConfirmationCode());
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
    public ResponseDTO getBookingByConfirmationCode(String confirmationCode) {
        ResponseDTO responseDto = new ResponseDTO();
        try {
            Booking booking = bookingRepository.findByConfirmationCode(confirmationCode)
                    .orElseThrow(() -> new OurException("Número de reserva no válido"));

            BookingDTO bookingDto = bookingMapper.mapBookingEntityToBookingDtoWithRoom(booking, true);

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
    public ResponseDTO getAllBookings() {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            List<Booking> bookings = bookingRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
            List<BookingDTO> listBookingsDTO = bookingMapper.mapBookingListEntityToBookingDtoList(bookings);

            responseDTO.setStatusCode(HttpStatus.OK.value());
            responseDTO.setMessage("Operación exitosa");
            responseDTO.setBookingList(listBookingsDTO);

        } catch (Exception e) {
            responseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDTO.setMessage("Hubo un error al realizar la operación: " + e.getMessage());
        }
        return responseDTO;
    }

    @Override
    public ResponseDTO cancelBooking(Long bookingId) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            bookingRepository.findById(bookingId).orElseThrow(() -> new OurException("No se encontró la reserva"));
            bookingRepository.deleteById(bookingId);

            responseDTO.setStatusCode(HttpStatus.OK.value());
            responseDTO.setMessage("Operación exitosa");
        } catch (OurException e) {
            responseDTO.setStatusCode(HttpStatus.NOT_FOUND.value());
            responseDTO.setMessage(e.getMessage());

        } catch (Exception e) {
            responseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDTO.setMessage("Hubo un error al realizar la operación: " + e.getMessage());
        }
        return responseDTO;
    }

    private boolean roomIsAvailable(RequestBookingDTO bookingRequest, List<Booking> existingBookings) {
        if (bookingRequest == null || bookingRequest.getBooking() == null ||
                bookingRequest.getBooking().getCheckInDate() == null ||
                bookingRequest.getBooking().getCheckOutDate() == null) {
            return false;
        }
        LocalDate newCheckIn = bookingRequest.getBooking().getCheckInDate();
        LocalDate newCheckOut = bookingRequest.getBooking().getCheckOutDate();

        if (newCheckIn.isAfter(newCheckOut)) {
            return false;
        }

        return existingBookings.stream()
                .filter(existingBooking -> existingBooking != null &&
                        existingBooking.getCheckInDate() != null &&
                        existingBooking.getCheckOutDate() != null)
                .noneMatch(existingBooking -> {
                    LocalDate existingCheckIn = existingBooking.getCheckInDate();
                    LocalDate existingCheckOut = existingBooking.getCheckOutDate();

                    return (newCheckIn.isBefore(existingCheckOut) &&
                            newCheckOut.isAfter(existingCheckIn));
                });
    }


    private User createUserIfNotExist(RequestBookingDTO requestBooking) {
        return userRepository.findByEmail(requestBooking.getClient().getEmail()).orElseGet(() -> {
            User newUser = User.builder()
                    .fullname(requestBooking.getClient().getFullname())
                    .email(requestBooking.getClient().getEmail())
                    .password(passwordEncoder.encode(requestBooking.getClient().getNumberPhone()))
                    .numberPhone(requestBooking.getClient().getNumberPhone())
                    .role(Role.ROLE_USER)
                    .build();
            return userRepository.save(newUser);
        });
    }

}
