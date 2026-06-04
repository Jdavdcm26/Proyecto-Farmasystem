package com.farmaciaproyecto.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.farmaciaproyecto.dto.response.ReporteResponseDTO;
import com.farmaciaproyecto.repository.VentaRepository;
import com.farmaciaproyecto.service.OracleErrorTranslator;
import com.farmaciaproyecto.service.PlSqlService;
import com.farmaciaproyecto.util.SessionUtils;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

/**
 * Expone los objetos PL/SQL de docs/db_objetos.sql como endpoints REST.
 * Todos los endpoints son exclusivos del rol ADMIN.
 *
 * PKG_INVENTARIO:
 *   GET  /api/admin/reportes/nivel-stock/{productoId}
 *   GET  /api/admin/reportes/stock-lote/{loteId}
 *   GET  /api/admin/reportes/conteos-inventario
 *   POST /api/admin/reportes/desactivar-lotes-vencidos
 *   PUT  /api/admin/reportes/actualizar-precio-lote/{loteId}
 *   GET  /api/admin/reportes/stock-bajo
 *
 * PKG_VENTAS:
 *   GET  /api/admin/reportes/total-compras/{cedula}
 *   GET  /api/admin/reportes/ingresos-mes/{mes}/{anio}
 *   GET  /api/admin/reportes/ingresos-anio/{anio}
 *   GET  /api/admin/reportes/cliente-activo/{cedula}
 *   GET  /api/admin/reportes/ventas-mes-count/{mes}/{anio}
 *   GET  /api/admin/reportes/resumen-ventas/{mes}/{anio}
 *   GET  /api/admin/reportes/top-clientes
 */
@RestController
@RequestMapping("/api/admin/reportes")
@RequiredArgsConstructor
public class ReportesController {

    private final PlSqlService plSqlService;
    private final VentaRepository ventaRepository;

    // =========================================================================
    // PKG_INVENTARIO — funciones
    // =========================================================================

    @GetMapping("/nivel-stock/{productoId}")
    public ResponseEntity<?> nivelStock(@PathVariable Long productoId, HttpSession s) {
        if (!SessionUtils.esAdmin(s)) return forbidden();
        try {
            return ResponseEntity.ok(Map.of(
                    "productoId", productoId,
                    "nivel", plSqlService.nivelStock(productoId)));
        } catch (Exception e) {
            return badRequest(e);
        }
    }

    @GetMapping("/stock-lote/{loteId}")
    public ResponseEntity<?> stockLote(@PathVariable Long loteId, HttpSession s) {
        if (!SessionUtils.esAdmin(s)) return forbidden();
        try {
            return ResponseEntity.ok(Map.of(
                    "loteId", loteId,
                    "stock", plSqlService.stockLote(loteId)));
        } catch (Exception e) {
            return badRequest(e);
        }
    }

    @GetMapping("/conteos-inventario")
    public ResponseEntity<?> conteosInventario(HttpSession s) {
        if (!SessionUtils.esAdmin(s)) return forbidden();
        try {
            return ResponseEntity.ok(Map.of(
                    "productosBajoMinimo", plSqlService.productosBajoMinimo(),
                    "lotesVencidos",       plSqlService.lotesVencidosCount()));
        } catch (Exception e) {
            return badRequest(e);
        }
    }

    @GetMapping("/gastos-netos")
    public ResponseEntity<?> gastosNetos(HttpSession s) {
        if (!SessionUtils.esAdmin(s)) return forbidden();
        try {
            return ResponseEntity.ok(Map.of("gastosNetos", plSqlService.gastosNetos()));
        } catch (Exception e) {
            return badRequest(e);
        }
    }

    // =========================================================================
    // PKG_INVENTARIO — procedimientos
    // =========================================================================

    @PostMapping("/desactivar-lotes-vencidos")
    public ResponseEntity<?> desactivarLotesVencidos(HttpSession s) {
        if (!SessionUtils.esAdmin(s)) return forbidden();
        try {
            return ResponseEntity.ok(ReporteResponseDTO.builder()
                    .titulo("Desactivación de lotes vencidos")
                    .lineas(plSqlService.desactivarLotesVencidos())
                    .build());
        } catch (Exception e) {
            return badRequest(e);
        }
    }

