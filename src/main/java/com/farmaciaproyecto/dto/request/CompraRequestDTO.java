package com.farmaciaproyecto.dto.request;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompraRequestDTO {

    @NotEmpty(message = "La compra debe tener al menos un producto")
    private List<DetalleVentaRequestDTO> detalleVentas;

    // Porcentaje de descuento (0 a 100). Si es null se asume 0.
    @DecimalMin(value = "0.00", message = "El descuento no puede ser negativo")
    @DecimalMax(value = "100.00", message = "El descuento no puede superar el 100%")
    private BigDecimal descuento;
}
