package com.farmaciaproyecto.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Entity
@Table(
        name = "CLIENTES",
        uniqueConstraints = @UniqueConstraint(name = "PK_CEDULA_CLIENTES", columnNames = "CEDULA")
)
@Setter @Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Cliente {

    @Id
    @Column(name = "cedula", nullable = false, length = 20)
    private String cedula;

    @Column(name = "nombre_cliente", nullable = false, length = 35)
    private String nombre;

    @Column(name = "apellido_cliente", nullable = false, length = 35)
    private String apellido;

    @Column(name = "correo_cliente", unique = true, length = 50)
    private String email;

    @Column(name = "telefono_cliente", nullable = false, length = 15)
    private String telefono;

    @Column(name = "direccion_cliente", nullable = false, length = 50)
    private String direccion;

    @Column(name = "estado_cliente", nullable = false)
    private Boolean activo;

    @OneToMany(mappedBy = "cliente")
    private List<Venta> ventas;

    @OneToOne
    @JoinColumn(
            name = "usuario_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "FK_USUARIO_ID_CLIENTES")
    )
    private User user;
}
