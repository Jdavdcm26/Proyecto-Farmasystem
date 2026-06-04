package com.farmaciaproyecto.model;

import java.util.List;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "ESTADOS_VENTA",
        uniqueConstraints = @UniqueConstraint(name = "UK_NOMBRE_ESTADOS_VENTA", columnNames = "nombre_estado")
)
@AttributeOverrides({
        @AttributeOverride(name = "nombre",      column = @Column(name = "nombre_estado",      nullable = false, length = 20)),
        @AttributeOverride(name = "descripcion", column = @Column(name = "descripcion_estado", length = 100))
})
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class EstadoVenta extends EntidadCatalogo {

    @Id
    @Column(name="id_estado_venta")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_estados_venta")
    @SequenceGenerator(name = "seq_estados_venta", sequenceName = "SEQ_ESTADOS_VENTA", allocationSize = 1)
    private Long id;

    @OneToMany(mappedBy = "estado")
     private List<Venta> ventas;
}
