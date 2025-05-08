package com.hotel_ng.app.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Builder
@Setter
@Getter
public class RequestFormQuestionDTO {

    @NotBlank(message = "Campo obligatorio")
    @Email(message = "Por favor, ingrese un email válido")
    private String email;

    @NotBlank(message = "Campo obligatorio")
    private String fullName;

    private String numberPhone;

    @NotBlank(message = "Campo obligatorio")
    private String message;
}
