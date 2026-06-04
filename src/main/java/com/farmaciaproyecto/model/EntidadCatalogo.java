package com.farmaciaproyecto.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Clase base para entidades de catálogo que comparten nombre y descripción.
 * Aplica @MappedSuperclass: sus campos se mapean a las tablas de las entidades hijas,
 * no genera tabla propia.
 */
@MappedSuperclass
@SuperBuilder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class EntidadCatalogo {

    @Column(nullable = false, length = 35)
    protected String nombre;

    @Column(length = 100)
    protected String descripcion;
}
