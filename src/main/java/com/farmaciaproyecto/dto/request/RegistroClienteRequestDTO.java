package com.farmaciaproyecto.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistroClienteRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede tener más de 50 caracteres")
    private String name;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 50, message = "El apellido no puede tener más de 50 caracteres")
    private String apellido;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(max = 30, message = "El nombre de usuario no puede tener más de 30 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "El nombre de usuario solo puede contener letras, números y guiones bajos")
    private String username;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El correo electrónico debe ser válido")
    @Size(max = 50, message = "El correo electrónico no puede tener más de 50 caracteres")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 50, message = "La contraseña debe tener entre 6 y 50 caracteres")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]+$", message = "La contraseña debe contener al menos una letra y un número")
    private String password;

    @NotBlank(message = "La cédula es obligatoria")
    @Size(max = 20, message = "La cédula no puede tener más de 20 caracteres")
    private String cedula;

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 15, message = "El teléfono no puede tener más de 15 caracteres")
    private String telefono;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 50, message = "La dirección no puede tener más de 50 caracteres")
    private String direccion;
}