package com.farmaciaproyecto.service;

import java.math.BigDecimal;
import java.util.List;

public interface PlSqlService {

    // ── PKG_INVENTARIO — funciones ────────────────────────────────────────────

    String nivelStock(Long productoId);

    int stockLote(Long loteId);

    long lotesVencidosCount();

    long productosBajoMinimo();

    BigDecimal gastosNetos();

    // ── PKG_INVENTARIO — procedimientos ──────────────────────────────────────

    List<String> desactivarLotesVencidos();

    List<String> actualizarPrecioLote(Long loteId, BigDecimal nuevoPrecio);

    List<String> productosStockBajo();

    // ── PKG_VENTAS — funciones ────────────────────────────────────────────────

    BigDecimal totalComprasCliente(String cedula);

    BigDecimal ingresosMes(int mes, int anio);

    BigDecimal ingresosAnio(int anio);

    BigDecimal costosMes(int mes, int anio);

    BigDecimal costosAnio(int anio);

    boolean clienteActivo(String cedula);

    long ventasMesCount(int mes, int anio);

    // ── PKG_VENTAS — procedimientos ───────────────────────────────────────────

    List<String> resumenVentasMes(int mes, int anio);

    List<String> topClientes(int top);
}
