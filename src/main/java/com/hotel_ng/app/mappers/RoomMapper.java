package com.hotel_ng.app.mappers;

import com.hotel_ng.app.dto.RoomDto;
import com.hotel_ng.app.entity.Room;
import com.hotel_ng.app.entity.ServiceRooms;
import com.hotel_ng.app.enums.RoomType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RoomMapper {

    public Room mapRoomDtoToRoomEntity(RoomType roomType, BigDecimal roomPrice, String description,
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

    public RoomDto mapRoomEntityToRoomDto(Room room) {
        return RoomDto.builder()
                .id(room.getId())
                .roomType(room.getRoomType().name().toLowerCase())
                .roomPrice(room.getRoomPrice())
                .roomDescription(room.getRoomDescription())
                .roomMaxOfGuest(room.getRoomMaxOfGuest())
                .serviceRooms(room.getServices())
                .roomImageUrl(room.getRoomImageUrl())
                .build();
    }

    public RoomDto mapRoomEntityToRoomDtoWithBooking(Room booking,BookingMapper bookingMapper) {
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
                    .map(bookingMapper::mapBookingEntityToBookingDto)
                    .toList());

        }
        return roomDto;
    }

    public List<RoomDto> mapRoomListEntityToRoomListDTO(List<Room> roomList) {
        return roomList.stream().map(this::mapRoomEntityToRoomDto).collect(Collectors.toList());
    }
}
