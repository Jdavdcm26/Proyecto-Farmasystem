package com.farmaciaproyecto.util;

import jakarta.servlet.http.HttpSession;

/**
 * Pure Fabrication (GRASP): centraliza la lectura de atributos de sesión HTTP
 * para evitar duplicación en todos los controladores.
 */
public final class SessionUtils {

    private SessionUtils() {}

    public static boolean esAdmin(HttpSession session) {
        return "ADMIN".equals(session.getAttribute("rol"));
    }

    public static boolean esCliente(HttpSession session) {
        return "CLIENT".equals(session.getAttribute("rol"));
    }

    public static boolean estaAutenticado(HttpSession session) {
        return session.getAttribute("userId") != null;
    }

    public static Long getUserId(HttpSession session) {
        return (Long) session.getAttribute("userId");
    }
}
