package com.farmaciaproyecto.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.farmaciaproyecto.model.Venta;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    List<Venta> findByUserId(Long userId);

    Optional<Venta> findByIdAndUserId(Long id, Long userId);

    boolean existsByNumeroVenta(String numeroVenta);

    long countByEstadoNombre(String nombre);

    List<Venta> findByClienteCedula(String cedula);

    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v WHERE v.estado.nombre = :nombre")
    BigDecimal calcularIngresosPorEstado(@Param("nombre") String nombre);

    @Query("SELECT v FROM Venta v WHERE EXTRACT(YEAR FROM v.fechaVenta) = EXTRACT(YEAR FROM CURRENT_TIMESTAMP) " +
           "AND EXTRACT(MONTH FROM v.fechaVenta) = EXTRACT(MONTH FROM CURRENT_TIMESTAMP)")
    List<Venta> findVentasMesActual();

    // Top clientes por monto gastado en ventas COMPLETADAS.
    // Devuelve filas [cedula, nombre, apellido, numCompras, totalGastado].
    @Query("SELECT v.cliente.cedula, v.cliente.nombre, v.cliente.apellido, " +
           "       COUNT(v), COALESCE(SUM(v.total), 0) " +
           "FROM Venta v " +
           "WHERE v.estado.nombre = 'COMPLETADA' " +
           "GROUP BY v.cliente.cedula, v.cliente.nombre, v.cliente.apellido " +
           "ORDER BY COALESCE(SUM(v.total), 0) DESC")
    List<Object[]> findTopClientesPorGasto(Pageable pageable);
}
