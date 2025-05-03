package com.hotel_ng.app.entity;

import java.math.BigDecimal;
import java.util.*;

import com.hotel_ng.app.enums.RoomType;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    private BigDecimal roomPrice;

    private String roomImageUrl;

    private String roomDescription;

    private int roomMaxOfGuest;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    // Nombre de la tabla de unión
    @JoinTable(name = "room_services",
            // Columna que referencia a Room
            joinColumns = @JoinColumn(name = "room_id"),
            // Columna que referencia a ServiceRooms
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<ServiceRooms> services = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private List<Booking> bookings = new ArrayList<>();

    @Override
    public String toString() {
        return "Room [id=" + id + ", roomMaxOfGuest=" + roomMaxOfGuest + ", roomType=" + roomType + ", roomPrice="
                + roomPrice + ", roomImageUrl=" + roomImageUrl + ", roomDescription=" + roomDescription + "]";
    }

}
