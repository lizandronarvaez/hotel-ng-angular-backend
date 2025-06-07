package com.hotel_ng.app.entity;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;

@ToString
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String confirmationCode;
    private String totalNights;
    private String totalPriceNights;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;
}
