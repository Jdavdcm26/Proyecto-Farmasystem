package com.farmaciaproyecto.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClienteResponseDTO {
    private String cedula;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String direccion;
    private Boolean estado;
    private Long userId;
}
