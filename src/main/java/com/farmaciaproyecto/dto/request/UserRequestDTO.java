package com.farmaciaproyecto.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede tener más de 50 caracteres")
    private String name;
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
    @Size(max = 50, message = "La contraseña no puede tener más de 50 caracteres")
    private String password;

}
