package com.farmaciaproyecto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.farmaciaproyecto.model.EstadoVenta;
import com.farmaciaproyecto.model.User;
import com.farmaciaproyecto.repository.EstadoVentaRepository;
import com.farmaciaproyecto.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final EstadoVentaRepository estadoVentaRepository;

    @Value("${admin.username:admin}")
    private String adminUsername;

    @Value("${admin.password:Admin123}")
    private String adminPassword;

    @Value("${admin.email:admin@farmasystem.com}")
    private String adminEmail;

    @Value("${admin.name:Administrador}")
    private String adminName;

    @Value("${admin.apellido:Sistema}")
    private String adminApellido;

    @Override
    public void run(String... args) {
        seedAdmin();
        seedEstadosVenta();
    }

    private void seedAdmin() {
        if (!userRepository.existsByRol("ADMIN")) {
            User admin = User.builder()
                    .name(adminName)
                    .apellido(adminApellido)
                    .username(adminUsername)
                    .email(adminEmail)
                    .password(adminPassword)
                    .rol("ADMIN")
                    .build();
            userRepository.save(admin);
            log.info("Admin inicial creado: username={}", adminUsername);
        } else {
            log.info("Admin ya existe, no se crea uno nuevo.");
        }
    }

    private void seedEstadosVenta() {
        crearEstadoSiNoExiste("COMPLETADA", "Venta finalizada y procesada");
        crearEstadoSiNoExiste("ANULADA", "Venta cancelada con stock devuelto");
        crearEstadoSiNoExiste("PENDIENTE", "Venta en proceso");
    }

    private void crearEstadoSiNoExiste(String nombre, String descripcion) {
        if (!estadoVentaRepository.existsByNombre(nombre)) {
            estadoVentaRepository.save(EstadoVenta.builder()
                    .nombre(nombre)
                    .descripcion(descripcion)
                    .build());
            log.info("Estado de venta sembrado: {}", nombre);
        }
    }
}
