package com.farmaciaproyecto.model;

import java.math.BigDecimal;
import java.time.LocalDate;

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
        name = "LOTES",
        uniqueConstraints = @UniqueConstraint(name = "PK_ID_LOTE_LOTES", columnNames = "id_lote")
)
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Lote {

    @Id
    @Column(name="id_lote")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_lotes")
    @SequenceGenerator(name = "seq_lotes", sequenceName = "SEQ_LOTES", allocationSize = 1)
    private Long id;

    @Column(name = "numero_lote", nullable = false, length = 30)
    private String numeroLote;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Column(name = "precio_costo", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioCosto;

    @Column(name = "porcentaje_utilidad", nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentajeUtilidad;

    @Column(name = "precio_venta", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioVenta;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Column(name = "estado_lote", nullable = false)
    private Boolean activo;

    @ManyToOne
    @JoinColumn(
            name = "producto_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_PRODUCTO_ID_LOTES")
    )
    private Producto producto;
}
