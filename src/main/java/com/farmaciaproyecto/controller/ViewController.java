package com.farmaciaproyecto.controller;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@Slf4j
public class ViewController {

    // ─── Auth ──────────────────────────────────────────────────────────────

    @GetMapping("/")
    public String index(HttpSession session) {
        String rol = (String) session.getAttribute("rol");
        if ("ADMIN".equals(rol)) return "redirect:/admin/inicio";
        if ("CLIENT".equals(rol)) return "redirect:/tienda";
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        if (session.getAttribute("userId") != null) return "redirect:/";
        return "login";
    }

    @GetMapping("/registro")
    public String registroPage(HttpSession session) {
        if (session.getAttribute("userId") != null) return "redirect:/";
        return "registro";
    }

    // ─── Admin ─────────────────────────────────────────────────────────────

    @GetMapping("/admin/inicio")
    public String adminInicio(HttpSession session) {
        if (!esAdmin(session)) return "redirect:/login";
        return "admin/inicio";
    }

    @GetMapping("/admin/productos")
    public String adminProductos(HttpSession session) {
        if (!esAdmin(session)) return "redirect:/login";
        return "admin/productos";
    }

    @GetMapping("/admin/productos/nuevo")
    public String adminCrearProducto(HttpSession session) {
        if (!esAdmin(session)) return "redirect:/login";
        return "admin/crear-producto";
    }

    @GetMapping("/admin/productos/{productoId}/editar")
    public String adminEditarProducto(@PathVariable Long productoId, HttpSession session) {
        if (!esAdmin(session)) return "redirect:/login";
        return "admin/editar-producto";
    }

    @GetMapping("/admin/productos/{productoId}/lotes")
    public String adminLotes(@PathVariable Long productoId, HttpSession session) {
        if (!esAdmin(session)) return "redirect:/login";
        return "admin/lotes";
    }

    @GetMapping("/admin/lotes")
    public String adminLotesGlobal(HttpSession session) {
        if (!esAdmin(session)) return "redirect:/login";
        return "admin/lotes-todos";
    }

    @GetMapping("/admin/categorias")
    public String adminCategorias(HttpSession session) {
        if (!esAdmin(session)) return "redirect:/login";
        return "admin/categorias";
    }

    @GetMapping("/admin/clientes")
    public String adminClientes(HttpSession session) {
        if (!esAdmin(session)) return "redirect:/login";
        return "admin/clientes";
    }

    @GetMapping("/admin/ventas")
    public String adminVentas(HttpSession session) {
        if (!esAdmin(session)) return "redirect:/login";
        return "admin/ventas";
    }

    @GetMapping("/admin/reportes")
    public String adminReportes(HttpSession session) {
        if (!esAdmin(session)) return "redirect:/login";
        return "admin/reportes";
    }

    @GetMapping("/admin/perfil")
    public String adminPerfil(HttpSession session) {
        if (!esAdmin(session)) return "redirect:/login";
        return "admin/perfil";
    }

    @GetMapping("/admin/editar-perfil")
    public String adminEditarPerfil(HttpSession session) {
        if (!esAdmin(session)) return "redirect:/login";
        return "admin/editar-perfil";
    }

    // ─── Cliente ───────────────────────────────────────────────────────────

    @GetMapping("/tienda")
    public String tienda(HttpSession session) {
        if (!esCliente(session)) return "redirect:/login";
        return "cliente/tienda";
    }

    @GetMapping("/mis-compras")
    public String misCompras(HttpSession session) {
        if (!esCliente(session)) return "redirect:/login";
        return "cliente/mis-compras";
    }

    @GetMapping("/mi-perfil")
    public String miPerfil(HttpSession session) {
        if (!esCliente(session)) return "redirect:/login";
        return "cliente/mi-perfil";
    }

    @GetMapping("/consulta-ia")
    public String consultaIA(HttpSession session) {
        if (!esCliente(session)) return "redirect:/login";
        return "cliente/consulta-ia";
    }

    // ─── Helpers ───────────────────────────────────────────────────────────

    private boolean esAdmin(HttpSession session) {
        return "ADMIN".equals(session.getAttribute("rol"));
    }

    private boolean esCliente(HttpSession session) {
        return "CLIENT".equals(session.getAttribute("rol"));
    }
}
