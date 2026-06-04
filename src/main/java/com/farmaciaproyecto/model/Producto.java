package com.farmaciaproyecto.model;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
        name = "PRODUCTOS",
        uniqueConstraints = @UniqueConstraint(name = "PK_ID_PRODUCTO_PRODUCTOS", columnNames = "id_producto")
)
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Producto {

    @Id
    @Column(name="id_producto")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_productos")
    @SequenceGenerator(name = "seq_productos", sequenceName = "SEQ_PRODUCTOS", allocationSize = 1)
    private Long id;

    @Column(name = "nombre_producto", nullable = false, length = 35)
    private String nombre;

    @Column(name = "descripcion_producto", length = 50)
    private String descripcion;

    @Column(name = "iva", nullable = false, precision = 5, scale = 2)
    private BigDecimal iva;

    @Column(name = "stock_producto", nullable = false)
    private Integer stock;

    @Column(name = "stock_minimo")
    private Integer stockMinimo;

    @Column(name = "estado_producto", nullable = false)
    private Boolean activo;

    // RELACIÓN: Este producto pertenece a un usuario (inventario privado)
    @ManyToOne
    @JoinColumn(
            name = "usuario_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_USUARIO_ID_PRODUCTOS")
    )
    private User user;

    // RELACIÓN: Este producto pertenece a una categoría
    @ManyToOne
    @JoinColumn(
            name = "categoria_id",
            foreignKey = @ForeignKey(name = "FK_CATEGORIA_ID_PRODUCTOS")
    )
    private Categoria categoria;

    // RELACIÓN: El producto tiene muchos lotes (cada lote con su costo, precio y vencimiento)
    @OneToMany(mappedBy = "producto")
    private List<Lote> lotes;

    // RELACIÓN: Este producto tiene muchos detalles de venta
    @OneToMany(mappedBy = "producto")
    private List<DetalleVenta> detalleVentas;
}
