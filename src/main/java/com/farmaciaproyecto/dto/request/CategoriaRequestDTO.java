package com.farmaciaproyecto.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class CategoriaRequestDTO {

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(max = 35, message = "El nombre de la categoría no puede exceder los 35 caracteres")
    private String nombre;
    @Size(max = 50, message = "La descripción de la categoría no puede exceder los 50 caracteres")
    private String descripcion;
    @NotBlank(message = "El estado es obligatorio")
    private Boolean activo;
    @NotBlank(message = "El ID del usuario es obligatorio")
    private Long userId;
    
}
