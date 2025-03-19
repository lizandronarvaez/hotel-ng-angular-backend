package com.hotel_ng.app.enums;

import lombok.Getter;

import java.util.*;

@Getter
public enum RoomType {
    STANDARD(List.of("Wi-Fi", "TV", "Aire Acondicionado")),
    PREMIUM(List.of("Wi-Fi", "TV", "Aire Acondicionado", "Desayuno incluido")),
    SUITE(List.of("Wi-Fi", "TV", "Aire Acondicionado", "Parking", "Desayuno incluido")),
    FAMILIAR(List.of("Wi-Fi", "TV", "Aire Acondicionado", "Parking", "Zona Infantil"));

    private final List<String> services;

    RoomType(List<String> services) {
        this.services = services;
    }

}
