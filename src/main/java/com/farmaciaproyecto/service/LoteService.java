package com.farmaciaproyecto.service;

import java.util.List;

import com.farmaciaproyecto.dto.request.LoteRequestDTO;
import com.farmaciaproyecto.dto.response.LoteResponseDTO;

public interface LoteService {

    LoteResponseDTO crearLote(Long userId, LoteRequestDTO request);
    List<LoteResponseDTO> listarTodosLosLotes(Long userId);
    List<LoteResponseDTO> listarLotesPorProducto(Long userId, Long productoId);
    LoteResponseDTO obtenerLote(Long userId, Long loteId);
    LoteResponseDTO actualizarLote(Long userId, Long loteId, LoteRequestDTO request);
    void eliminarLote(Long userId, Long loteId);

    List<LoteResponseDTO> listarLotesVencidos(Long userId);
    List<LoteResponseDTO> listarLotesProximosAVencer(Long userId);
}
