package com.hotel_ng.app.mappers;

import com.hotel_ng.app.dto.UserDTO;
import com.hotel_ng.app.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserMapper {

    public UserDTO mapUserEntityToUserDto(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .numberPhone(user.getNumberPhone())
                .role(user.getRole().name())
                .build();
    }

    public List<UserDTO> mapUserListEntityToUserDtoList(List<User> users) {
        return users.stream()
                .map(this::mapUserEntityToUserDto)
                .toList();
    }

    public UserDTO mapUserEntityToUserDtoWithBookingAndRoom(User user, BookingMapper bookingMapper) {
        UserDTO userDto = UserDTO.builder()
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
