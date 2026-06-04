package com.farmaciaproyecto.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.farmaciaproyecto.model.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, String> {

    Optional<Cliente> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    boolean existsByCedula(String cedula);

    boolean existsByEmail(String email);

    Optional<Cliente> findByCedula(String cedula);

    List<Cliente> findByActivoTrue();
}