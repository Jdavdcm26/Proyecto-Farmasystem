package com.farmaciaproyecto.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.farmaciaproyecto.model.ConsultaIA;

@Repository
public interface ConsultaIARepository extends JpaRepository<ConsultaIA, Long> {

    List<ConsultaIA> findByUserIdOrderByFechaDesc(Long userId);
}
