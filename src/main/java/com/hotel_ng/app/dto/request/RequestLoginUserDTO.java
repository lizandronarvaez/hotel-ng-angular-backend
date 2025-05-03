package com.hotel_ng.app.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.*;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestLoginUserDTO {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Por favor, ingrese un email válido")
    private String email;

    @NotBlank(message = "El password es obligatorio")
    private String password;
}