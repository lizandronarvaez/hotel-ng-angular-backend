package com.hotel_ng.app.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hotel_ng.app.dto.*;
import com.hotel_ng.app.entity.Room;
import com.hotel_ng.app.entity.ServiceRooms;
import com.hotel_ng.app.enums.RoomType;
import com.hotel_ng.app.exception.OurException;
import com.hotel_ng.app.repository.*;
import com.hotel_ng.app.service.interfaces.RoomService;
import com.hotel_ng.app.uploads.cloudDinary.service.CloudDinaryService;
import com.hotel_ng.app.utils.Utils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final CloudDinaryService cloudDinaryService;
    private final RoomServiceRepository roomServiceRepository;

    @Override
    public ResponseDto addNewRoom(MultipartFile roomImage, RoomType roomType, BigDecimal roomPrice, String description,
            String roomMaxOfGuest) {
        ResponseDto responseDto = new ResponseDto();
        List<ServiceRooms> services = findOrCreateServices(roomType);
        String imageUrl = null;

        try {
            if (roomImage != null)
                imageUrl = cloudDinaryService.uploadImage(roomImage, "rooms");

            Room room = Utils.mapRoomDtoToRoomEntity(roomImage, roomType, roomPrice, description, roomMaxOfGuest,
                    services, imageUrl);
            roomRepository.save(room);
            RoomDto roomDto = Utils.mapRoomEntityToRoomDto(room);

            responseDto.setRoom(roomDto);
            responseDto.setMessage("Operación exitosa");
            responseDto.setStatusCode(HttpStatus.OK.value());

        } catch (Exception e) {
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setMessage("Hubo un error al realizar la operación: " + e.getMessage());
        }
        return responseDto;
    }

    @Override
    public List<String> getAllRoomTypes() {
        return roomRepository.findDistinctRoomType();
    }

    @Override
    public ResponseDto getAllRooms(Pageable pageable) {
        ResponseDto responseDto = new ResponseDto();

        try {
            Pageable sortedPageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.ASC, "id"));
            Page<Room> roomPage = roomRepository.findAll(sortedPageable);
            List<RoomDto> roomDtos = Utils.mapRoomListEntityToRoomListDTO(roomPage.getContent());

            responseDto.setRoomList(roomDtos);
            responseDto.setMessage("Operación exitosa");
            responseDto.setStatusCode(HttpStatus.OK.value());

            responseDto.setPageNumber(roomPage.getNumber());
            responseDto.setPageSize(roomPage.getSize());
            responseDto.setTotalElements(roomPage.getTotalElements());
            responseDto.setTotalPages(roomPage.getTotalPages());

        } catch (Exception e) {
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setMessage("Hubo un error al realizar la operación: " + e.getMessage());
        }
        return responseDto;
    }

    @Override
    public ResponseDto deleteRoom(Long roomId) {
        ResponseDto responseDto = new ResponseDto();

        try {
            roomRepository.findById(roomId)
                    .orElseThrow(() -> new OurException("No se encontró la habitación con el id: " + roomId));
            roomRepository.deleteById(roomId);

            responseDto.setMessage("Operación exitosa");
            responseDto.setStatusCode(HttpStatus.OK.value());

        } catch (OurException e) {
            responseDto.setStatusCode(HttpStatus.NOT_FOUND.value());
            responseDto.setMessage(e.getMessage());

        } catch (Exception e) {
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setMessage("Hubo un error al realizar la operación: " + e.getMessage());
        }
        return responseDto;
    }

    @Override
    public ResponseDto updateRoom(Long roomId, MultipartFile roomImageUrl, RoomType roomType, BigDecimal roomPrice,
            String description, String roomMaxOfGuest) {
        ResponseDto responseDto = new ResponseDto();

        try {
            String imageUrl = null;
            if (roomImageUrl != null && !roomImageUrl.isEmpty()) {
                imageUrl = cloudDinaryService.uploadImage(roomImageUrl, "rooms");
            }

            Room roomUpdate = roomRepository.findById(roomId)
                    .orElseThrow(() -> new OurException("No se encontró la habitación con el id: " + roomId));
            if (imageUrl != null)
                roomUpdate.setRoomImageUrl(imageUrl);

            roomUpdate.setRoomType(roomType);
            roomUpdate.setRoomPrice(roomPrice);
            roomUpdate.setRoomDescription(description);
            roomUpdate.setRoomMaxOfGuest(Integer.parseInt(roomMaxOfGuest));

            Room savedRoom = roomRepository.save(roomUpdate);
            RoomDto roomDto = Utils.mapRoomEntityToRoomDto(savedRoom);

            responseDto.setMessage("Operación exitosa");
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setRoom(roomDto);

        } catch (Exception e) {
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setMessage("Hubo un error al realizar la operación: " + e.getMessage());
        }
        return responseDto;
    }

    @Override
    public ResponseDto getRoomById(Long roomId) {
        ResponseDto responseDto = new ResponseDto();

        try {
            Room room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new OurException("No se encontró la habitación con el id: " + roomId));
            RoomDto roomDto = Utils.mapRoomEntityToRoomDto(room);

            responseDto.setMessage("Operación exitosa");
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setRoom(roomDto);

        } catch (OurException e) {
            responseDto.setStatusCode(HttpStatus.NOT_FOUND.value());
            responseDto.setMessage(e.getMessage());

        } catch (Exception e) {
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setMessage("Hubo un error al realizar la operación: " + e.getMessage());
        }
        return responseDto;
    }

    @Override
    public ResponseDto getAvaliaRoomsByDateAndType(LocalDate checkInDate, LocalDate checkOutDate, RoomType roomType) {
        ResponseDto responseDto = new ResponseDto();

        try {
            List<Room> availableRooms = roomRepository.findAvailableByDateAndTypes(checkInDate, checkOutDate,
                    roomType);
            List<RoomDto> roomListDto = Utils.mapRoomListEntityToRoomListDTO(availableRooms);

            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setMessage("Operación exitosa");
            responseDto.setRoomList(roomListDto);

        } catch (OurException e) {
            responseDto.setStatusCode(HttpStatus.NOT_FOUND.value());
            responseDto.setMessage(e.getMessage());

        } catch (Exception e) {
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setMessage("Hubo un error al realizar la operación: " + e.getMessage());
        }
        return responseDto;
    }

    @Override
    public ResponseDto getAvaliableRooms() {
        ResponseDto responseDto = new ResponseDto();

        try {
            List<Room> availableRooms = roomRepository.findAllAvailableRooms();
            List<RoomDto> roomListDto = Utils.mapRoomListEntityToRoomListDTO(availableRooms);

            responseDto.setMessage("Operación exitosa");
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setRoomList(roomListDto);

        } catch (Exception e) {
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setMessage("Hubo un error al realizar la operación: " + e.getMessage());
        }
        return responseDto;
    }

    private List<ServiceRooms> findOrCreateServices(RoomType roomType) {
        // Obtener o crear los servicios del enum en la base de datos
        return roomType.getServices().stream().map(serviceName -> {
            return roomServiceRepository.findByName(serviceName).orElseGet(() -> {
                ServiceRooms newService = ServiceRooms.builder().name(serviceName).build();
                return roomServiceRepository.save(newService);
            });
        }).toList();
    }
}
