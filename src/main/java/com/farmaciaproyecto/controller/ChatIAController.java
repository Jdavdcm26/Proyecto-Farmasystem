package com.farmaciaproyecto.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.farmaciaproyecto.dto.request.ChatMensajeRequestDTO;
import com.farmaciaproyecto.dto.response.ConsultaIAResponseDTO;
import com.farmaciaproyecto.dto.response.ProductoResponseDTO;
import com.farmaciaproyecto.service.ChatIAService;
import com.farmaciaproyecto.service.ProductoService;
import com.farmaciaproyecto.util.SessionUtils;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatIAController {

    private final ChatIAService chatIAService;
    private final ProductoService productoService;

    @PostMapping("/api/consulta-ia")
    public ResponseEntity<?> consultar(@Valid @RequestBody ChatMensajeRequestDTO request,
                                       HttpSession session) {
        if (!SessionUtils.esCliente(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Debes iniciar sesión como cliente"));
        }

        Long userId = SessionUtils.getUserId(session);
        try {
            List<ProductoResponseDTO> productos = productoService.buscarProductosPublicos(null, null);
            String respuesta = chatIAService.consultar(request.getMensaje(), productos);
            chatIAService.guardarConsulta(userId, request.getMensaje(), respuesta);
            return ResponseEntity.ok(Map.of("respuesta", respuesta));
        } catch (RuntimeException e) {
            log.error("Error en consulta IA: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/api/consulta-ia/historial")
    public ResponseEntity<?> historial(HttpSession session) {
        if (!SessionUtils.esCliente(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Debes iniciar sesión como cliente"));
        }
        List<ConsultaIAResponseDTO> historial = chatIAService.obtenerHistorial(SessionUtils.getUserId(session));
        return ResponseEntity.ok(historial);
    }

    @DeleteMapping("/api/consulta-ia/historial/{id}")
    public ResponseEntity<?> eliminarConsulta(@PathVariable Long id, HttpSession session) {
        if (!SessionUtils.esCliente(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Debes iniciar sesión como cliente"));
        }
        try {
            chatIAService.eliminarConsulta(id, SessionUtils.getUserId(session));
            return ResponseEntity.ok(Map.of("mensaje", "Consulta eliminada"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}
