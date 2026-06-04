package com.farmaciaproyecto.service;

import java.util.List;

import com.farmaciaproyecto.dto.response.ConsultaIAResponseDTO;
import com.farmaciaproyecto.dto.response.ProductoResponseDTO;

public interface ChatIAService {

    String consultar(String sintomas, List<ProductoResponseDTO> productos);

    void guardarConsulta(Long userId, String sintomas, String respuesta);

    List<ConsultaIAResponseDTO> obtenerHistorial(Long userId);

    void eliminarConsulta(Long id, Long userId);
}
