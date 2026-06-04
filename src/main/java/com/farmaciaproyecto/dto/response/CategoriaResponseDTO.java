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
public class CategoriaResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Boolean activo;
    private Long userId; // ID del usuario al que pertenece la categoría
}
