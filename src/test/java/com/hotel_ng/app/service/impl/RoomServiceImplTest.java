package com.hotel_ng.app.service.impl;

import com.hotel_ng.app.dto.ResponseDto;
import com.hotel_ng.app.dto.RoomDto;
import com.hotel_ng.app.entity.Room;
import com.hotel_ng.app.entity.ServiceRooms;
import com.hotel_ng.app.enums.RoomType;
import com.hotel_ng.app.mappers.RoomMapper;
import com.hotel_ng.app.repository.RoomRepository;
import com.hotel_ng.app.repository.RoomServiceRepository;
import com.hotel_ng.app.uploads.cloudDinary.service.CloudDinaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceImplTest {

    @Mock
    private RoomRepository roomRepository;
    @Mock
    private RoomServiceRepository roomServiceRepository;
    @Mock
    private CloudDinaryService cloudDinaryService;
    @Mock
    private MultipartFile multipartFile;
    @Mock
    private RoomMapper roomMapper;

    @InjectMocks
    RoomServiceImpl roomService;
    public RoomDto roomDto;
    public Room room;

    @BeforeEach()
    void setUp() {

        roomDto = RoomDto.builder()
                .roomImageUrl("http://fake-image-url.com")
                .roomType("familiar")
                .roomPrice(new BigDecimal("30.00"))
                .roomDescription("Es una buena habitación")
                .roomMaxOfGuest(2)
                .build();

        room = new Room();
        room.setId(1L);
        room.setRoomType(RoomType.STANDARD);
        room.setRoomPrice(new BigDecimal("30.00"));
        room.setRoomDescription("Es una buena habitación");
        room.setRoomMaxOfGuest(2);
        room.setRoomImageUrl("http://fake-image-url.com");
    }

    @Test
    void addNewRoom() throws IOException {
        when(cloudDinaryService.uploadImage(multipartFile, "rooms"))
                .thenReturn("http://fake-image-url.com");

        ServiceRooms wifiService = new ServiceRooms();
        wifiService.setName("Wi-Fi");
        ServiceRooms tvService = new ServiceRooms();
        tvService.setName("TV");
        ServiceRooms acService = new ServiceRooms();
        acService.setName("Aire Acondicionado");

        when(roomServiceRepository.findByName("Wi-Fi")).thenReturn(Optional.empty());
        when(roomServiceRepository.findByName("TV")).thenReturn(Optional.empty());
        when(roomServiceRepository.findByName("Aire Acondicionado")).thenReturn(Optional.empty());

        when(roomServiceRepository.save(any(ServiceRooms.class)))
                .thenReturn(wifiService, tvService, acService);

        RoomDto expectedRoomDto = new RoomDto();

        when(roomMapper.mapRoomDtoToRoomEntity(
                eq(RoomType.STANDARD),
                eq(new BigDecimal("30.00")),
                eq("Es una buena habitación"),
                eq("2"),
                anyList(),
                eq("http://fake-image-url.com")))
                .thenReturn(room);

        when(roomRepository.save(room)).thenReturn(room);
        when(roomMapper.mapRoomEntityToRoomDto(room)).thenReturn(expectedRoomDto);

        ResponseDto response = roomService.addNewRoom(
                multipartFile,
                RoomType.STANDARD,
                new BigDecimal("30.00"),
                "Es una buena habitación",
                "2"
        );

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals("Operación exitosa", response.getMessage());
        assertEquals(expectedRoomDto, response.getRoom());

        verify(cloudDinaryService, times(1)).uploadImage(multipartFile, "rooms");
        verify(roomServiceRepository, times(3)).findByName(anyString());
        verify(roomServiceRepository, times(3)).save(any(ServiceRooms.class));

    }

    @Test
    void getAllRoomTypes() {
    }

    @Test
    void getAllRooms() {
    }

    @Test
    void deleteRoom() {
    }

    @Test
    void updateRoom() {
    }

    @Test
    void getRoomById() {
    }

    @Test
    void getAvailableRoomsByDateAndType() {
    }

    @Test
    void getAvailableRooms() {
    }

    @Test
    void testAddNewRoom() {
    }

    @Test
    void testGetAllRoomTypes() {
    }

    @Test
    void testGetAllRooms() {
    }

    @Test
    void testDeleteRoom() {
    }

    @Test
    void testUpdateRoom() {
    }

    @Test
    void testGetRoomById() {
    }

    @Test
    void testGetAvailableRoomsByDateAndType() {
    }

    @Test
    void testGetAvailableRooms() {
    }
}