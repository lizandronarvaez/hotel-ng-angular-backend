package com.hotel_ng.app.service.impl;

import com.hotel_ng.app.dto.RoomDTO;
import com.hotel_ng.app.entity.Room;
import com.hotel_ng.app.entity.ServiceRooms;
import com.hotel_ng.app.enums.RoomType;
import com.hotel_ng.app.mappers.RoomMapper;
import com.hotel_ng.app.repository.RoomRepository;
import com.hotel_ng.app.repository.RoomServiceRepository;
import com.hotel_ng.app.uploads.cloudDinary.service.CloudDinaryService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
    private RoomServiceImpl roomService;

    static String URL_IMAGE_FAKE = "http://fake-image-url.com";

    static Room ROOM_PREPARED = Room.builder()
            .id(1L)
            .roomType(RoomType.PREMIUM)
            .roomPrice(new BigDecimal("60.00"))
            .roomDescription("Es una buena habitación")
            .roomMaxOfGuest(2)
            .roomImageUrl(URL_IMAGE_FAKE)
            .build();

    static Room ROOM_MODIFIED_PREPARED = Room.builder()
            .id(1L)
            .roomType(RoomType.PREMIUM)
            .roomPrice(new BigDecimal("45.00"))
            .roomDescription("Es una buena habitación")
            .roomMaxOfGuest(2)
            .roomImageUrl(URL_IMAGE_FAKE)
            .build();

    static RoomDTO ROOM_DTO_PREPARED = RoomDTO
            .builder()
            .id(1L)
            .roomImageUrl(URL_IMAGE_FAKE)
            .roomType(RoomType.valueOf("FAMILIAR"))
            .roomPrice(new BigDecimal("30.00"))
            .roomDescription("Es una buena habitación")
            .roomMaxOfGuest(2)
            .build();

    @Test
    void testAddNewRoom() throws IOException {

        when(cloudDinaryService.uploadImage(any(MultipartFile.class), anyString())).thenReturn(URL_IMAGE_FAKE);

        ServiceRooms wifiService = ServiceRooms.builder().name("Wi-Fi").build();
        ServiceRooms tvService = ServiceRooms.builder().name("TV").build();
        ServiceRooms acService = ServiceRooms.builder().name("Aire Acondicionado").build();

        when(roomServiceRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(roomServiceRepository.save(any(ServiceRooms.class))).thenReturn(wifiService, tvService, acService);

        when(roomMapper.mapRoomDtoToRoomEntity(
                any(RoomType.class),
                any(BigDecimal.class),
                anyString(),
                anyString(),
                anyList(),
                anyString()
        )).thenReturn(ROOM_PREPARED);


        when(roomRepository.save(any(Room.class))).thenReturn(ROOM_PREPARED);
        when(roomMapper.mapRoomEntityToRoomDto(any(Room.class))).thenReturn(ROOM_DTO_PREPARED);

        //llamada al servicio
        roomService.addNewRoom(
                multipartFile,
                RoomType.STANDARD,
                new BigDecimal("30.00"),
                "Es una buena habitación",
                "2"
        );


        verify(cloudDinaryService, times(1)).uploadImage(any(MultipartFile.class), anyString());
        verify(roomServiceRepository, times(3)).findByName(anyString());
        verify(roomServiceRepository, times(3)).save(any(ServiceRooms.class));
    }

    @Test
    void testGetAllRoomTypes() {
        List<String> roomTypes = Arrays.stream(RoomType.values())
                .map(types -> types.name().toUpperCase())
                .collect(Collectors.toList());

        when(roomRepository.findDistinctRoomType()).thenReturn(roomTypes);

        roomService.getAllRoomTypes();

        verify(roomRepository, times(1)).findDistinctRoomType();
    }

    @Test
    void testGetAllRooms() {
        Pageable sortedPageable = PageRequest.of(1, 5, Sort.by(Sort.Direction.ASC, "id"));
        List<Room> roomList = Arrays.asList(new Room(), new Room());
        Page<Room> roomPage = new PageImpl<>(roomList, sortedPageable, 10);
        List<RoomDTO> roomsDTO = Arrays.asList(new RoomDTO(), new RoomDTO());

        when(roomRepository.findAll(any(Pageable.class))).thenReturn(roomPage);
        when(roomMapper.mapRoomListEntityToRoomListDTO(roomPage.getContent())).thenReturn(roomsDTO);

        roomService.getAllRooms(sortedPageable);

        verify(roomRepository, times(1)).findAll(any(Pageable.class));
        verify(roomMapper, times(1)).mapRoomListEntityToRoomListDTO(anyList());
    }

    @Nested
    class DeleteRoom {

        @Test
        void testDeleteRoom() {

            when(roomRepository.findById(anyLong())).thenReturn(Optional.of(ROOM_PREPARED));
            roomService.deleteRoom(ROOM_PREPARED.getId());

            verify(roomRepository, times(1)).findById(anyLong());
            verify(roomRepository, times(1)).deleteById(anyLong());
        }

        @Test
        void testDeleteRoom_NotFound() {
            Long idRoom = 999L;
            when(roomRepository.findById(idRoom)).thenReturn(Optional.empty());

            roomService.deleteRoom(idRoom);

            verify(roomRepository, times(1)).findById(anyLong());
            verify(roomRepository, never()).deleteById(anyLong());
        }

    }

    @Test
    void testUpdateRoom() throws IOException {

        when(multipartFile.isEmpty()).thenReturn(false);
        when(cloudDinaryService.uploadImage(any(MultipartFile.class), anyString())).thenReturn(URL_IMAGE_FAKE);

        when(roomRepository.findById(anyLong())).thenReturn(Optional.of(ROOM_PREPARED));
        when(roomRepository.save(any(Room.class))).thenReturn(ROOM_PREPARED);
        when(roomMapper.mapRoomEntityToRoomDto(any(Room.class))).thenReturn(ROOM_DTO_PREPARED);

        roomService.updateRoom(
                ROOM_MODIFIED_PREPARED.getId(),
                multipartFile,
                RoomType.PREMIUM,
                ROOM_MODIFIED_PREPARED.getRoomPrice(),
                ROOM_MODIFIED_PREPARED.getRoomDescription(),
                String.valueOf(ROOM_MODIFIED_PREPARED.getRoomMaxOfGuest()));

        verify(roomRepository, times(1)).findById(anyLong());
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    void testGetRoomById() {

        when(roomRepository.findById(anyLong())).thenReturn(Optional.of(ROOM_PREPARED));
        when(roomMapper.mapRoomEntityToRoomDto(any(Room.class))).thenReturn(ROOM_DTO_PREPARED);

        roomService.getRoomById(ROOM_PREPARED.getId());

        verify(roomRepository, times(1)).findById(anyLong());
        verify(roomMapper, times(1)).mapRoomEntityToRoomDto(any(Room.class));

    }

    @Test
    void testGetAvailableRoomsByDateAndType() {
        List<Room> roomList = List.of(ROOM_PREPARED);

        when(roomRepository.findAvailableByDateAndTypes(any(LocalDate.class), any(LocalDate.class), any(RoomType.class))).thenReturn(roomList);
        when(roomMapper.mapRoomListEntityToRoomListDTO(anyList())).thenReturn(List.of(ROOM_DTO_PREPARED));

        roomService.getAvailableRoomsByDateAndType(LocalDate.now(), LocalDate.now(), RoomType.STANDARD);

        verify(roomRepository, times(1)).findAvailableByDateAndTypes(any(), any(), any());
    }

    @Nested
    class testAvailableRooms {
        List<Room> roomList = List.of(ROOM_PREPARED);

        @Test
        void testGetAvailableRooms() {

            when(roomRepository.findAllAvailableRooms()).thenReturn(roomList);
            when(roomMapper.mapRoomListEntityToRoomListDTO(roomList)).thenReturn(List.of(ROOM_DTO_PREPARED));

            roomService.getAvailableRooms();

            verify(roomRepository, times(1)).findAllAvailableRooms();
            verify(roomMapper, times(1)).mapRoomListEntityToRoomListDTO(anyList());
        }

        @Test
        void testGetAvailableRooms_NotFound() {

            when(roomRepository.findAllAvailableRooms()).thenReturn(Collections.emptyList());

            roomService.getAvailableRooms();

            verify(roomRepository, times(1)).findAllAvailableRooms();
        }

        @Test
        void testGetAvailableRooms_InternalServerError() {
            when(roomRepository.findAllAvailableRooms()).thenThrow(new RuntimeException("Error inesperado"));

            roomService.getAvailableRooms();

            verify(roomRepository, times(1)).findAllAvailableRooms();
        }
    }


}