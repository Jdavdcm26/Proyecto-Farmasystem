package com.farmaciaproyecto.service;

import java.math.BigDecimal;
import java.util.List;

import com.farmaciaproyecto.dto.request.ProductoRequestDTO;
import com.farmaciaproyecto.dto.response.ProductoResponseDTO;

public interface ProductoService {

    ProductoResponseDTO crearProducto(Long userId, ProductoRequestDTO productoRequestDTO);
    List<ProductoResponseDTO> obtenerProductosPorUsuario(Long userId);
    ProductoResponseDTO obtenerProductoPorId(Long userId, Long productoId);
    ProductoResponseDTO actualizarProducto(Long userId, Long productoId, ProductoRequestDTO productoRequestDTO);
    void eliminarProducto(Long userId, Long productoId);

    // Métodos de búsqueda avanzada (admin)
    List<ProductoResponseDTO> buscarProductos(Long userId, String nombre, Long categoriaId, Boolean activo, BigDecimal precioMin, BigDecimal precioMax);
    List<ProductoResponseDTO> obtenerProductosStockBajo(Long userId);
    List<ProductoResponseDTO> obtenerProductosSinStock(Long userId);
    List<ProductoResponseDTO> obtenerProductosProximosAVencer(Long userId);
    List<ProductoResponseDTO> obtenerProductosVencidos(Long userId);

    // Catálogo público (clientes)
    List<ProductoResponseDTO> buscarProductosPublicos(String nombre, Long categoriaId);
    ProductoResponseDTO obtenerProductoPublicoPorId(Long productoId);
}