    @PutMapping("/actualizar-precio-lote/{loteId}")
    public ResponseEntity<?> actualizarPrecioLote(
            @PathVariable Long loteId,
            @RequestBody Map<String, BigDecimal> body,
            HttpSession s) {
        if (!SessionUtils.esAdmin(s)) return forbidden();
        BigDecimal nuevoPrecio = body.get("precio");
        if (nuevoPrecio == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Falta el campo 'precio'"));
        }
        try {
            return ResponseEntity.ok(ReporteResponseDTO.builder()
                    .titulo("Actualización de precio del lote " + loteId)
                    .lineas(plSqlService.actualizarPrecioLote(loteId, nuevoPrecio))
                    .build());
        } catch (Exception e) {
            return badRequest(e);
        }
    }

    @GetMapping("/stock-bajo")
    public ResponseEntity<?> stockBajo(HttpSession s) {
        if (!SessionUtils.esAdmin(s)) return forbidden();
        try {
            return ResponseEntity.ok(ReporteResponseDTO.builder()
                    .titulo("Productos con stock bajo el mínimo")
                    .lineas(plSqlService.productosStockBajo())
                    .build());
        } catch (Exception e) {
            return badRequest(e);
        }
    }

    // =========================================================================
    // PKG_VENTAS — funciones
    // =========================================================================

    @GetMapping("/total-compras/{cedula}")
    public ResponseEntity<?> totalComprasCliente(@PathVariable String cedula, HttpSession s) {
        if (!SessionUtils.esAdmin(s)) return forbidden();
        try {
            return ResponseEntity.ok(Map.of(
                    "cedula", cedula,
                    "total",  plSqlService.totalComprasCliente(cedula)));
        } catch (Exception e) {
            return badRequest(e);
        }
    }

    @GetMapping("/ingresos-mes/{mes}/{anio}")
    public ResponseEntity<?> ingresosMes(
            @PathVariable int mes, @PathVariable int anio, HttpSession s) {
        if (!SessionUtils.esAdmin(s)) return forbidden();
        try {
            return ResponseEntity.ok(Map.of(
                    "mes",      mes,
                    "anio",     anio,
                    "ingresos", plSqlService.ingresosMes(mes, anio)));
        } catch (Exception e) {
            return badRequest(e);
        }
    }

    @GetMapping("/ingresos-anio/{anio}")
    public ResponseEntity<?> ingresosAnio(@PathVariable int anio, HttpSession s) {
        if (!SessionUtils.esAdmin(s)) return forbidden();
        try {
            return ResponseEntity.ok(Map.of(
                    "anio",     anio,
                    "ingresos", plSqlService.ingresosAnio(anio)));
        } catch (Exception e) {
            return badRequest(e);
        }
    }

    @GetMapping("/costos-mes/{mes}/{anio}")
    public ResponseEntity<?> costosMes(
            @PathVariable int mes, @PathVariable int anio, HttpSession s) {
        if (!SessionUtils.esAdmin(s)) return forbidden();
        try {
            return ResponseEntity.ok(Map.of(
                    "mes",    mes,
                    "anio",   anio,
                    "costos", plSqlService.costosMes(mes, anio)));
        } catch (Exception e) {
            return badRequest(e);
        }
    }

    @GetMapping("/costos-anio/{anio}")
    public ResponseEntity<?> costosAnio(@PathVariable int anio, HttpSession s) {
        if (!SessionUtils.esAdmin(s)) return forbidden();
        try {
            BigDecimal ingresos = plSqlService.ingresosAnio(anio);
            BigDecimal costos   = plSqlService.costosAnio(anio);
            BigDecimal ganancia = ingresos.subtract(costos);
            return ResponseEntity.ok(Map.of(
                    "anio",     anio,
                    "ingresos", ingresos,
                    "costos",   costos,
                    "ganancia", ganancia));
        } catch (Exception e) {
            return badRequest(e);
        }
    }

