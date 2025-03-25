package com.hotel_ng.app.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hotel_ng.app.entity.Room;
import com.hotel_ng.app.enums.RoomType;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    // Obtener los tipos de habitaciones
    @Query("SELECT DISTINCT r.roomType from Room r")
    List<String> findDistinctRoomType();

    // obtener todas las habitaciones disponibles
    @Query("SELECT  r from Room r WHERE r.id NOT IN (SELECT b.room.id from Booking b)")
    List<Room> findAllAvailableRooms();



    @Query("SELECT r FROM Room r WHERE r.roomType = :roomType AND r.id NOT IN " +
            "(SELECT bk.room.id FROM Booking bk WHERE " +
            "(bk.checkInDate <= :checkOutDate) AND (bk.checkOutDate >= :checkInDate))")
    List<Room> findAvailableByDateAndTypes(
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("roomType") RoomType roomType);

    
}
