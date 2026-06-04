package com.farmaciaproyecto.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.farmaciaproyecto.dto.request.ProductoRequestDTO;
import com.farmaciaproyecto.dto.response.ProductoResponseDTO;
import com.farmaciaproyecto.service.ProductoService;
import com.farmaciaproyecto.util.SessionUtils;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    // ── Admin: CRUD del catálogo ─────────────────────────────────────────────

    @PostMapping("/api/admin/productos")
    public ResponseEntity<?> crearProducto(@RequestBody ProductoRequestDTO request, HttpSession session) {
        if (!SessionUtils.esAdmin(session)) return forbidden();
        Long adminId = SessionUtils.getUserId(session);
        return new ResponseEntity<>(productoService.crearProducto(adminId, request), HttpStatus.CREATED);
    }

    @GetMapping("/api/admin/productos")
    public ResponseEntity<?> listarProductos(HttpSession session) {
        if (!SessionUtils.esAdmin(session)) return forbidden();
        Long adminId = SessionUtils.getUserId(session);
        return ResponseEntity.ok(productoService.obtenerProductosPorUsuario(adminId));
    }

    @GetMapping("/api/admin/productos/{productoId}")
    public ResponseEntity<?> obtenerProducto(@PathVariable Long productoId, HttpSession session) {
        if (!SessionUtils.esAdmin(session)) return forbidden();
        Long adminId = SessionUtils.getUserId(session);
        return ResponseEntity.ok(productoService.obtenerProductoPorId(adminId, productoId));
    }

    @PutMapping("/api/admin/productos/{productoId}")
    public ResponseEntity<?> actualizarProducto(
            @PathVariable Long productoId,
            @RequestBody ProductoRequestDTO request,
            HttpSession session) {
        if (!SessionUtils.esAdmin(session)) return forbidden();
        Long adminId = SessionUtils.getUserId(session);
        return ResponseEntity.ok(productoService.actualizarProducto(adminId, productoId, request));
    }

    @DeleteMapping("/api/admin/productos/{productoId}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long productoId, HttpSession session) {
        if (!SessionUtils.esAdmin(session)) return forbidden();
        Long adminId = SessionUtils.getUserId(session);
        productoService.eliminarProducto(adminId, productoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/admin/productos/buscar")
    public ResponseEntity<?> buscarProductos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) Boolean activo,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax,
            HttpSession session) {
        if (!SessionUtils.esAdmin(session)) return forbidden();
        Long adminId = SessionUtils.getUserId(session);
        return ResponseEntity.ok(productoService.buscarProductos(adminId, nombre, categoriaId, activo, precioMin, precioMax));
    }

    @GetMapping("/api/admin/productos/stock-bajo")
    public ResponseEntity<?> stockBajo(HttpSession session) {
        if (!SessionUtils.esAdmin(session)) return forbidden();
        Long adminId = SessionUtils.getUserId(session);
        return ResponseEntity.ok(productoService.obtenerProductosStockBajo(adminId));
    }

    @GetMapping("/api/admin/productos/sin-stock")
    public ResponseEntity<?> sinStock(HttpSession session) {
        if (!SessionUtils.esAdmin(session)) return forbidden();
        Long adminId = SessionUtils.getUserId(session);
        return ResponseEntity.ok(productoService.obtenerProductosSinStock(adminId));
    }

    @GetMapping("/api/admin/productos/proximos-vencer")
    public ResponseEntity<?> proximosVencer(HttpSession session) {
        if (!SessionUtils.esAdmin(session)) return forbidden();
        Long adminId = SessionUtils.getUserId(session);
        return ResponseEntity.ok(productoService.obtenerProductosProximosAVencer(adminId));
    }

    @GetMapping("/api/admin/productos/vencidos")
    public ResponseEntity<?> vencidos(HttpSession session) {
        if (!SessionUtils.esAdmin(session)) return forbidden();
        Long adminId = SessionUtils.getUserId(session);
        return ResponseEntity.ok(productoService.obtenerProductosVencidos(adminId));
    }

    // ── Catálogo público (clientes autenticados) ─────────────────────────────

    @GetMapping("/api/catalogo")
    public ResponseEntity<?> catalogo(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Long categoriaId,
            HttpSession session) {
        if (!SessionUtils.estaAutenticado(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Sin sesión"));
        }
        List<ProductoResponseDTO> productos = productoService.buscarProductosPublicos(nombre, categoriaId);
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/api/catalogo/{productoId}")
    public ResponseEntity<?> detalleCatalogo(@PathVariable Long productoId, HttpSession session) {
        if (!SessionUtils.estaAutenticado(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Sin sesión"));
        }
        try {
            return ResponseEntity.ok(productoService.obtenerProductoPublicoPorId(productoId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    private ResponseEntity<?> forbidden() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Acceso denegado"));
    }
}
