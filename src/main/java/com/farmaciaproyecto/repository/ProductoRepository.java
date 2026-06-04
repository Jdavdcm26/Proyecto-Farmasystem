package com.farmaciaproyecto.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.farmaciaproyecto.model.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // ── Admin: gestión del catálogo ──────────────────────────────────────────
    List<Producto> findByUserId(Long userId);
    Optional<Producto> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT DISTINCT p FROM Producto p WHERE p.user.id = :userId " +
           "AND LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) " +
           "AND (:categoriaId IS NULL OR p.categoria.id = :categoriaId) " +
           "AND (:activo IS NULL OR p.activo = :activo) " +
           "AND (:precioMin IS NULL OR EXISTS (SELECT 1 FROM Lote l WHERE l.producto = p AND l.precioVenta >= :precioMin)) " +
           "AND (:precioMax IS NULL OR EXISTS (SELECT 1 FROM Lote l WHERE l.producto = p AND l.precioVenta <= :precioMax))")
    List<Producto> buscarProductos(
            @Param("userId") Long userId,
            @Param("nombre") String nombre,
            @Param("categoriaId") Long categoriaId,
            @Param("activo") Boolean activo,
            @Param("precioMin") BigDecimal precioMin,
            @Param("precioMax") BigDecimal precioMax
    );

    @Query("SELECT p FROM Producto p WHERE p.user.id = :userId " +
           "AND p.activo = true " +
           "AND p.stockMinimo IS NOT NULL " +
           "AND p.stock <= p.stockMinimo " +
           "AND p.stock > 0")
    List<Producto> findProductosStockBajo(@Param("userId") Long userId);

    @Query("SELECT p FROM Producto p WHERE p.user.id = :userId AND p.activo = true AND p.stock = 0")
    List<Producto> findProductosSinStock(@Param("userId") Long userId);

    @Query("SELECT DISTINCT p FROM Producto p JOIN p.lotes l " +
           "WHERE p.user.id = :userId AND p.activo = true " +
           "AND l.activo = true " +
           "AND l.fechaVencimiento >= :hoy " +
           "AND l.fechaVencimiento <= :proximoMes")
    List<Producto> findProductosProximosAVencer(
            @Param("userId") Long userId,
            @Param("hoy") LocalDate hoy,
            @Param("proximoMes") LocalDate proximoMes
    );

    @Query("SELECT DISTINCT p FROM Producto p JOIN p.lotes l " +
           "WHERE p.user.id = :userId " +
           "AND l.activo = true " +
           "AND l.fechaVencimiento < :hoy")
    List<Producto> findProductosVencidos(
            @Param("userId") Long userId,
            @Param("hoy") LocalDate hoy
    );

    // ── Catálogo público (clientes) ──────────────────────────────────────────
    List<Producto> findByActivoTrue();

    @Query("SELECT p FROM Producto p WHERE p.activo = true " +
           "AND (:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
           "AND (:categoriaId IS NULL OR p.categoria.id = :categoriaId)")
    List<Producto> buscarProductosPublicos(
            @Param("nombre") String nombre,
            @Param("categoriaId") Long categoriaId
    );
}
