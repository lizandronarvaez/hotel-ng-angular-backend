package com.hotel_ng.app.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class RequestFormQuestionDTO {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Por favor, ingrese un email válido")
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    private String fullName;

    private String numberPhone;

    @NotBlank(message = "El teléfono es obligatorio")
    private String message;
}
