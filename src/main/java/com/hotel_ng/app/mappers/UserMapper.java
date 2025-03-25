package com.hotel_ng.app.mappers;

import com.hotel_ng.app.dto.UserDto;
import com.hotel_ng.app.entity.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserMapper {

    public UserDto mapUserEntityToUserDto(Client user) {
        return UserDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .numberPhone(user.getNumberPhone())
                .role(user.getRole().name())
                .build();
    }

    public List<UserDto> mapUserListEntityToUserDtoList(List<Client> users) {
        return users.stream()
                .map(this::mapUserEntityToUserDto)
                .toList();
    }

    public UserDto mapUserEntityToUserDtoWithBookingAndRoom(Client user,BookingMapper bookingMapper) {
        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .numberPhone(user.getNumberPhone())
                .role(user.getRole().name())
                .build();

        if (!user.getListBookings().isEmpty()) {
            userDto.setBookings(user.getListBookings().stream()
                    .map(bookingMapper::mapBookingEntityToBookingDto)
                    .toList());
        }
        return userDto;
    }
}
