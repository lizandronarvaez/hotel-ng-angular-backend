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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
    private RoomServiceImpl roomService;

    final static String URL_IMAGE_FAKE = "http://fake-image-url.com";

    final static Room ROOM_PREPARED = Room.builder()
            .id(1L)
            .roomType(RoomType.PREMIUM)
            .roomPrice(new BigDecimal("60.00"))
            .roomDescription("Es una buena habitación")
            .roomMaxOfGuest(2)
            .roomImageUrl(URL_IMAGE_FAKE)
            .build();

    final static Room ROOM_MODIFIED_PREPARED = Room.builder()
            .id(1L)
            .roomType(RoomType.PREMIUM)
            .roomPrice(new BigDecimal("45.00"))
            .roomDescription("Es una buena habitación")
            .roomMaxOfGuest(2)
            .roomImageUrl(URL_IMAGE_FAKE)
            .build();

    final static RoomDto ROOM_DTO_PREPARED = RoomDto
            .builder()
            .id(1L)
            .roomImageUrl(URL_IMAGE_FAKE)
            .roomType("FAMILIAR")
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

        List<ServiceRooms> servicesList = new ArrayList<>();
        servicesList.add(wifiService);
        servicesList.add(tvService);
        servicesList.add(acService);

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
        ResponseDto response = roomService.addNewRoom(
                multipartFile,
                RoomType.STANDARD,
                new BigDecimal("30.00"),
                "Es una buena habitación",
                "2"
        );


        verify(cloudDinaryService, times(1)).uploadImage(any(MultipartFile.class), anyString());
        verify(roomServiceRepository, times(3)).findByName(anyString());
        verify(roomServiceRepository, times(3)).save(any(ServiceRooms.class));

        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals("Operación exitosa", response.getMessage());
        assertEquals(ROOM_DTO_PREPARED, response.getRoom());
    }

    @Test
    void testGetAllRoomTypes() {
        final Integer ROOM_TYPES_TOTAL = RoomType.values().length;

        List<String> roomTypes = Arrays.stream(RoomType.values())
                .map(types -> types.name().toUpperCase())
                .collect(Collectors.toList());

        when(roomRepository.findDistinctRoomType()).thenReturn(roomTypes);

        List<String> result = roomService.getAllRoomTypes();

        verify(roomRepository, times(1)).findDistinctRoomType();
        assertEquals(roomTypes, result);
        assertNotNull(result);
        assertEquals(ROOM_TYPES_TOTAL, result.size());
    }

    @Test
    void testGetAllRooms() {
        Pageable sortedPageable = PageRequest.of(1, 5, Sort.by(Sort.Direction.ASC, "id"));
        List<Room> roomList = Arrays.asList(new Room(), new Room());
        Page<Room> roomPage = new PageImpl<>(roomList, sortedPageable, 10);
        List<RoomDto> roomDtos = Arrays.asList(new RoomDto(), new RoomDto());
        List<RoomDto> result = roomMapper.mapRoomListEntityToRoomListDTO(roomPage.getContent());

        when(roomRepository.findAll(any(Pageable.class))).thenReturn(roomPage);
        when(roomMapper.mapRoomListEntityToRoomListDTO(roomPage.getContent())).thenReturn(roomDtos);

        ResponseDto responseDto = roomService.getAllRooms(sortedPageable);

        assertEquals(HttpStatus.OK.value(), responseDto.getStatusCode());
        assertEquals("Operación exitosa", responseDto.getMessage());
        assertEquals(1, responseDto.getPageNumber());
        assertEquals(5, responseDto.getPageSize());
        assertEquals(10, responseDto.getTotalElements());
        assertEquals(2, responseDto.getTotalPages());
        assertEquals(roomDtos, responseDto.getRoomList());
    }

    @Nested
    class DeleteRoom {

        @Test
        void testDeleteRoom() {

            when(roomRepository.findById(anyLong())).thenReturn(Optional.of(ROOM_PREPARED));
            ResponseDto responseDto = roomService.deleteRoom(ROOM_PREPARED.getId());

            verify(roomRepository, times(1)).findById(anyLong());
            verify(roomRepository, times(1)).deleteById(anyLong());

            assertEquals(HttpStatus.OK.value(), responseDto.getStatusCode());
            assertEquals("Operación exitosa", responseDto.getMessage());
        }

        @Test
        void testDeleteRoom_NotFound() {
            Long idRoom = 999L;
            when(roomRepository.findById(idRoom)).thenReturn(Optional.empty());

            ResponseDto responseDto = roomService.deleteRoom(idRoom);

            verify(roomRepository, times(1)).findById(anyLong());
            verify(roomRepository, times(0)).deleteById(anyLong());

            assertEquals(HttpStatus.NOT_FOUND.value(), responseDto.getStatusCode());
            assertEquals("No se encontró la habitación con el id: " + idRoom, responseDto.getMessage());
        }

    }

    @Test
    void testUpdateRoom() throws IOException {

        when(multipartFile.isEmpty()).thenReturn(false);
        when(cloudDinaryService.uploadImage(any(MultipartFile.class), anyString())).thenReturn(URL_IMAGE_FAKE);

        when(roomRepository.findById(anyLong())).thenReturn(Optional.of(ROOM_PREPARED));
        when(roomRepository.save(any(Room.class))).thenReturn(ROOM_PREPARED);
        when(roomMapper.mapRoomEntityToRoomDto(any(Room.class))).thenReturn(ROOM_DTO_PREPARED);

        ResponseDto responseDto = roomService.updateRoom(
                ROOM_MODIFIED_PREPARED.getId(),
                multipartFile,
                RoomType.PREMIUM,
                ROOM_MODIFIED_PREPARED.getRoomPrice(),
                ROOM_MODIFIED_PREPARED.getRoomDescription(),
                String.valueOf(ROOM_MODIFIED_PREPARED.getRoomMaxOfGuest()));

        // Verificar resultados
        verify(roomRepository, times(1)).findById(anyLong());
        verify(roomRepository, times(1)).save(any(Room.class));

        assertEquals(HttpStatus.OK.value(), responseDto.getStatusCode());
        assertEquals("Operación exitosa", responseDto.getMessage());
        assertEquals(ROOM_DTO_PREPARED, responseDto.getRoom());
    }

    @Test
    void testGetRoomById() {

        when(roomRepository.findById(anyLong())).thenReturn(Optional.of(ROOM_PREPARED));
        when(roomMapper.mapRoomEntityToRoomDto(any(Room.class))).thenReturn(ROOM_DTO_PREPARED);

        ResponseDto responseDto = roomService.getRoomById(ROOM_PREPARED.getId());

        verify(roomRepository, times(1)).findById(anyLong());
        assertEquals(HttpStatus.OK.value(), responseDto.getStatusCode());
        assertEquals("Operación exitosa", responseDto.getMessage());
        assertEquals(ROOM_DTO_PREPARED, responseDto.getRoom());
    }

    @Test
    void testGetAvailableRoomsByDateAndType() {
        List<Room> roomList = List.of(ROOM_PREPARED);

        when(roomRepository.findAvailableByDateAndTypes(any(LocalDate.class), any(LocalDate.class), any(RoomType.class))).thenReturn(roomList);
        when(roomMapper.mapRoomListEntityToRoomListDTO(anyList())).thenReturn(List.of(ROOM_DTO_PREPARED));

        ResponseDto response = roomService.getAvailableRoomsByDateAndType(LocalDate.now(), LocalDate.now(), RoomType.STANDARD);

        verify(roomRepository, times(1)).findAvailableByDateAndTypes(any(), any(), any());

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals("Operación exitosa", response.getMessage());
        assertNotNull(response.getRoomList());
        assertEquals(1, response.getRoomList().size());
    }

    @Nested
    class testAvailableRooms {

        @Test
        void testGetAvailableRooms() {
            List<Room> roomList = List.of(ROOM_PREPARED);

            when(roomRepository.findAllAvailableRooms()).thenReturn(roomList);
            when(roomMapper.mapRoomListEntityToRoomListDTO(roomList)).thenReturn(List.of(ROOM_DTO_PREPARED));

            ResponseDto response = roomService.getAvailableRooms();

            verify(roomRepository, times(1)).findAllAvailableRooms();
            assertNotNull(response);
            assertEquals(HttpStatus.OK.value(), response.getStatusCode());
            assertEquals("Operación exitosa", response.getMessage());
            assertFalse(response.getRoomList().isEmpty());
        }

        @Test
        void testGetAvailableRooms_NotFound() {

            when(roomRepository.findAllAvailableRooms()).thenReturn(Collections.emptyList());

            ResponseDto response = roomService.getAvailableRooms();

            verify(roomRepository, times(1)).findAllAvailableRooms();
            assertNotNull(response);
            assertEquals(HttpStatus.OK.value(), response.getStatusCode());
            assertEquals("Operación exitosa", response.getMessage());
            assertTrue(response.getRoomList().isEmpty());
        }

        @Test
        void testGetAvailableRooms_InternalServerError() {
            when(roomRepository.findAllAvailableRooms()).thenThrow(new RuntimeException("Error inesperado"));

            ResponseDto response = roomService.getAvailableRooms();

            assertNotNull(response);
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatusCode());
            assertEquals("Hubo un error al realizar la operación: Error inesperado", response.getMessage());
        }
    }


}