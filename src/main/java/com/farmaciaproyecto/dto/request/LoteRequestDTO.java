package com.farmaciaproyecto.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoteRequestDTO {

    @NotBlank(message = "El número de lote es obligatorio")
    @Size(max = 30, message = "El número de lote no puede exceder los 30 caracteres")
    private String numeroLote;

    @NotNull(message = "La fecha de ingreso es obligatoria")
    private LocalDate fechaIngreso;

    @NotNull(message = "La fecha de vencimiento es obligatoria")
    private LocalDate fechaVencimiento;

    @NotNull(message = "El precio de costo es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio de costo debe ser un valor positivo")
    private BigDecimal precioCosto;

    @NotNull(message = "El porcentaje de utilidad es obligatorio")
    @DecimalMin(value = "0.00", message = "El porcentaje de utilidad no puede ser negativo")
    private BigDecimal porcentajeUtilidad;

    @NotNull(message = "El stock del lote es obligatorio")
    @PositiveOrZero(message = "El stock del lote no puede ser negativo")
    private Integer stock;

    @NotNull(message = "El estado del lote es obligatorio")
    private Boolean activo;

    @NotNull(message = "El ID del producto es obligatorio")
    private Long productoId;
}
