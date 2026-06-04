package com.farmaciaproyecto.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class VentaRequestDTO {

    @NotBlank(message = "El número de venta es obligatorio")
    private String numeroVenta;


    @NotNull(message = "El estado es obligatorio")
    private Long estadoId;

    // Relaciones por ID
    @NotBlank(message = "La cédula del cliente es obligatoria")
    private String clienteCedula;

    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId;

    // Detalles de la venta
    @NotNull(message = "La venta debe tener al menos un detalle")
    private List<DetalleVentaRequestDTO> detalleVentas;
}