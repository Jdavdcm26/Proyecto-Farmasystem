package com.farmaciaproyecto.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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
public class ProductoRequestDTO {

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 35, message = "El nombre del producto no puede exceder los 35 caracteres")
    private String nombre;

    @Size(max = 50, message = "La descripción del producto no puede exceder los 50 caracteres")
    private String descripcion;

    @NotNull(message = "El IVA del producto es obligatorio")
    @DecimalMin(value = "0.00", message = "El IVA no puede ser negativo")
    private BigDecimal iva;

    @NotNull(message = "El stock del producto es obligatorio")
    @PositiveOrZero(message = "El stock no puede ser negativo")
    private Integer stock;

    @NotNull(message = "El stock mínimo del producto es obligatorio")
    @PositiveOrZero(message = "El stock mínimo no puede ser negativo")
    private Integer stockMinimo;

    @NotNull(message = "El estado del producto es obligatorio")
    private Boolean activo;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long userId;

    @NotNull(message = "El ID de la categoría es obligatorio")
    private Long categoriaId;
}
