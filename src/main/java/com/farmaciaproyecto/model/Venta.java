package com.farmaciaproyecto.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
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
        name = "VENTAS",
        uniqueConstraints = @UniqueConstraint(name = "PK_ID_VENTA_VENTAS", columnNames = "id_venta")
)
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Venta {

   @Id
   @Column(name="id_venta")
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_ventas")
   @SequenceGenerator(name = "seq_ventas", sequenceName = "SEQ_VENTAS", allocationSize = 1)
   private Long id;

   @Column(name = "numero_venta", nullable = false, unique = true, length = 20)
   private String numeroVenta;

   @Column(name = "fecha_venta", nullable = false)
   private LocalDateTime fechaVenta;

   // Suma bruta de los detalles (precio unitario x cantidad), antes de descuento e IVA.
   @Column(name = "parcial_venta", nullable = false, precision = 14, scale = 2)
   private BigDecimal parcial;

   // Porcentaje de descuento aplicado a la venta (ej. 10.00 = 10%).
   @Column(name = "descuento", nullable = false, precision = 5, scale = 2)
   private BigDecimal descuento;

   // parcial menos el descuento (parcial x (1 - descuento/100)).
   @Column(name = "subtotal_venta", nullable = false, precision = 14, scale = 2)
   private BigDecimal subtotal;

   // IVA total, calculado por línea según el IVA de cada producto sobre el subtotal con descuento.
   @Column(name = "iva_monto", nullable = false, precision = 14, scale = 2)
   private BigDecimal ivaMonto;

   // Total a pagar (subtotal + ivaMonto).
   @Column(name = "total_venta", nullable = false, precision = 14, scale = 2)
   private BigDecimal total;

   // RELACIÓN: El estado de la venta es un catálogo (COMPLETADA, ANULADA, ...)
   @ManyToOne
   @JoinColumn(
           name = "estado_id",
           nullable = false,
           foreignKey = @ForeignKey(name = "FK_ESTADO_ID_VENTAS")
   )
   private EstadoVenta estado;

   // RELACIÓN: La venta es de un cliente (referencia por cédula)
   @ManyToOne
   @JoinColumn(
           name = "cliente_cedula",
           nullable = false,
           foreignKey = @ForeignKey(name = "FK_CLIENTE_CEDULA_VENTAS"),
           referencedColumnName = "CEDULA"
   )
   private Cliente cliente;

   // RELACIÓN: La venta la hace un usuario (vendedor)
   @ManyToOne
   @JoinColumn(
           name = "usuario_id",
           nullable = false,
           foreignKey = @ForeignKey(name = "FK_USUARIO_ID_VENTAS")
   )
   private User user;

   // RELACIÓN: La venta tiene muchos detalles
   @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
   private List<DetalleVenta> detalleVentas;
}
