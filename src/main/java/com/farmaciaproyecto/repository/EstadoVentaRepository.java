package com.farmaciaproyecto.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.farmaciaproyecto.model.EstadoVenta;

@Repository
public interface EstadoVentaRepository extends JpaRepository<EstadoVenta, Long> {

    Optional<EstadoVenta> findByNombre(String nombre);

    boolean existsByNombre(String nombre);
}
