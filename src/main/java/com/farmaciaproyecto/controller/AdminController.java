package com.farmaciaproyecto.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.farmaciaproyecto.dto.response.ClienteResponseDTO;
import com.farmaciaproyecto.dto.response.DashboardAdminResponseDTO;
import com.farmaciaproyecto.service.AdminService;
import com.farmaciaproyecto.service.ClienteService;
import com.farmaciaproyecto.util.SessionUtils;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final ClienteService clienteService;

    @GetMapping("/dashboard")
    public ResponseEntity<?> obtenerDashboard(HttpSession session) {
        if (!SessionUtils.esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Acceso denegado"));
        }
        DashboardAdminResponseDTO dashboard = adminService.obtenerDashboard();
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/clientes")
    public ResponseEntity<?> listarClientes(HttpSession session) {
        if (!SessionUtils.esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Acceso denegado"));
        }
        List<ClienteResponseDTO> clientes = clienteService.listarTodosLosClientes();
        return ResponseEntity.ok(clientes);
    }
}
