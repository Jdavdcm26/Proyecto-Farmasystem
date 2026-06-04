package com.farmaciaproyecto.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

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
public class ProductoResponseDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal iva;
    private Integer stock;
    private Integer stockMinimo;
    private Boolean activo;
    private Long userId;
    private Long categoriaId;
    private String categoriaNombre;
    private String nivelStock;

    // Datos derivados del lote vigente más próximo a vencer (con stock).
    private BigDecimal precio;             // precioVenta del lote vigente
    private LocalDate fechaVencimiento;    // vencimiento del lote vigente
}
