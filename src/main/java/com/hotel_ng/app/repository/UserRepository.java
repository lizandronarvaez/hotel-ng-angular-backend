package com.hotel_ng.app.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.hotel_ng.app.entity.Client;

public interface UserRepository extends JpaRepository<Client, Long> {

    boolean existsByEmail(String email);

    Optional<Client> findByEmail(String email);
}
