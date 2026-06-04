package com.farmaciaproyecto.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.farmaciaproyecto.dto.request.CategoriaRequestDTO;
import com.farmaciaproyecto.service.CategoriaService;
import com.farmaciaproyecto.util.SessionUtils;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    // ── Admin: CRUD de categorías ────────────────────────────────────────────

    @PostMapping("/api/admin/categorias")
    public ResponseEntity<?> crearCategoria(@RequestBody CategoriaRequestDTO request, HttpSession session) {
        if (!SessionUtils.esAdmin(session)) return forbidden();
        Long adminId = SessionUtils.getUserId(session);
        return new ResponseEntity<>(categoriaService.crearCategoria(adminId, request), HttpStatus.CREATED);
    }

    @GetMapping("/api/admin/categorias")
    public ResponseEntity<?> listarCategorias(HttpSession session) {
        if (!SessionUtils.esAdmin(session)) return forbidden();
        Long adminId = SessionUtils.getUserId(session);
        return ResponseEntity.ok(categoriaService.listarCategoriasPorUsuario(adminId));
    }

    @GetMapping("/api/admin/categorias/{categoriaId}")
    public ResponseEntity<?> obtenerCategoria(@PathVariable Long categoriaId, HttpSession session) {
        if (!SessionUtils.esAdmin(session)) return forbidden();
        Long adminId = SessionUtils.getUserId(session);
        return ResponseEntity.ok(categoriaService.obtenerCategoriaPorId(adminId, categoriaId));
    }

    @PutMapping("/api/admin/categorias/{categoriaId}")
    public ResponseEntity<?> actualizarCategoria(
            @PathVariable Long categoriaId,
            @RequestBody CategoriaRequestDTO request,
            HttpSession session) {
        if (!SessionUtils.esAdmin(session)) return forbidden();
        Long adminId = SessionUtils.getUserId(session);
        return ResponseEntity.ok(categoriaService.actualizarCategoria(adminId, categoriaId, request));
    }

    @DeleteMapping("/api/admin/categorias/{categoriaId}")
    public ResponseEntity<?> eliminarCategoria(@PathVariable Long categoriaId, HttpSession session) {
        if (!SessionUtils.esAdmin(session)) return forbidden();
        Long adminId = SessionUtils.getUserId(session);
        categoriaService.eliminarCategoria(adminId, categoriaId);
        return ResponseEntity.noContent().build();
    }

    // ── Público: listado de categorías para el filtro del catálogo ───────────

    @GetMapping("/api/categorias/publicas")
    public ResponseEntity<?> categoriasPublicas(HttpSession session) {
        if (!SessionUtils.estaAutenticado(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Sin sesión"));
        }
        return ResponseEntity.ok(categoriaService.listarCategoriasPublicas());
    }

    private ResponseEntity<?> forbidden() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Acceso denegado"));
    }
}
