package com.farmaciaproyecto.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
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
        name = "CONSULTAS_IA",
        uniqueConstraints = @UniqueConstraint(name = "PK_ID_CONSULTA_CONSULTAS_IA", columnNames = "id_consulta")
)
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConsultaIA {

    @Id
    @Column(name="id_consulta")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_consultas_ia")
    @SequenceGenerator(name = "seq_consultas_ia", sequenceName = "SEQ_CONSULTAS_IA", allocationSize = 1)
    private Long id;

    @Column(name = "sintomas", nullable = false, length = 2000)
    private String sintomas;

    @Lob
    @Column(name = "respuesta", nullable = false)
    private String respuesta;

    @Column(name = "fecha_consulta", nullable = false)
    private LocalDateTime fecha;

    @ManyToOne
    @JoinColumn(
            name = "usuario_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_USUARIO_ID_CONSULTAS_IA")
    )
    private User user;
}
