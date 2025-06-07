package com.hotel_ng.app.mappers;

import com.hotel_ng.app.dto.BookingDTO;
import com.hotel_ng.app.dto.RoomDTO;
import com.hotel_ng.app.entity.Booking;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingMapper {
    private final UserMapper userMapper;
    private final RoomMapper roomMapper;

    public BookingDTO mapBookingEntityToBookingDto(Booking booking) {
        return BookingDTO.builder()
                .id(booking.getId())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .bookingCode(booking.getConfirmationCode())
                .totalNights(booking.getTotalNights())
                .totalPriceNights(booking.getTotalPriceNights())
                .userDto(userMapper.mapUserEntityToUserDto(booking.getUser()))
                .roomDto(roomMapper.mapRoomEntityToRoomDto(booking.getRoom()))
                .build();
    }

    public Booking mapBookingDtoToBookingEntity(BookingDTO bookingDTO) {
        return Booking.builder()
                .checkInDate(bookingDTO.getCheckInDate())
                .checkOutDate(bookingDTO.getCheckOutDate())
                .confirmationCode(bookingDTO.getBookingCode())
                .totalPriceNights(bookingDTO.getTotalPriceNights())
                .totalNights(bookingDTO.getTotalNights())
                .user(userMapper.mapUserDtoToUserEntity(bookingDTO.getUserDto()))
                .room(roomMapper.mapRoomDtoToRoomEntity(bookingDTO.getRoomDto()))
                .build();
    }

    public BookingDTO mapBookingEntityToBookingDtoWithRoom(Booking booking, boolean mapUser) {
        BookingDTO bookingDto = BookingDTO.builder()
                .id(booking.getId())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .totalNights(booking.getTotalNights())
                .totalPriceNights(booking.getTotalPriceNights())
                .bookingCode(booking.getConfirmationCode())
                .userDto(userMapper.mapUserEntityToUserDto(booking.getUser()))
                .build();
        if (mapUser) {
            bookingDto.setUserDto(userMapper.mapUserEntityToUserDto(booking.getUser()));
        }
        if (booking.getRoom() != null) {
            RoomDTO roomDto = RoomDTO.builder()
                    .id(booking.getRoom().getId())
                    .roomType(booking.getRoom().getRoomType())
                    .roomMaxOfGuest(booking.getRoom().getRoomMaxOfGuest())
                    .roomImageUrl(booking.getRoom().getRoomImageUrl())
                    .roomDescription(booking.getRoom().getRoomDescription())
                    .roomPrice(booking.getRoom().getRoomPrice())
                    .build();
            bookingDto.setRoomDto(roomDto);
        }
        return bookingDto;
    }

    public  List<BookingDTO> mapBookingListEntityToBookingDtoList(List<Booking> bookings) {
        return bookings.stream()
                .map(this::mapBookingEntityToBookingDto)
                .toList();
    }
}
