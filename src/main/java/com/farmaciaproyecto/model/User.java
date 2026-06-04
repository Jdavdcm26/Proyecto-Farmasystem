package com.farmaciaproyecto.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
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
        name = "USUARIOS",
        uniqueConstraints = @UniqueConstraint(name = "PK_ID_USUARIO_USUARIOS", columnNames = "id_usuario")
)
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    @Column(name="id_usuario")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_users")
    @SequenceGenerator(name = "seq_users", sequenceName = "SEQ_USERS", allocationSize = 1)
    private Long id;

    @Column(name = "nombre_usuario", nullable = false, length = 50)
    private String name;

    @Column(name = "apellido_usuario", nullable = false, length = 50)
    private String apellido;

    @Column(name = "username", nullable = false, unique = true, length = 30)
    private String username;

    @Column(name = "correo_usuario", nullable = false, unique = true, length = 50)
    private String email;

    @Column(name = "contraseña", nullable = false, length = 50)
    private String password;

    @Column(name = "rol", nullable = false, length = 10,
            columnDefinition = "VARCHAR2(10) DEFAULT 'CLIENT'")
    @Builder.Default
    private String rol = "CLIENT";

    @OneToMany(mappedBy = "user")
    private List<Producto> productos;

    @OneToMany(mappedBy = "user")
    private List<Categoria> categorias;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Cliente cliente;

    @OneToMany(mappedBy = "user")
    private List<ConsultaIA> consultasIA;
}
