package com.farmaciaproyecto.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import java.util.List;

@Entity
@Table(
        name = "CATEGORIAS",
        uniqueConstraints = @UniqueConstraint(name = "PK_ID_CATEGORIAS", columnNames = "id_categoria")
)
@AttributeOverrides({
        @AttributeOverride(name = "nombre",      column = @Column(name = "nombre_categoria",      nullable = false, unique = true, length = 35)),
        @AttributeOverride(name = "descripcion", column = @Column(name = "descripcion_categoria", length = 50))
})
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Categoria extends EntidadCatalogo {

    @Id
    @Column(name = "id_categoria")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_categorias")
    @SequenceGenerator(name = "seq_categorias", sequenceName = "SEQ_CATEGORIAS", allocationSize = 1)
    private Long id;

    @Column(name = "estado_categoria", nullable = false)
    private Boolean activo;

    @OneToMany(mappedBy = "categoria")
    private List<Producto> productos;

    @ManyToOne
    @JoinColumn(
            name = "usuario_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_USUARIO_ID_CATEGORIAS")
    )
    private User user;
}
