package com.farmaciaproyecto.service;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Convierte errores ORA-200xx lanzados por los triggers de db_objetos.sql
 * en mensajes amigables para mostrar en la UI.
 *
 * Códigos usados:
 *   20001 – precio de venta <= 0 (trg_validar_lote / PKG_INVENTARIO.prc_actualizar_precio_lote)
 *   20004 – stock negativo      (trg_validar_producto, trg_validar_lote)
 *   20005 – stock mínimo < 0   (trg_validar_producto)
 *   20006 – rol inválido        (trg_validar_rol_usuario)
 *   20007 – cantidad <= 0       (trg_validar_detalle_venta)
 *   20008 – producto no existe  (trg_validar_detalle_venta)
 *   20012 – reactivar anulada   (trg_proteger_venta_anulada)
 *   20013 – subtotal incorrecto (trg_validar_subtotal_detalle)
 *   20014 – IVA negativo        (trg_validar_producto)
 *   20015 – precio costo <= 0  (trg_validar_lote)
 *   20016 – utilidad < 0        (trg_validar_lote)
 *   20017 – fechas incoherentes (trg_validar_lote)
 *   20018 – lote no existe      (trg_validar_detalle_venta)
 *   20019 – lote-producto mismatch (trg_validar_detalle_venta)
 */
public final class OracleErrorTranslator {

    private static final Pattern ORA_CODE =
            Pattern.compile("ORA-(\\d{5}):\\s*([^\\n]+)");

    private static final Map<String, String> MENSAJES = Map.ofEntries(
            Map.entry("20001", "El precio de venta debe ser mayor que cero."),
            Map.entry("20004", "El stock no puede ser negativo."),
            Map.entry("20005", "El stock mínimo no puede ser negativo."),
            Map.entry("20006", "Rol inválido: solo se permiten ADMIN o CLIENT."),
            Map.entry("20007", "La cantidad de la compra debe ser mayor que cero."),
            Map.entry("20008", "El producto solicitado no existe."),
            Map.entry("20012", "No se puede reactivar una venta ya anulada."),
            Map.entry("20013", "El subtotal del detalle no coincide con precio × cantidad."),
            Map.entry("20014", "El IVA no puede ser negativo."),
            Map.entry("20015", "El precio de costo del lote debe ser mayor que cero."),
            Map.entry("20016", "El porcentaje de utilidad no puede ser negativo."),
            Map.entry("20017", "La fecha de vencimiento debe ser posterior a la fecha de ingreso."),
            Map.entry("20018", "El lote indicado no existe."),
            Map.entry("20019", "El lote no corresponde al producto indicado.")
    );

    private OracleErrorTranslator() { }

    public static String translate(Throwable ex) {
        Throwable cur = ex;
        while (cur != null) {
            String msg = cur.getMessage();
            if (msg != null) {
                Matcher m = ORA_CODE.matcher(msg);
                if (m.find()) {
                    String code          = m.group(1);
                    String oracleMessage = m.group(2).trim();
                    String friendly      = MENSAJES.get(code);

                    if (friendly != null) {
                        // Si el trigger incluyó contexto adicional (nombre, ID…) lo adjuntamos.
                        if (oracleMessage.contains(":") || oracleMessage.contains("\"")) {
                            return friendly + " (" + oracleMessage + ")";
                        }
                        return friendly;
                    }
                    return oracleMessage;
                }
            }
            cur = cur.getCause();
        }
        return ex.getMessage() != null ? ex.getMessage() : "Error desconocido";
    }
}
