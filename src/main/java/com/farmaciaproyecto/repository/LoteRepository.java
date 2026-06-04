package com.farmaciaproyecto.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.farmaciaproyecto.model.Lote;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Long> {

    List<Lote> findByProductoId(Long productoId);

    Optional<Lote> findByIdAndProductoUserId(Long id, Long userId);

    List<Lote> findByProductoUserId(Long userId);

    @Query("SELECT l FROM Lote l WHERE l.producto.id = :productoId " +
           "AND l.activo = true AND l.stock > 0 " +
           "ORDER BY l.fechaVencimiento ASC")
    List<Lote> findLotesDisponiblesPorProducto(@Param("productoId") Long productoId);

    @Query("SELECT l FROM Lote l WHERE l.producto.user.id = :userId " +
           "AND l.activo = true AND l.fechaVencimiento < :hoy")
    List<Lote> findLotesVencidos(@Param("userId") Long userId, @Param("hoy") LocalDate hoy);

    @Query("SELECT l FROM Lote l WHERE l.producto.user.id = :userId " +
           "AND l.activo = true " +
           "AND l.fechaVencimiento >= :hoy " +
           "AND l.fechaVencimiento <= :limite")
    List<Lote> findLotesProximosAVencer(
            @Param("userId") Long userId,
            @Param("hoy") LocalDate hoy,
            @Param("limite") LocalDate limite
    );
}
