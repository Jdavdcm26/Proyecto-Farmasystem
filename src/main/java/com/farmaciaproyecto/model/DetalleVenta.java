package com.farmaciaproyecto.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "DETALLE_VENTAS",
        uniqueConstraints = @UniqueConstraint(name = "PK_ID_DETALLE_DETALLE_VENTAS", columnNames = "id_detalle")
)
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DetalleVenta {

    @Id
    @Column(name="id_detalle")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_detalle_ventas")
    @SequenceGenerator(name = "seq_detalle_ventas", sequenceName = "SEQ_DETALLE_VENTAS", allocationSize = 1)
    private Long id;

    @Column(name = "cantidad_producto", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "subtotal", nullable = false, precision = 14, scale = 2)
    private BigDecimal subtotal;

    // RELACIÓN: El detalle pertenece a una venta
    @ManyToOne
    @JoinColumn(
            name = "venta_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_VENTA_ID_DETALLE_VENTAS")
    )
    private Venta venta;

    // RELACIÓN: El detalle referencia un producto
    @ManyToOne
    @JoinColumn(
            name = "producto_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_PRODUCTO_ID_DETALLE_VENTAS")
    )
    private Producto producto;

    // RELACIÓN: El detalle se vendió de un lote específico (para trazabilidad en la factura)
    @ManyToOne
    @JoinColumn(
            name = "lote_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_LOTE_ID_DETALLE_VENTAS")
    )
    private Lote lote;
}