    @GetMapping("/cliente-activo/{cedula}")
    public ResponseEntity<?> clienteActivo(@PathVariable String cedula, HttpSession s) {
        if (!SessionUtils.esAdmin(s)) return forbidden();
        try {
            return ResponseEntity.ok(Map.of(
                    "cedula",  cedula,
                    "activo",  plSqlService.clienteActivo(cedula)));
        } catch (Exception e) {
            return badRequest(e);
        }
    }

    @GetMapping("/ventas-mes-count/{mes}/{anio}")
    public ResponseEntity<?> ventasMesCount(
            @PathVariable int mes, @PathVariable int anio, HttpSession s) {
        if (!SessionUtils.esAdmin(s)) return forbidden();
        try {
            return ResponseEntity.ok(Map.of(
                    "mes",   mes,
                    "anio",  anio,
                    "total", plSqlService.ventasMesCount(mes, anio)));
        } catch (Exception e) {
            return badRequest(e);
        }
    }

    // =========================================================================
    // PKG_VENTAS — procedimientos
    // =========================================================================

    @GetMapping("/resumen-ventas/{mes}/{anio}")
    public ResponseEntity<?> resumenVentas(
            @PathVariable int mes, @PathVariable int anio, HttpSession s) {
        if (!SessionUtils.esAdmin(s)) return forbidden();
        try {
            return ResponseEntity.ok(ReporteResponseDTO.builder()
                    .titulo("Resumen de ventas " + String.format("%02d", mes) + "/" + anio)
                    .lineas(plSqlService.resumenVentasMes(mes, anio))
                    .build());
        } catch (Exception e) {
            return badRequest(e);
        }
    }

    @GetMapping("/top-clientes")
    public ResponseEntity<?> topClientes(
            @RequestParam(defaultValue = "5") int top, HttpSession s) {
        if (!SessionUtils.esAdmin(s)) return forbidden();
        try {
            return ResponseEntity.ok(ReporteResponseDTO.builder()
                    .titulo("Top " + top + " clientes")
                    .lineas(plSqlService.topClientes(top))
                    .build());
        } catch (Exception e) {
            return badRequest(e);
        }
    }

    /** Top clientes en formato JSON estructurado (para gráficas). */
    @GetMapping("/top-clientes-datos")
    public ResponseEntity<?> topClientesDatos(
            @RequestParam(defaultValue = "5") int top, HttpSession s) {
        if (!SessionUtils.esAdmin(s)) return forbidden();
        try {
            List<Map<String, Object>> data = ventaRepository
                    .findTopClientesPorGasto(PageRequest.of(0, Math.max(1, Math.min(top, 50))))
                    .stream()
                    .map(row -> {
                        Map<String, Object> m = new java.util.LinkedHashMap<>();
                        m.put("cedula",        row[0]);
                        m.put("nombre",        row[1]);
                        m.put("apellido",      row[2]);
                        m.put("nombreCompleto", String.valueOf(row[1]) + " " + String.valueOf(row[2]));
                        m.put("numCompras",    row[3]);
                        m.put("totalGastado",  row[4]);
                        return m;
                    })
                    .toList();
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return badRequest(e);
        }
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    /** Atajo para generar la URL del resumen del mes actual. */
    @GetMapping("/resumen-mes-actual")
    public ResponseEntity<?> resumenMesActual(HttpSession s) {
        if (!SessionUtils.esAdmin(s)) return forbidden();
        LocalDate hoy = LocalDate.now();
        try {
            return ResponseEntity.ok(ReporteResponseDTO.builder()
                    .titulo("Resumen de ventas " + String.format("%02d", hoy.getMonthValue()) + "/" + hoy.getYear())
                    .lineas(plSqlService.resumenVentasMes(hoy.getMonthValue(), hoy.getYear()))
                    .build());
        } catch (Exception e) {
            return badRequest(e);
        }
    }

    private ResponseEntity<?> forbidden() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Acceso denegado"));
    }

    private ResponseEntity<?> badRequest(Exception e) {
        return ResponseEntity.badRequest()
                .body(Map.of("error", OracleErrorTranslator.translate(e)));
    }
}
