package com.farmaciaproyecto.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmaciaproyecto.dto.response.DashboardAdminResponseDTO;
import com.farmaciaproyecto.repository.CategoriaRepository;
import com.farmaciaproyecto.repository.ClienteRepository;
import com.farmaciaproyecto.repository.ProductoRepository;
import com.farmaciaproyecto.repository.UserRepository;
import com.farmaciaproyecto.repository.VentaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminServiceImpl implements AdminService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ClienteRepository clienteRepository;
    private final VentaRepository ventaRepository;
    private final UserRepository userRepository;
    private final PlSqlService plSqlService;

    @Override
    public DashboardAdminResponseDTO obtenerDashboard() {
        Long adminId = userRepository.findByRol("ADMIN")
                .map(u -> u.getId())
                .orElse(null);

        long totalProductos = adminId != null
                ? productoRepository.findByUserId(adminId).size() : 0;
        long productosActivos = adminId != null
                ? productoRepository.findByUserId(adminId).stream()
                        .filter(p -> Boolean.TRUE.equals(p.getActivo())).count()
                : 0;

        // Métricas de inventario vía PKG_INVENTARIO
        long stockBajo  = plSqlService.productosBajoMinimo();
        long vencidos   = plSqlService.lotesVencidosCount();

        long sinStock   = adminId != null
                ? productoRepository.findProductosSinStock(adminId).size() : 0;

        LocalDate hoy = LocalDate.now();
        long proxVencer = adminId != null
                ? productoRepository.findProductosProximosAVencer(adminId, hoy, hoy.plusDays(30)).size() : 0;

        long totalCategorias = adminId != null
                ? categoriaRepository.findByUserId(adminId).size() : 0;
        long totalClientes   = clienteRepository.count();
        long totalVentas     = ventaRepository.count();
        long ventasCompletadas = ventaRepository.countByEstadoNombre("COMPLETADA");
        long ventasAnuladas    = ventaRepository.countByEstadoNombre("ANULADA");

        BigDecimal ingresosTotal = ventaRepository.calcularIngresosPorEstado("COMPLETADA");

        // Ventas y ingresos del mes actual vía PKG_VENTAS
        long ventasMesActual    = plSqlService.ventasMesCount(hoy.getMonthValue(), hoy.getYear());
        BigDecimal ingresosMesActual = plSqlService.ingresosMes(hoy.getMonthValue(), hoy.getYear());

        return DashboardAdminResponseDTO.builder()
                .totalProductos(totalProductos)
                .productosActivos(productosActivos)
                .productosStockBajo(stockBajo)
                .productosSinStock(sinStock)
                .totalCategorias(totalCategorias)
                .totalClientes(totalClientes)
                .totalVentas(totalVentas)
                .ventasCompletadas(ventasCompletadas)
                .ventasAnuladas(ventasAnuladas)
                .ingresosTotal(ingresosTotal)
                .ventasMesActual(ventasMesActual)
                .ingresosMesActual(ingresosMesActual)
                .productosVencidos(vencidos)
                .productosProximosVencer(proxVencer)
                .build();
    }
}
