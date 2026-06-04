package com.farmaciaproyecto.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatMensajeRequestDTO {

    @NotBlank(message = "El mensaje no puede estar vacío")
    private String mensaje;
}
