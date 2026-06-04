package com.farmaciaproyecto.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.farmaciaproyecto.dto.request.ClienteRequestDTO;
import com.farmaciaproyecto.dto.response.ClienteResponseDTO;
import com.farmaciaproyecto.service.ClienteService;
import com.farmaciaproyecto.util.SessionUtils;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    // ── Cliente: ver su propio perfil ────────────────────────────────────────
    @GetMapping("/mi-perfil")
    public ResponseEntity<?> miPerfil(HttpSession session) {
        if (!SessionUtils.esCliente(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Acceso denegado"));
        }
        String cedula = (String) session.getAttribute("cedula");
        try {
            ClienteResponseDTO response = clienteService.obtenerClientePorCedula(cedula);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // ── Cliente: actualizar su propio perfil ─────────────────────────────────
    @PutMapping("/mi-perfil")
    public ResponseEntity<?> actualizarMiPerfil(
            @Validated @RequestBody ClienteRequestDTO request,
            HttpSession session) {

        if (!SessionUtils.esCliente(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Acceso denegado"));
        }
        String cedula = (String) session.getAttribute("cedula");
        try {
            ClienteResponseDTO response = clienteService.actualizarClientePorCedula(cedula, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── Admin: detalle de un cliente ─────────────────────────────────────────
    @GetMapping("/{cedula}")
    public ResponseEntity<?> obtenerCliente(@PathVariable String cedula, HttpSession session) {
        if (!SessionUtils.esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Acceso denegado"));
        }
        try {
            return ResponseEntity.ok(clienteService.obtenerClientePorCedula(cedula));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

}
