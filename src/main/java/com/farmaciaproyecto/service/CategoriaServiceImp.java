package com.farmaciaproyecto.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmaciaproyecto.dto.request.CategoriaRequestDTO;
import com.farmaciaproyecto.dto.response.CategoriaResponseDTO;
import com.farmaciaproyecto.model.Categoria;
import com.farmaciaproyecto.model.User;
import com.farmaciaproyecto.repository.CategoriaRepository;
import com.farmaciaproyecto.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoriaServiceImp implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final UserRepository userRepository;

    // CREAR
    @Override
    public CategoriaResponseDTO crearCategoria(Long userId, CategoriaRequestDTO request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        categoriaRepository.findByNombreAndUserId(request.getNombre(), userId)
                .ifPresent(c -> {
                    throw new RuntimeException("Ya existe una categoría con ese nombre");
                });

        Categoria categoria = new Categoria();
        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());
        categoria.setActivo(request.getActivo());
        categoria.setUser(user);

        return toResponseDTO(categoriaRepository.save(categoria));
    }

    // ✅ LISTAR
    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarCategoriasPorUsuario(Long userId) {
        return categoriaRepository.findByUserId(userId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // ✅ OBTENER POR ID
    @Override
    @Transactional(readOnly = true)
    public CategoriaResponseDTO obtenerCategoriaPorId(Long userId, Long categoriaId) {
        Categoria categoria = categoriaRepository.findByIdAndUserId(categoriaId, userId)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        return toResponseDTO(categoria);
    }

    // ✅ ACTUALIZAR
    @Override
    public CategoriaResponseDTO actualizarCategoria(
            Long userId,
            Long categoriaId,
            CategoriaRequestDTO request) {

        Categoria categoria = categoriaRepository.findByIdAndUserId(categoriaId, userId)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        categoriaRepository.findByNombreAndUserId(request.getNombre(), userId)
                .ifPresent(c -> {
                    if (!c.getId().equals(categoriaId)) {
                        throw new RuntimeException("Ya existe una categoría con ese nombre");
                    }
                });

        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());
        categoria.setActivo(request.getActivo());

        return toResponseDTO(categoriaRepository.save(categoria));
    }

    // ✅ ELIMINAR
    @Override
    public void eliminarCategoria(Long userId, Long categoriaId) {

        Categoria categoria = categoriaRepository.findByIdAndUserId(categoriaId, userId)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        categoriaRepository.delete(categoria);
    }

    // ✅ CATÁLOGO PÚBLICO
    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarCategoriasPublicas() {
        return categoriaRepository.findByActivoTrue()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // 🔄 Mapper
    private CategoriaResponseDTO toResponseDTO(Categoria categoria) {
        return CategoriaResponseDTO.builder()
                .id(categoria.getId())
                .nombre(categoria.getNombre())
                .descripcion(categoria.getDescripcion())
                .activo(categoria.getActivo())
                .userId(categoria.getUser().getId())
                .build();
    }
}