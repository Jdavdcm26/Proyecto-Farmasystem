package com.farmaciaproyecto.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardAdminResponseDTO {
    private Long totalProductos;
    private Long productosActivos;
    private Long productosStockBajo;
    private Long productosSinStock;
    private Long totalCategorias;
    private Long totalClientes;
    private Long totalVentas;
    private Long ventasCompletadas;
    private Long ventasAnuladas;
    private BigDecimal ingresosTotal;
    private Long ventasMesActual;
    private BigDecimal ingresosMesActual;
    private Long productosVencidos;
    private Long productosProximosVencer;
}