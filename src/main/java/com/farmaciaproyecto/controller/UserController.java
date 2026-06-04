package com.farmaciaproyecto.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.farmaciaproyecto.dto.request.RegistroClienteRequestDTO;
import com.farmaciaproyecto.dto.request.UserLoginDTO;
import com.farmaciaproyecto.dto.request.UserRequestDTO;
import com.farmaciaproyecto.dto.response.UserResponseDTO;
import com.farmaciaproyecto.service.ClienteService;
import com.farmaciaproyecto.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final ClienteService clienteService;

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@Valid @RequestBody RegistroClienteRequestDTO requestDTO) {
        log.info("POST /api/auth/registro - Usuario: {}", requestDTO.getUsername());
        try {
            UserResponseDTO response = userService.registrar(requestDTO);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDTO loginDTO, HttpSession session) {
        log.info("POST /api/auth/login - Usuario: {}", loginDTO.getUsername());
        try {
            UserResponseDTO response = userService.login(loginDTO);

            session.setAttribute("userId", response.getId());
            session.setAttribute("username", response.getUsername());
            session.setAttribute("nombreCompleto", response.getNombreCompleto());
            session.setAttribute("rol", response.getRol());

            if ("CLIENT".equals(response.getRol())) {
                clienteService.obtenerCedulaPorUserId(response.getId())
                        .ifPresent(cedula -> session.setAttribute("cedula", cedula));
            }

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/verificar-sesion")
    public ResponseEntity<?> verificarSesion(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("autenticado", false));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("autenticado", true);
        response.put("userId", userId);
        response.put("username", session.getAttribute("username"));
        response.put("nombreCompleto", session.getAttribute("nombreCompleto"));
        response.put("rol", session.getAttribute("rol"));
        response.put("cedula", session.getAttribute("cedula"));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("mensaje", "Sesión cerrada exitosamente"));
    }

    @GetMapping("/mi-perfil")
    public ResponseEntity<?> obtenerMiPerfil(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No hay sesión activa"));
        }
        try {
            return ResponseEntity.ok(userService.obtenerMiPerfil(userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/mi-perfil")
    public ResponseEntity<?> actualizarMiPerfil(@Valid @RequestBody UserRequestDTO requestDTO, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No hay sesión activa"));
        }
        try {
            UserResponseDTO response = userService.actualizarMiPerfil(userId, requestDTO);
            session.setAttribute("username", response.getUsername());
            session.setAttribute("nombreCompleto", response.getNombreCompleto());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/mi-cuenta")
    public ResponseEntity<?> eliminarMiCuenta(
            @RequestBody(required = false) Map<String, String> confirmacion,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No hay sesión activa"));
        }
        if (confirmacion == null || !"CONFIRMAR".equals(confirmacion.get("confirmacion"))) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Debes confirmar enviando {\"confirmacion\": \"CONFIRMAR\"}"));
        }
        try {
            userService.eliminarMiCuenta(userId);
            session.invalidate();
            return ResponseEntity.ok(Map.of("mensaje", "Cuenta eliminada exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
}
