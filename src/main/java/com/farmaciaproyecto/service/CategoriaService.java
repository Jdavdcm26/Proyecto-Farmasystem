package com.farmaciaproyecto.service;

import java.util.List;

import com.farmaciaproyecto.dto.request.CategoriaRequestDTO;
import com.farmaciaproyecto.dto.response.CategoriaResponseDTO;

public interface CategoriaService {

    CategoriaResponseDTO crearCategoria(Long userId, CategoriaRequestDTO request);

    List<CategoriaResponseDTO> listarCategoriasPorUsuario(Long userId);

    CategoriaResponseDTO obtenerCategoriaPorId(Long userId, Long categoriaId);

    CategoriaResponseDTO actualizarCategoria(
            Long userId,
            Long categoriaId,
            CategoriaRequestDTO request
    );

    void eliminarCategoria(Long userId, Long categoriaId);

    List<CategoriaResponseDTO> listarCategoriasPublicas();
}