package com.farmaciaproyecto.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
public class VentaResponseDTO {

    private Long id;
    private String numeroVenta;
    private LocalDateTime fechaVenta;

    // Desglose de totales
    private BigDecimal parcial;
    private BigDecimal descuento;
    private BigDecimal subtotal;
    private BigDecimal ivaMonto;
    private BigDecimal total;

    // Estado de la venta (catálogo)
    private Long estadoId;
    private String estado;

    // Cliente
    private String clienteCedula;
    private String clienteNombre;

    // Usuario
    private Long usuarioId;
    private String usuarioNombre;

    // Detalles
    private List<DetalleVentaResponseDTO> detalleVentas;
}