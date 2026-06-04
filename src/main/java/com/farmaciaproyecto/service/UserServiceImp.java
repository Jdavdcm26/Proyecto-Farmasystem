package com.farmaciaproyecto.service;

import com.farmaciaproyecto.dto.request.RegistroClienteRequestDTO;
import com.farmaciaproyecto.dto.request.UserLoginDTO;
import com.farmaciaproyecto.dto.request.UserRequestDTO;
import com.farmaciaproyecto.dto.response.UserResponseDTO;
import com.farmaciaproyecto.model.Cliente;
import com.farmaciaproyecto.model.User;
import com.farmaciaproyecto.repository.ClienteRepository;
import com.farmaciaproyecto.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final ClienteRepository clienteRepository;

    @Override
    @Transactional
    public UserResponseDTO registrar(RegistroClienteRequestDTO requestDTO) {
        log.info("Registrando nuevo cliente: {}", requestDTO.getUsername());

        if (userRepository.existsByUsername(requestDTO.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }
        if (userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new IllegalArgumentException("El correo electrónico ya está registrado");
        }
        if (clienteRepository.existsByCedula(requestDTO.getCedula())) {
            throw new IllegalArgumentException("La cédula ya está registrada");
        }

        User user = User.builder()
                .name(requestDTO.getName())
                .apellido(requestDTO.getApellido())
                .username(requestDTO.getUsername())
                .email(requestDTO.getEmail())
                .password(requestDTO.getPassword())
                .rol("CLIENT")
                .build();

        User userGuardado;
        try {
            userGuardado = userRepository.save(user);
        } catch (DataAccessException ex) {
            throw new RuntimeException(OracleErrorTranslator.translate(ex));
        }

        Cliente cliente = Cliente.builder()
                .cedula(requestDTO.getCedula())
                .nombre(requestDTO.getName())
                .apellido(requestDTO.getApellido())
                .email(requestDTO.getEmail())
                .telefono(requestDTO.getTelefono())
                .direccion(requestDTO.getDireccion())
                .activo(true)
                .user(userGuardado)
                .build();

        try {
            clienteRepository.save(cliente);
        } catch (DataAccessException ex) {
            throw new RuntimeException(OracleErrorTranslator.translate(ex));
        }

        log.info("Cliente registrado con ID: {} y cédula: {}", userGuardado.getId(), requestDTO.getCedula());
        return convertirAResponseDTO(userGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO login(UserLoginDTO loginDTO) {
        log.info("Intento de login: {}", loginDTO.getUsername());

        User user = userRepository.findByUsername(loginDTO.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuario o contraseña incorrectos"));

        if (!user.getPassword().equals(loginDTO.getPassword())) {
            throw new IllegalArgumentException("Usuario o contraseña incorrectos");
        }

        log.info("Login exitoso para usuario ID: {} rol: {}", user.getId(), user.getRol());
        return convertirAResponseDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO obtenerMiPerfil(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return convertirAResponseDTO(user);
    }

    @Override
    @Transactional
    public UserResponseDTO actualizarMiPerfil(Long userId, UserRequestDTO requestDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (!user.getUsername().equals(requestDTO.getUsername())
                && userRepository.existsByUsername(requestDTO.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }
        if (!user.getEmail().equals(requestDTO.getEmail())
                && userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new IllegalArgumentException("El correo electrónico ya está registrado");
        }

        user.setName(requestDTO.getName());
        user.setApellido(requestDTO.getApellido());
        user.setUsername(requestDTO.getUsername());
        user.setEmail(requestDTO.getEmail());

        if (requestDTO.getPassword() != null && !requestDTO.getPassword().isBlank()) {
            if (requestDTO.getPassword().length() < 6)
                throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
            user.setPassword(requestDTO.getPassword());
        }

        try {
            clienteRepository.findByUserId(userId).ifPresent(cliente -> {
                cliente.setNombre(requestDTO.getName());
                cliente.setApellido(requestDTO.getApellido());
                clienteRepository.save(cliente);
            });
            return convertirAResponseDTO(userRepository.save(user));
        } catch (DataAccessException ex) {
            throw new RuntimeException(OracleErrorTranslator.translate(ex));
        }
    }

    @Override
    @Transactional
    public void eliminarMiCuenta(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        userRepository.deleteById(userId);
    }

    private UserResponseDTO convertirAResponseDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .apellido(user.getApellido())
                .nombreCompleto(user.getName() + " " + user.getApellido())
                .username(user.getUsername())
                .email(user.getEmail())
                .rol(user.getRol())
                .build();
    }
}
