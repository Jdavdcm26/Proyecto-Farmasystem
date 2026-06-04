package com.farmaciaproyecto.service;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlSqlServiceImpl implements PlSqlService {

    private final JdbcTemplate jdbc;

    public PlSqlServiceImpl(DataSource dataSource) {
        this.jdbc = new JdbcTemplate(dataSource);
    }

    // =========================================================================
    // PKG_INVENTARIO — funciones
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public String nivelStock(Long productoId) {
        if (productoId == null) return "PRODUCTO NO EXISTE";
        return callScalar(
                "BEGIN ? := PKG_INVENTARIO.fn_nivel_stock(?); END;",
                Types.VARCHAR,
                cs -> cs.setLong(2, productoId),
                cs -> cs.getString(1));
    }

    @Override
    @Transactional(readOnly = true)
    public int stockLote(Long loteId) {
        if (loteId == null) return -1;
        BigDecimal v = callScalar(
                "BEGIN ? := PKG_INVENTARIO.fn_stock_lote(?); END;",
                Types.NUMERIC,
                cs -> cs.setLong(2, loteId),
                cs -> cs.getBigDecimal(1));
        return v == null ? -1 : v.intValue();
    }

    @Override
    @Transactional(readOnly = true)
    public long lotesVencidosCount() {
        BigDecimal v = callScalar(
                "BEGIN ? := PKG_INVENTARIO.fn_lotes_vencidos_count; END;",
                Types.NUMERIC,
                cs -> {},
                cs -> cs.getBigDecimal(1));
        return v == null ? 0L : v.longValue();
    }

    @Override
    @Transactional(readOnly = true)
    public long productosBajoMinimo() {
        BigDecimal v = callScalar(
                "BEGIN ? := PKG_INVENTARIO.fn_productos_bajo_minimo; END;",
                Types.NUMERIC,
                cs -> {},
                cs -> cs.getBigDecimal(1));
        return v == null ? 0L : v.longValue();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal gastosNetos() {
        BigDecimal v = callScalar(
                "BEGIN ? := PKG_INVENTARIO.fn_gastos_netos; END;",
                Types.NUMERIC,
                cs -> {},
                cs -> cs.getBigDecimal(1));
        return v == null ? BigDecimal.ZERO : v;
    }

    // =========================================================================
    // PKG_INVENTARIO — procedimientos
    // =========================================================================

    @Override
    @Transactional
    public List<String> desactivarLotesVencidos() {
        return runWithDbmsOutput(conn -> {
            try (CallableStatement cs = conn.prepareCall(
                    "BEGIN PKG_INVENTARIO.prc_desactivar_lotes_vencidos; END;")) {
                cs.execute();
            }
        });
    }

    @Override
    @Transactional
    public List<String> actualizarPrecioLote(Long loteId, BigDecimal nuevoPrecio) {
        return runWithDbmsOutput(conn -> {
            try (CallableStatement cs = conn.prepareCall(
                    "BEGIN PKG_INVENTARIO.prc_actualizar_precio_lote(?, ?); END;")) {
                cs.setLong(1, loteId);
                cs.setBigDecimal(2, nuevoPrecio);
                cs.execute();
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> productosStockBajo() {
        return runWithDbmsOutput(conn -> {
            try (CallableStatement cs = conn.prepareCall(
                    "BEGIN PKG_INVENTARIO.prc_productos_stock_bajo; END;")) {
                cs.execute();
            }
        });
    }

    // =========================================================================
    // PKG_VENTAS — funciones
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public BigDecimal totalComprasCliente(String cedula) {
        return callScalar(
                "BEGIN ? := PKG_VENTAS.fn_total_compras_cliente(?); END;",
                Types.NUMERIC,
                cs -> cs.setString(2, cedula),
                cs -> cs.getBigDecimal(1));
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal ingresosMes(int mes, int anio) {
        return callScalar(
                "BEGIN ? := PKG_VENTAS.fn_ingresos_mes(?, ?); END;",
                Types.NUMERIC,
                cs -> { cs.setInt(2, mes); cs.setInt(3, anio); },
                cs -> cs.getBigDecimal(1));
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal ingresosAnio(int anio) {
        return callScalar(
                "BEGIN ? := PKG_VENTAS.fn_ingresos_anio(?); END;",
                Types.NUMERIC,
                cs -> cs.setInt(2, anio),
                cs -> cs.getBigDecimal(1));
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal costosMes(int mes, int anio) {
        return callScalar(
                "BEGIN ? := PKG_VENTAS.fn_costos_mes(?, ?); END;",
                Types.NUMERIC,
                cs -> { cs.setInt(2, mes); cs.setInt(3, anio); },
                cs -> cs.getBigDecimal(1));
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal costosAnio(int anio) {
        return callScalar(
                "BEGIN ? := PKG_VENTAS.fn_costos_anio(?); END;",
                Types.NUMERIC,
                cs -> cs.setInt(2, anio),
                cs -> cs.getBigDecimal(1));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean clienteActivo(String cedula) {
        BigDecimal v = callScalar(
                "BEGIN ? := PKG_VENTAS.fn_cliente_activo(?); END;",
                Types.NUMERIC,
                cs -> cs.setString(2, cedula),
                cs -> cs.getBigDecimal(1));
        return v != null && v.intValue() == 1;
    }

    @Override
    @Transactional(readOnly = true)
    public long ventasMesCount(int mes, int anio) {
        BigDecimal v = callScalar(
                "BEGIN ? := PKG_VENTAS.fn_ventas_mes_count(?, ?); END;",
                Types.NUMERIC,
                cs -> { cs.setInt(2, mes); cs.setInt(3, anio); },
                cs -> cs.getBigDecimal(1));
        return v == null ? 0L : v.longValue();
    }

    // =========================================================================
    // PKG_VENTAS — procedimientos
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public List<String> resumenVentasMes(int mes, int anio) {
        return runWithDbmsOutput(conn -> {
            try (CallableStatement cs = conn.prepareCall(
                    "BEGIN PKG_VENTAS.prc_resumen_ventas_mes(?, ?); END;")) {
                cs.setInt(1, mes);
                cs.setInt(2, anio);
                cs.execute();
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> topClientes(int top) {
        return runWithDbmsOutput(conn -> {
            try (CallableStatement cs = conn.prepareCall(
                    "BEGIN PKG_VENTAS.prc_top_clientes(?); END;")) {
                cs.setInt(1, top);
                cs.execute();
            }
        });
    }

    // =========================================================================
    // Helpers internos
    // =========================================================================

    @FunctionalInterface
    private interface CsBinder {
        void bind(CallableStatement cs) throws java.sql.SQLException;
    }

    @FunctionalInterface
    private interface CsReader<T> {
        T read(CallableStatement cs) throws java.sql.SQLException;
    }

    @FunctionalInterface
    private interface ConnAction {
        void run(Connection conn) throws java.sql.SQLException;
    }

    private <T> T callScalar(String sql, int outType, CsBinder binder, CsReader<T> reader) {
        return jdbc.execute((Connection conn) -> {
            try (CallableStatement cs = conn.prepareCall(sql)) {
                cs.registerOutParameter(1, outType);
                binder.bind(cs);
                cs.execute();
                return reader.read(cs);
            }
        });
    }

    /**
     * Activa DBMS_OUTPUT, ejecuta el procedimiento y devuelve las líneas
     * escritas con DBMS_OUTPUT.PUT_LINE, todo en la misma conexión.
     */
    private List<String> runWithDbmsOutput(ConnAction action) {
        return jdbc.execute((Connection conn) -> {
            List<String> lines = new ArrayList<>();

            try (CallableStatement enable = conn.prepareCall(
                    "BEGIN DBMS_OUTPUT.ENABLE(1000000); END;")) {
                enable.execute();
            }

            try {
                action.run(conn);

                try (CallableStatement get = conn.prepareCall(
                        "BEGIN DBMS_OUTPUT.GET_LINE(?, ?); END;")) {
                    get.registerOutParameter(1, Types.VARCHAR);
                    get.registerOutParameter(2, Types.INTEGER);
                    int status = 0;
                    int safety = 0;
                    while (status == 0 && safety < 10_000) {
                        get.execute();
                        status = get.getInt(2);
                        if (status == 0) {
                            String line = get.getString(1);
                            if (line != null) lines.add(line);
                        }
                        safety++;
                    }
                }
            } finally {
                try (CallableStatement disable = conn.prepareCall(
                        "BEGIN DBMS_OUTPUT.DISABLE; END;")) {
                    disable.execute();
                } catch (java.sql.SQLException ignore) { }
            }

            return lines;
        });
    }
}
