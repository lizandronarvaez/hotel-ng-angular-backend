package com.hotel_ng.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.*;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterUserDto {
    @NotBlank(message = "El nombre es obligatorio")
    private String fullName;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Por favor, ingrese un email válido")
    private String email;

    @NotBlank(message = "El password es obligatorio")
    private String password;

    @NotBlank(message = "El teléfono es obligatorio")
    private String numberPhone;
}
