package com.hotel_ng.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hotel_ng.app.entity.ServiceRooms;
import java.util.Optional;

@Repository
public interface RoomServiceRepository extends JpaRepository<ServiceRooms, Long> {
    Optional<ServiceRooms> findByName(String name);
}