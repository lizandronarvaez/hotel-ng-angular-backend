package com.hotel_ng.app.service.impl;

import java.time.LocalDate;
import java.util.List;

import com.hotel_ng.app.dto.response.ResponseDTO;
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
    public ResponseDTO saveBooking(Long roomId, Long userId, Booking bookingRequest) {
        ResponseDTO responseDto = new ResponseDTO();

        try {
            if (bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())) {
                throw new OurException("Debes realizar el check in antes del checkout");
            }
            if (bookingRequest.getCheckInDate().isBefore(LocalDate.now())) {
                throw new OurException("La fecha de check-in no puede ser anterior a hoy");
            }

            Room room = roomRepository.findById(roomId).orElseThrow(() -> new OurException("Habitación no encontrada"));
            User user = userRepository.findById(userId).orElseThrow(() -> new OurException("Usuario no encontrado"));

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
