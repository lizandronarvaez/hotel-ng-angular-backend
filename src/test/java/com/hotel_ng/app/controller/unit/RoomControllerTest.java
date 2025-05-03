package com.hotel_ng.app.controller.unit;

import com.hotel_ng.app.controller.RoomController;
import com.hotel_ng.app.dto.response.ResponseDTO;
import com.hotel_ng.app.dto.RoomDTO;
import com.hotel_ng.app.enums.RoomType;
import com.hotel_ng.app.exception.OurException;
import com.hotel_ng.app.service.impl.RoomServiceImpl;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomControllerTest {

    @Mock
    MultipartFile multipartFile;

    @Mock
    private RoomServiceImpl roomService;

    @InjectMocks
    private RoomController roomController;

    static String MESSAGE_SUCCESS = "Operación exitosa";
    static String FIELD_EMPTY = "Por favor los campos son obligatorios";


    static RoomDTO ROOM_DTO_PREPARED_FIELD_NOT_EMPTY = RoomDTO.builder().id(1L).roomType("FAMILIAR").roomPrice(new BigDecimal("30.00")).roomDescription("Es una buena habitación").roomMaxOfGuest(2).build();

    static RoomDTO ROOM_DTO_PREPARED_FIELD_EMPTY = RoomDTO.builder().roomType(null).roomPrice(null).roomDescription(null).roomMaxOfGuest(null).roomImageUrl(null).build();
    static ResponseDTO RESPONSE_DTO_PREPARED = ResponseDTO.builder().room(ROOM_DTO_PREPARED_FIELD_NOT_EMPTY).message(MESSAGE_SUCCESS).statusCode(HttpStatus.OK.value()).build();

    static ResponseDTO RESPONSE_DTO_LIST_ROOMS = ResponseDTO.builder().statusCode(HttpStatus.OK.value()).message("Operación exitosa").roomList(List.of(ROOM_DTO_PREPARED_FIELD_NOT_EMPTY)).build();

    @Test
    void testGetAllRooms() {
        final int expectedPage = 1;
        final int expectedSize = 5;
        final String expectedSortField = "id";
        final Sort.Direction expectedDirection = Sort.Direction.ASC;

        Pageable sortedPageable = PageRequest.of(expectedPage, expectedSize, Sort.by(expectedDirection, expectedSortField));

        given(roomService.getAllRooms(any(Pageable.class))).willReturn(RESPONSE_DTO_LIST_ROOMS);

        ResponseEntity<ResponseDTO> response = roomController.getAllRooms(sortedPageable);

        verify(roomService, times(1)).getAllRooms(any(Pageable.class));
        verifyNoMoreInteractions(roomService);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MESSAGE_SUCCESS, response.getBody().getMessage());
        assertFalse(response.getBody().getRoomList().isEmpty());
    }

    @Nested
    class CreateRoom {

        @Test
        void testCreateRoomFieldNotEmpty() {
            when(roomService.addNewRoom(any(MultipartFile.class), any(RoomType.class), any(BigDecimal.class), anyString(), anyString())).thenReturn(RESPONSE_DTO_PREPARED);

            ResponseEntity<ResponseDTO> response = roomController.addNewRoom(multipartFile, RoomType.valueOf(ROOM_DTO_PREPARED_FIELD_NOT_EMPTY.getRoomType()), ROOM_DTO_PREPARED_FIELD_NOT_EMPTY.getRoomPrice(), ROOM_DTO_PREPARED_FIELD_NOT_EMPTY.getRoomDescription(), String.valueOf(ROOM_DTO_PREPARED_FIELD_NOT_EMPTY.getRoomMaxOfGuest()));

            verify(roomService).addNewRoom(any(MultipartFile.class), any(RoomType.class), any(BigDecimal.class), anyString(), anyString());
            verifyNoMoreInteractions(roomService);

            assertNotNull(response.getBody());
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(MESSAGE_SUCCESS, response.getBody().getMessage());
        }

        @Test
        void testCreateRoomFieldEmpty() {

            ResponseEntity<ResponseDTO> response = roomController.addNewRoom(multipartFile, RoomType.FAMILIAR, ROOM_DTO_PREPARED_FIELD_EMPTY.getRoomPrice(), ROOM_DTO_PREPARED_FIELD_EMPTY.getRoomDescription(), String.valueOf(ROOM_DTO_PREPARED_FIELD_EMPTY.getRoomMaxOfGuest()));

            verify(roomService, times(0)).addNewRoom(any(MultipartFile.class), any(RoomType.class), any(BigDecimal.class), anyString(), anyString());
            verifyNoInteractions(roomService);

            assertNotNull(response.getBody());
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals(FIELD_EMPTY, response.getBody().getMessage());
        }
    }

    @Nested
    class GetRoomById {

        @Test
        void testGetRoomByIdSuccess() {

            given(roomService.getRoomById(anyLong())).willReturn(RESPONSE_DTO_PREPARED);

            ResponseEntity<ResponseDTO> response = roomController.getRoomById(ROOM_DTO_PREPARED_FIELD_NOT_EMPTY.getId());
            verify(roomService, times(1)).getRoomById(ROOM_DTO_PREPARED_FIELD_NOT_EMPTY.getId());
            verifyNoMoreInteractions(roomService);

            assertNotNull(response.getBody());
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(MESSAGE_SUCCESS, response.getBody().getMessage());
        }

        @Test
        void testGetRoomByIdFailed() {

            long idNoExist = 999L;
            String ErrorMessage = "No se encontró la habitación con el id: " + idNoExist;

            when(roomService.getRoomById(anyLong())).thenThrow(new OurException(ErrorMessage));
            OurException exception = assertThrows(OurException.class, () -> roomController.getRoomById(idNoExist));

            verify(roomService, times(1)).getRoomById(idNoExist);
            assertEquals(ErrorMessage, exception.getMessage());
        }
    }

    @Test
    void updateRoom() {
    }

    @Test
    void deleteRoom() {
    }

    @Test
    void getTypesRooms() {
    }

    @Test
    void getAvailableRooms() {
    }

    @Test
    void getAvailableRoomsByDateAndType() {
    }
}