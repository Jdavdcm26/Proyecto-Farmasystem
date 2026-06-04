package com.farmaciaproyecto.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoteResponseDTO {

    private Long id;
    private String numeroLote;
    private LocalDate fechaIngreso;
    private LocalDate fechaVencimiento;
    private BigDecimal precioCosto;
    private BigDecimal porcentajeUtilidad;
    private BigDecimal precioVenta;
    private Integer stock;
    private Boolean activo;
    private Long productoId;
    private String productoNombre;
}
