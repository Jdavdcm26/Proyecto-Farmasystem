package com.farmaciaproyecto.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClienteRequestDTO {
   @NotBlank(message = "La cédula es obligatoria")
   @Size(max=20,message="La cédula no puede exceder los 20 caracteres")
   private String cedula;
   @NotBlank(message = "El nombre es obligatorio")
   @Size(max=35,message="El nombre del cliente no puede exceder los 35 caracteres")
   private String nombre;
   @NotBlank(message = "El apellido es obligatorio")
   @Size(max=35,message="El nombre del cliente no puede exceder los 35 caracteres")
   private String apellido;
   @Email(message = "El email debe ser valido")
   @Size(max=50,message="El email no puede exceder los 50 caracteres")
   private String email;
   @NotBlank(message = "El telefono es obligatorio")
   @Size(max=15,min=6,message="El telefono debe estar entre 6 y 15 numeros")
   private String telefono;
   @NotBlank(message = "La direccion es obligatoria")
   @Size(max=50,message="La direccion no puede exceder los 50 caracteres")
   private String direccion;
   @NotNull(message = "El estado del cliente es obligatorio")
   private Boolean activo;
   @NotNull(message = "El ID del usuario es obligatorio")
   private Long usuarioId;

}
