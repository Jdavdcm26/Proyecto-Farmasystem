package com.farmaciaproyecto.controller;

import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.farmaciaproyecto.dto.request.CompraRequestDTO;
import com.farmaciaproyecto.dto.response.VentaResponseDTO;
import com.farmaciaproyecto.service.VentaService;
import com.farmaciaproyecto.util.SessionUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    // ── Cliente: realizar compra ─────────────────────────────────────────────
    @PostMapping("/api/compras")
    public ResponseEntity<?> realizarCompra(
            @Valid @RequestBody CompraRequestDTO request,
            HttpSession session) {

        if (!SessionUtils.esCliente(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Acceso denegado"));
        }

        String cedula = (String) session.getAttribute("cedula");
        if (cedula == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "No se encontró el perfil de cliente en sesión"));
        }

        try {
            VentaResponseDTO response = ventaService.realizarCompra(cedula, request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── Cliente: historial de mis compras ────────────────────────────────────
    @GetMapping("/api/mis-compras")
    public ResponseEntity<?> misCompras(HttpSession session) {
        if (!SessionUtils.esCliente(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Acceso denegado"));
        }
        String cedula = (String) session.getAttribute("cedula");
        List<VentaResponseDTO> compras = ventaService.listarVentasPorCliente(cedula);
        return ResponseEntity.ok(compras);
    }

    // ── Admin: todas las ventas ──────────────────────────────────────────────
    @GetMapping("/api/admin/ventas")
    public ResponseEntity<?> listarTodasLasVentas(HttpSession session) {
        if (!SessionUtils.esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Acceso denegado"));
        }
        return ResponseEntity.ok(ventaService.listarTodasLasVentas());
    }

    // ── Admin: detalle de una venta ──────────────────────────────────────────
    @GetMapping("/api/admin/ventas/{ventaId}")
    public ResponseEntity<?> obtenerVenta(
            @PathVariable Long ventaId,
            HttpSession session) {

        if (!SessionUtils.esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Acceso denegado"));
        }
        try {
            return ResponseEntity.ok(ventaService.obtenerVentaPorId(ventaId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // ── Admin: cancelar venta ────────────────────────────────────────────────
    @PutMapping("/api/admin/ventas/{ventaId}/cancelar")
    public ResponseEntity<?> cancelarVenta(
            @PathVariable Long ventaId,
            HttpSession session) {

        if (!SessionUtils.esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Acceso denegado"));
        }
        try {
            ventaService.cancelarVenta(ventaId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

}
