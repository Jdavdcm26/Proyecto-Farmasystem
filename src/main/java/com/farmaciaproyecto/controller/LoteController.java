package com.farmaciaproyecto.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.farmaciaproyecto.dto.request.LoteRequestDTO;
import com.farmaciaproyecto.service.LoteService;
import com.farmaciaproyecto.util.SessionUtils;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class LoteController {

    private final LoteService loteService;

    @GetMapping("/lotes")
    public ResponseEntity<?> listarTodos(HttpSession session) {
        if (!SessionUtils.esAdmin(session)) return forbidden();
        Long adminId = SessionUtils.getUserId(session);
        return ResponseEntity.ok(loteService.listarTodosLosLotes(adminId));
    }

    @PostMapping("/lotes")
    public ResponseEntity<?> crear(@Valid @RequestBody LoteRequestDTO request, HttpSession session) {
        if (!SessionUtils.esAdmin(session)) return forbidden();
        Long adminId = SessionUtils.getUserId(session);
        try {
            return new ResponseEntity<>(loteService.crearLote(adminId, request), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/productos/{productoId}/lotes")
    public ResponseEntity<?> listarPorProducto(@PathVariable Long productoId, HttpSession session) {
        if (!SessionUtils.esAdmin(session)) return forbidden();
        Long adminId = SessionUtils.getUserId(session);
        try {
            return ResponseEntity.ok(loteService.listarLotesPorProducto(adminId, productoId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/lotes/{loteId}")
    public ResponseEntity<?> obtener(@PathVariable Long loteId, HttpSession session) {
        if (!SessionUtils.esAdmin(session)) return forbidden();
        Long adminId = SessionUtils.getUserId(session);
        try {
            return ResponseEntity.ok(loteService.obtenerLote(adminId, loteId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/lotes/{loteId}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long loteId,
            @Valid @RequestBody LoteRequestDTO request,
            HttpSession session) {
        if (!SessionUtils.esAdmin(session)) return forbidden();
        Long adminId = SessionUtils.getUserId(session);
        try {
            return ResponseEntity.ok(loteService.actualizarLote(adminId, loteId, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/lotes/{loteId}")
    public ResponseEntity<?> eliminar(@PathVariable Long loteId, HttpSession session) {
        if (!SessionUtils.esAdmin(session)) return forbidden();
        Long adminId = SessionUtils.getUserId(session);
        try {
            loteService.eliminarLote(adminId, loteId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/lotes/vencidos")
    public ResponseEntity<?> vencidos(HttpSession session) {
        if (!SessionUtils.esAdmin(session)) return forbidden();
        Long adminId = SessionUtils.getUserId(session);
        return ResponseEntity.ok(loteService.listarLotesVencidos(adminId));
    }

    @GetMapping("/lotes/proximos-vencer")
    public ResponseEntity<?> proximosVencer(HttpSession session) {
        if (!SessionUtils.esAdmin(session)) return forbidden();
        Long adminId = SessionUtils.getUserId(session);
        return ResponseEntity.ok(loteService.listarLotesProximosAVencer(adminId));
    }

    private ResponseEntity<?> forbidden() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Acceso denegado"));
    }
}
