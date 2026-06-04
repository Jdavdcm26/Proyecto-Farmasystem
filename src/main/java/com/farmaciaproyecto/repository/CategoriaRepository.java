package com.farmaciaproyecto.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.farmaciaproyecto.model.Categoria;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
  List<Categoria> findByUserId(Long userId);
  Optional<Categoria> findByIdAndUserId(Long id, Long userId);
  Optional<Categoria> findByNombreAndUserId(String nombre, Long userId);
  List<Categoria> findByActivoTrue();
}
