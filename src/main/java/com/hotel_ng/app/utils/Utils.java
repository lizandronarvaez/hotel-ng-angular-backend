package com.hotel_ng.app.utils;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.multipart.MultipartFile;

import com.hotel_ng.app.dto.*;
import com.hotel_ng.app.entity.*;
import com.hotel_ng.app.enums.RoomType;

public class Utils {

    private static final String ALPHANUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateCodeBooking(int length) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(ALPHANUMERIC_STRING.length());
            char randomChar = ALPHANUMERIC_STRING.charAt(randomIndex);
            stringBuilder.append(randomChar);
        }
        return stringBuilder.toString();
    }

    public static AdminDto mapAdminEntityToAdminDto(Admin admin) {
        return AdminDto.builder()
                .id(admin.getId())
                .email(admin.getEmail())
                .role(admin.getRole().name())
                .build();
    }

    public static UserDto mapUserEntityToUserDto(Client user) {
        return UserDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .numberPhone(user.getNumberPhone())
                .role(user.getRole().name())
                .build();
    }

    public static Room mapRoomDtoToRoomEntity(MultipartFile roomImage, RoomType roomType, BigDecimal roomPrice,
            String description,
            String roomMaxOfGuest, List<ServiceRooms> services, String imageUrl) {
        return Room.builder()
                .roomType(roomType)
                .roomPrice(roomPrice)
                .roomMaxOfGuest(Integer.parseInt(roomMaxOfGuest))
                .roomDescription(description)
                .services(services)
                .roomImageUrl(imageUrl)
                .build();
    }

    public static RoomDto mapRoomEntityToRoomDto(Room room) {
        return RoomDto.builder()
                .id(room.getId())
                .roomType(room.getRoomType().name())
                .roomPrice(room.getRoomPrice())
                .roomImageUrl(room.getRoomImageUrl())
                .roomDescription(room.getRoomDescription())
                .roomMaxOfGuest(room.getRoomMaxOfGuest())
                .serviceRooms(room.getServices())
                .build();
    }

    public static BookingDto mapBookingEntityToBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .bookingCode(booking.getConfirmationCode())
                .userDto(mapUserEntityToUserDto(booking.getUser()))
                .roomDto(mapRoomEntityToRoomDto(booking.getRoom()))
                .build();
    }

    public static RoomDto mapRoomEntityToRoomDtoWithBooking(Room booking) {
        RoomDto roomDto = RoomDto.builder()
                .id(booking.getId())
                .roomType(booking.getRoomType().name())
                .roomMaxOfGuest(booking.getRoomMaxOfGuest())
                .roomImageUrl(booking.getRoomImageUrl())
                .roomDescription(booking.getRoomDescription())
                .roomPrice(booking.getRoomPrice())
                .build();

        if (booking.getBookings() != null) {
            roomDto.setBookings(booking.getBookings().stream()
                    .map(Utils::mapBookingEntityToBookingDto)
                    .toList());

        }
        return roomDto;
    }

    public static BookingDto mapBookingEntityToBookingDtoWithRoom(Booking booking, boolean mapUser) {
        BookingDto bookingDto = BookingDto.builder()
                .id(booking.getId())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .bookingCode(booking.getConfirmationCode())
                // .maxOfGuest(booking.getMaxOfGuest())
                .userDto(mapUserEntityToUserDto(booking.getUser()))
                .build();
        if (mapUser) {
            bookingDto.setUserDto(mapUserEntityToUserDto(booking.getUser()));
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

    public static UserDto mapUserEntityToUserDtoWithBookingAndRoom(Client user) {
        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .numberPhone(user.getNumberPhone())
                .role(user.getRole().name())
                .build();

        if (!user.getListBookings().isEmpty()) {
            userDto.setBookings(user.getListBookings().stream()
                    .map(Utils::mapBookingEntityToBookingDto)
                    .toList());
        }
        return userDto;
    }

    public static List<UserDto> mapUserListEntityToUserDtoList(List<Client> users) {
        return users.stream()
                .map(Utils::mapUserEntityToUserDto)
                .toList();
    }

    public static List<BookingDto> mapBookingListEntityToBookingDtoList(List<Booking> bookings) {
        return bookings.stream()
                .map(Utils::mapBookingEntityToBookingDto)
                .toList();
    }

    public static List<RoomDto> mapRoomListEntityToRoomListDTO(List<Room> roomList) {
        return roomList.stream().map(Utils::mapRoomEntityToRoomDto).collect(Collectors.toList());
    }

}
