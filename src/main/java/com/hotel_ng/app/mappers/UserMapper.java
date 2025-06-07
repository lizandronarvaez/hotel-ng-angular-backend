package com.hotel_ng.app.mappers;

import com.hotel_ng.app.dto.UserDTO;
import com.hotel_ng.app.entity.User;
import com.hotel_ng.app.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserMapper {

    public User mapUserDtoToUserEntity(UserDTO userDTO) {
        return User.builder()
                .fullname(userDTO.getFullname())
                .email(userDTO.getEmail())
                .numberPhone(userDTO.getNumberPhone())
                .role(Role.valueOf(userDTO.getRole()))
                .build();
    }

    public UserDTO mapUserEntityToUserDto(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .fullname(user.getFullname())
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
                .fullname(user.getFullname())
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
