package com.farmaciaproyecto.dto.response;

import java.math.BigDecimal;

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
public class DetalleVentaResponseDTO {

    private Long id;

    // Producto
    private Long productoId;
    private String productoNombre;

    // Lote del que se despachó (para la factura)
    private Long loteId;
    private String numeroLote;

    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}
