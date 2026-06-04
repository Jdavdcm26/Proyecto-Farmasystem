package com.farmaciaproyecto.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmaciaproyecto.dto.request.LoteRequestDTO;
import com.farmaciaproyecto.dto.response.LoteResponseDTO;
import com.farmaciaproyecto.model.Lote;
import com.farmaciaproyecto.model.Producto;
import com.farmaciaproyecto.repository.LoteRepository;
import com.farmaciaproyecto.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class LoteServiceImpl implements LoteService {

    private static final BigDecimal CIEN = new BigDecimal("100");

    private final LoteRepository loteRepository;
    private final ProductoRepository productoRepository;

    @Override
    public LoteResponseDTO crearLote(Long userId, LoteRequestDTO request) {
        Producto producto = productoRepository.findByIdAndUserId(request.getProductoId(), userId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado para el usuario"));

        Lote lote = Lote.builder()
                .numeroLote(request.getNumeroLote())
                .fechaIngreso(request.getFechaIngreso())
                .fechaVencimiento(request.getFechaVencimiento())
                .precioCosto(request.getPrecioCosto())
                .porcentajeUtilidad(request.getPorcentajeUtilidad())
                .precioVenta(calcularPrecioVenta(request.getPrecioCosto(), request.getPorcentajeUtilidad()))
                .stock(request.getStock())
                .activo(request.getActivo())
                .producto(producto)
                .build();

        try {
            Lote guardado = loteRepository.save(lote);
            ajustarStockProducto(producto, request.getStock());
            return toResponseDTO(guardado);
        } catch (DataAccessException ex) {
            throw new RuntimeException(OracleErrorTranslator.translate(ex));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoteResponseDTO> listarTodosLosLotes(Long userId) {
        return loteRepository.findByProductoUserId(userId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoteResponseDTO> listarLotesPorProducto(Long userId, Long productoId) {
        productoRepository.findByIdAndUserId(productoId, userId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado para el usuario"));
        return loteRepository.findByProductoId(productoId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public LoteResponseDTO obtenerLote(Long userId, Long loteId) {
        Lote lote = loteRepository.findByIdAndProductoUserId(loteId, userId)
                .orElseThrow(() -> new RuntimeException("Lote no encontrado para el usuario"));
        return toResponseDTO(lote);
    }

    @Override
    public LoteResponseDTO actualizarLote(Long userId, Long loteId, LoteRequestDTO request) {
        Lote lote = loteRepository.findByIdAndProductoUserId(loteId, userId)
                .orElseThrow(() -> new RuntimeException("Lote no encontrado para el usuario"));

        Producto producto = lote.getProducto();
        int delta = request.getStock() - lote.getStock();

        lote.setNumeroLote(request.getNumeroLote());
        lote.setFechaIngreso(request.getFechaIngreso());
        lote.setFechaVencimiento(request.getFechaVencimiento());
        lote.setPrecioCosto(request.getPrecioCosto());
        lote.setPorcentajeUtilidad(request.getPorcentajeUtilidad());
        lote.setPrecioVenta(calcularPrecioVenta(request.getPrecioCosto(), request.getPorcentajeUtilidad()));
        lote.setStock(request.getStock());
        lote.setActivo(request.getActivo());

        try {
            ajustarStockProducto(producto, delta);
            return toResponseDTO(loteRepository.save(lote));
        } catch (DataAccessException ex) {
            throw new RuntimeException(OracleErrorTranslator.translate(ex));
        }
    }

    @Override
    public void eliminarLote(Long userId, Long loteId) {
        Lote lote = loteRepository.findByIdAndProductoUserId(loteId, userId)
                .orElseThrow(() -> new RuntimeException("Lote no encontrado para el usuario"));

        try {
            ajustarStockProducto(lote.getProducto(), -lote.getStock());
            loteRepository.delete(lote);
        } catch (DataAccessException ex) {
            throw new RuntimeException(OracleErrorTranslator.translate(ex));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoteResponseDTO> listarLotesVencidos(Long userId) {
        return loteRepository.findLotesVencidos(userId, LocalDate.now())
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoteResponseDTO> listarLotesProximosAVencer(Long userId) {
        LocalDate hoy = LocalDate.now();
        return loteRepository.findLotesProximosAVencer(userId, hoy, hoy.plusDays(30))
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private BigDecimal calcularPrecioVenta(BigDecimal precioCosto, BigDecimal porcentajeUtilidad) {
        BigDecimal factor = BigDecimal.ONE.add(
                porcentajeUtilidad.divide(CIEN, 4, RoundingMode.HALF_UP));
        return precioCosto.multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }

    private void ajustarStockProducto(Producto producto, int delta) {
        int actual = producto.getStock() != null ? producto.getStock() : 0;
        producto.setStock(actual + delta);
        productoRepository.save(producto);
    }

    private LoteResponseDTO toResponseDTO(Lote lote) {
        return LoteResponseDTO.builder()
                .id(lote.getId())
                .numeroLote(lote.getNumeroLote())
                .fechaIngreso(lote.getFechaIngreso())
                .fechaVencimiento(lote.getFechaVencimiento())
                .precioCosto(lote.getPrecioCosto())
                .porcentajeUtilidad(lote.getPorcentajeUtilidad())
                .precioVenta(lote.getPrecioVenta())
                .stock(lote.getStock())
                .activo(lote.getActivo())
                .productoId(lote.getProducto().getId())
                .productoNombre(lote.getProducto().getNombre())
                .build();
    }
}
