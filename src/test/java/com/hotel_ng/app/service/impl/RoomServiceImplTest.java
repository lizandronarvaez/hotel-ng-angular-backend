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
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

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

        roomDto = RoomDto.builder().roomImageUrl("http://fake-image-url.com").roomType("familiar").roomPrice(new BigDecimal("30.00")).roomDescription("Es una buena habitación").roomMaxOfGuest(2).build();

        room = new Room();
        room.setId(1L);
        room.setRoomType(RoomType.STANDARD);
        room.setRoomPrice(new BigDecimal("30.00"));
        room.setRoomDescription("Es una buena habitación");
        room.setRoomMaxOfGuest(2);
        room.setRoomImageUrl("http://fake-image-url.com");
    }

    @Test
    void testAddNewRoom() throws IOException {
        when(cloudDinaryService.uploadImage(multipartFile, "rooms")).thenReturn("http://fake-image-url.com");

        ServiceRooms wifiService = new ServiceRooms();
        wifiService.setName("Wi-Fi");
        ServiceRooms tvService = new ServiceRooms();
        tvService.setName("TV");
        ServiceRooms acService = new ServiceRooms();
        acService.setName("Aire Acondicionado");

        when(roomServiceRepository.findByName("Wi-Fi")).thenReturn(Optional.empty());
        when(roomServiceRepository.findByName("TV")).thenReturn(Optional.empty());
        when(roomServiceRepository.findByName("Aire Acondicionado")).thenReturn(Optional.empty());

        when(roomServiceRepository.save(any(ServiceRooms.class))).thenReturn(wifiService, tvService, acService);

        RoomDto expectedRoomDto = new RoomDto();

        when(roomMapper.mapRoomDtoToRoomEntity(eq(RoomType.STANDARD), eq(new BigDecimal("30.00")), eq("Es una buena habitación"), eq("2"), anyList(), eq("http://fake-image-url.com"))).thenReturn(room);

        when(roomRepository.save(room)).thenReturn(room);
        when(roomMapper.mapRoomEntityToRoomDto(room)).thenReturn(expectedRoomDto);

        ResponseDto response = roomService.addNewRoom(multipartFile, RoomType.STANDARD, new BigDecimal("30.00"), "Es una buena habitación", "2");

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals("Operación exitosa", response.getMessage());
        assertEquals(expectedRoomDto, response.getRoom());

        verify(cloudDinaryService, times(1)).uploadImage(multipartFile, "rooms");
        verify(roomServiceRepository, times(3)).findByName(anyString());
        verify(roomServiceRepository, times(3)).save(any(ServiceRooms.class));

    }

    @Test
    void testGetAllRoomTypes() {
        List<String> roomTypes = List.of(Arrays.toString(RoomType.values()));
        when(roomRepository.findDistinctRoomType()).thenReturn(roomTypes);

        List<String> result = roomService.getAllRoomTypes();

        verify(roomRepository, times(1)).findDistinctRoomType();
        assertEquals(roomTypes, result);
        assertNotNull(result);
    }

    @Test
    void testGetAllRooms() {
        Pageable sortedPageable = PageRequest.of(1, 5, Sort.by(Sort.Direction.ASC, "id"));
        List<Room> roomList = Arrays.asList(new Room(), new Room());
        Page<Room> roomPage = new PageImpl<>(roomList, sortedPageable, 10);
        List<RoomDto> roomDtos = Arrays.asList(new RoomDto(), new RoomDto());
        List<RoomDto> result = roomMapper.mapRoomListEntityToRoomListDTO(roomPage.getContent());

        when(roomRepository.findAll(sortedPageable)).thenReturn(roomPage);
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

    @Test
    void testDeleteRoom() {
        Long idRoom = roomDto.getId();
        Room room1 = new Room();
        room1.setId(1L);
        room1.setRoomType(RoomType.STANDARD);
        room1.setRoomPrice(new BigDecimal("30.00"));
        room1.setRoomDescription("Es una buena habitación");
        room1.setRoomMaxOfGuest(2);
        room1.setRoomImageUrl("http://fake-image-url.com");
        when(roomRepository.findById(idRoom)).thenReturn(Optional.of(room1));

        ResponseDto responseDto = roomService.deleteRoom(idRoom);

        assertEquals(HttpStatus.OK.value(), responseDto.getStatusCode());
        assertEquals("Operación exitosa", responseDto.getMessage());
    }

    @Test
    void testDeleteRoom_NotFound() {
        Long idRoom = 999L;
        when(roomRepository.findById(idRoom)).thenReturn(Optional.empty());

        ResponseDto responseDto = roomService.deleteRoom(idRoom);

        assertEquals(HttpStatus.NOT_FOUND.value(), responseDto.getStatusCode());
        assertEquals("No se encontró la habitación con el id: " + idRoom, responseDto.getMessage());
    }


    @Test
    void testUpdateRoom() throws IOException {
        // Preparar datos de entrada
        Long roomId = 1L;
        MultipartFile roomImageUrl = mock(MultipartFile.class);
        RoomType roomType = RoomType.PREMIUM;
        BigDecimal roomPrice = new BigDecimal("100.00");
        String description = "Habitación moderna";
        String roomMaxOfGuest = "4";

        Room room = new Room();
        Room savedRoom = new Room();
        RoomDto roomDto = new RoomDto();

        // Configurar mocks
        when(roomImageUrl.isEmpty()).thenReturn(false);
        when(cloudDinaryService.uploadImage(roomImageUrl, "rooms")).thenReturn("http://cloudinary.com/image.jpg");
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(roomRepository.save(any(Room.class))).thenReturn(savedRoom);
        when(roomMapper.mapRoomEntityToRoomDto(savedRoom)).thenReturn(roomDto);

        // Ejecutar el método
        ResponseDto responseDto = roomService.updateRoom(roomId, roomImageUrl, roomType, roomPrice, description, roomMaxOfGuest);

        // Verificar resultados
        assertEquals(HttpStatus.OK.value(), responseDto.getStatusCode());
        assertEquals("Operación exitosa", responseDto.getMessage());
        assertEquals(roomDto, responseDto.getRoom());
    }

    @Test
    void testGetRoomById() {

        Room room1 = new Room();
        room1.setId(1L);
        room1.setRoomType(RoomType.STANDARD);
        room1.setRoomPrice(new BigDecimal("30.00"));
        room1.setRoomDescription("Es una buena habitación");
        room1.setRoomMaxOfGuest(2);
        room1.setRoomImageUrl("http://fake-image-url.com");

        RoomDto roomDto = new RoomDto();
        roomDto.setId(1L);
        roomDto.setRoomType(RoomType.STANDARD.name());
        roomDto.setRoomPrice(new BigDecimal("30.00"));
        roomDto.setRoomDescription("Es una buena habitación");
        roomDto.setRoomMaxOfGuest(2);
        roomDto.setRoomImageUrl("http://fake-image-url.com");

        when(roomRepository.findById(room1.getId())).thenReturn(Optional.of(room1));
        when(roomMapper.mapRoomEntityToRoomDto(room1)).thenReturn(roomDto);

        ResponseDto responseDto = roomService.getRoomById(room1.getId());

        assertEquals(HttpStatus.OK.value(), responseDto.getStatusCode());
        assertEquals("Operación exitosa", responseDto.getMessage());
        assertEquals(roomDto, responseDto.getRoom());
    }

    @Test
    void testGetAvailableRoomsByDateAndType() {
        List<Room> roomList = List.of(room);

        when(roomRepository.findAvailableByDateAndTypes(any(), any(), any())).thenReturn(roomList);
        when(roomMapper.mapRoomListEntityToRoomListDTO(roomList)).thenReturn(List.of(roomDto));

        ResponseDto response = roomService.getAvailableRoomsByDateAndType(LocalDate.now(), LocalDate.now(), RoomType.STANDARD);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals("Operación exitosa", response.getMessage());
        assertNotNull(response.getRoomList());
        assertEquals(1, response.getRoomList().size());
    }

    @Test
    void testGetAvailableRooms() {
        List<Room> roomList = List.of(room);

        when(roomRepository.findAllAvailableRooms()).thenReturn(roomList);
        when(roomMapper.mapRoomListEntityToRoomListDTO(roomList)).thenReturn(List.of(roomDto));

        ResponseDto response = roomService.getAvailableRooms();

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals("Operación exitosa", response.getMessage());
        assertNotNull(response.getRoomList());
        assertEquals(1, response.getRoomList().size());
    }

    @Test
    void testGetAvailableRooms_NotFound() {
        when(roomRepository.findAllAvailableRooms()).thenReturn(Collections.emptyList());

        ResponseDto response = roomService.getAvailableRooms();

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