package com.farmaciaproyecto.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmaciaproyecto.dto.request.ProductoRequestDTO;
import com.farmaciaproyecto.dto.response.ProductoResponseDTO;
import com.farmaciaproyecto.model.Categoria;
import com.farmaciaproyecto.model.Lote;
import com.farmaciaproyecto.model.Producto;
import com.farmaciaproyecto.model.User;
import com.farmaciaproyecto.repository.CategoriaRepository;
import com.farmaciaproyecto.repository.ProductoRepository;
import com.farmaciaproyecto.repository.UserRepository;

@Service
public class ProductoServiceImp implements ProductoService {

    private final ProductoRepository productoRepository;
    private final UserRepository userRepository;
    private final CategoriaRepository categoriaRepository;
    private final PlSqlService plSqlService;

    public ProductoServiceImp(ProductoRepository productoRepository,
                              UserRepository userRepository,
                              CategoriaRepository categoriaRepository,
                              PlSqlService plSqlService) {
        this.productoRepository  = productoRepository;
        this.userRepository      = userRepository;
        this.categoriaRepository = categoriaRepository;
        this.plSqlService        = plSqlService;
    }

    @Override
    public ProductoResponseDTO crearProducto(Long userId, ProductoRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Categoria categoria = resolverCategoria(request.getCategoriaId());

        Producto producto = Producto.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .iva(request.getIva())
                .stock(request.getStock())
                .stockMinimo(request.getStockMinimo())
                .activo(request.getActivo())
                .user(user)
                .categoria(categoria)
                .build();

        try {
            return toResponseDTO(productoRepository.save(producto));
        } catch (DataAccessException ex) {
            throw new RuntimeException(OracleErrorTranslator.translate(ex));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> obtenerProductosPorUsuario(Long userId) {
        return productoRepository.findByUserId(userId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponseDTO obtenerProductoPorId(Long userId, Long productoId) {
        Producto producto = productoRepository.findByIdAndUserId(productoId, userId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado para el usuario"));
        return toResponseDTO(producto);
    }

    @Override
    public ProductoResponseDTO actualizarProducto(Long userId, Long productoId, ProductoRequestDTO request) {
        Producto producto = productoRepository.findByIdAndUserId(productoId, userId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado para el usuario"));

        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setIva(request.getIva());
        producto.setStock(request.getStock());
        producto.setStockMinimo(request.getStockMinimo());
        producto.setActivo(request.getActivo());
        producto.setCategoria(resolverCategoria(request.getCategoriaId()));

        try {
            return toResponseDTO(productoRepository.save(producto));
        } catch (DataAccessException ex) {
            throw new RuntimeException(OracleErrorTranslator.translate(ex));
        }
    }

    @Override
    public void eliminarProducto(Long userId, Long productoId) {
        Producto producto = productoRepository.findByIdAndUserId(productoId, userId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado para el usuario"));

        try {
            productoRepository.delete(producto);
        } catch (DataAccessException ex) {
            throw new RuntimeException(OracleErrorTranslator.translate(ex));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> buscarProductos(Long userId, String nombre, Long categoriaId,
                                                     Boolean activo, BigDecimal precioMin, BigDecimal precioMax) {
        return productoRepository.buscarProductos(
                        userId, nombre != null ? nombre : "", categoriaId, activo, precioMin, precioMax)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> obtenerProductosStockBajo(Long userId) {
        return productoRepository.findProductosStockBajo(userId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> obtenerProductosSinStock(Long userId) {
        return productoRepository.findProductosSinStock(userId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> obtenerProductosProximosAVencer(Long userId) {
        LocalDate hoy = LocalDate.now();
        return productoRepository.findProductosProximosAVencer(userId, hoy, hoy.plusDays(30))
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> obtenerProductosVencidos(Long userId) {
        return productoRepository.findProductosVencidos(userId, LocalDate.now())
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> buscarProductosPublicos(String nombre, Long categoriaId) {
        return productoRepository.buscarProductosPublicos(nombre, categoriaId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponseDTO obtenerProductoPublicoPorId(Long productoId) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        if (!Boolean.TRUE.equals(producto.getActivo())) {
            throw new RuntimeException("Producto no disponible");
        }
        return toResponseDTO(producto);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Categoria resolverCategoria(Long categoriaId) {
        if (categoriaId == null) return null;
        return categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
    }

    private ProductoResponseDTO toResponseDTO(Producto producto) {
        Lote loteVigente = lotePrincipal(producto);

        return ProductoResponseDTO.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .iva(producto.getIva())
                .stock(producto.getStock())
                .stockMinimo(producto.getStockMinimo())
                .activo(producto.getActivo())
                .userId(producto.getUser().getId())
                .categoriaId(producto.getCategoria() != null ? producto.getCategoria().getId() : null)
                .categoriaNombre(producto.getCategoria() != null ? producto.getCategoria().getNombre() : null)
                .nivelStock(safeNivelStock(producto.getId()))
                .precio(loteVigente != null ? loteVigente.getPrecioVenta() : null)
                .fechaVencimiento(loteVigente != null ? loteVigente.getFechaVencimiento() : null)
                .build();
    }

    /** Lote activo con stock disponible más próximo a vencer (precio mostrado al cliente). */
    private Lote lotePrincipal(Producto producto) {
        if (producto.getLotes() == null) return null;
        return producto.getLotes().stream()
                .filter(l -> Boolean.TRUE.equals(l.getActivo()))
                .filter(l -> l.getStock() != null && l.getStock() > 0)
                .min(Comparator.comparing(Lote::getFechaVencimiento))
                .orElse(null);
    }

    private String safeNivelStock(Long productoId) {
        try {
            return plSqlService.nivelStock(productoId);
        } catch (Exception e) {
            return null;
        }
    }
}
