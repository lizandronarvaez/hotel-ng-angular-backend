package com.hotel_ng.app.mappers;

import com.hotel_ng.app.dto.BookingDto;
import com.hotel_ng.app.dto.RoomDto;
import com.hotel_ng.app.entity.Booking;
import com.hotel_ng.app.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingMapper {
    private final UserMapper userMapper;
    private final RoomMapper roomMapper;

    public BookingDto mapBookingEntityToBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .bookingCode(booking.getConfirmationCode())
                .userDto(userMapper.mapUserEntityToUserDto(booking.getUser()))
                .roomDto(roomMapper.mapRoomEntityToRoomDto(booking.getRoom()))
                .build();
    }

    public BookingDto mapBookingEntityToBookingDtoWithRoom(Booking booking, boolean mapUser) {
        BookingDto bookingDto = BookingDto.builder()
                .id(booking.getId())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .bookingCode(booking.getConfirmationCode())
                // .maxOfGuest(booking.getMaxOfGuest())
                .userDto(userMapper.mapUserEntityToUserDto(booking.getUser()))
                .build();
        if (mapUser) {
            bookingDto.setUserDto(userMapper.mapUserEntityToUserDto(booking.getUser()));
        }
        if (booking.getRoom() != null) {
            RoomDto roomDto = RoomDto.builder()
                    .id(booking.getRoom().getId())
                    .roomType(booking.getRoom().getRoomType().name())
                    .roomMaxOfGuest(booking.getRoom().getRoomMaxOfGuest())
                    .roomImageUrl(booking.getRoom().getRoomImageUrl())
                    .roomDescription(booking.getRoom().getRoomDescription())
                    .roomPrice(booking.getRoom().getRoomPrice())
                    .build();
            bookingDto.setRoomDto(roomDto);
        }
        return bookingDto;
    }

    public  List<BookingDto> mapBookingListEntityToBookingDtoList(List<Booking> bookings) {
        return bookings.stream()
                .map(this::mapBookingEntityToBookingDto)
                .toList();
    }
}
