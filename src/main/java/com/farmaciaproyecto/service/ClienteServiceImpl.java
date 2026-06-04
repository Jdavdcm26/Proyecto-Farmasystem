package com.farmaciaproyecto.service;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmaciaproyecto.dto.request.ClienteRequestDTO;
import com.farmaciaproyecto.dto.response.ClienteResponseDTO;
import com.farmaciaproyecto.model.Cliente;
import com.farmaciaproyecto.repository.ClienteRepository;
import com.farmaciaproyecto.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> listarTodosLosClientes() {
        return clienteRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDTO obtenerClientePorCedula(String cedula) {
        Cliente cliente = clienteRepository.findByCedula(cedula)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        return toResponseDTO(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDTO obtenerClientePorUserId(Long userId) {
        Cliente cliente = clienteRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Perfil de cliente no encontrado"));
        return toResponseDTO(cliente);
    }

    @Override
    public ClienteResponseDTO actualizarClientePorCedula(String cedula, ClienteRequestDTO request) {
        Cliente cliente = clienteRepository.findByCedula(cedula)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        cliente.setNombre(request.getNombre());
        cliente.setApellido(request.getApellido());
        cliente.setEmail(request.getEmail());
        cliente.setTelefono(request.getTelefono());
        cliente.setDireccion(request.getDireccion());
        cliente.setActivo(request.getActivo());

        var user = cliente.getUser();
        user.setName(request.getNombre());
        user.setApellido(request.getApellido());

        try {
            userRepository.save(user);
            return toResponseDTO(clienteRepository.save(cliente));
        } catch (DataAccessException ex) {
            throw new RuntimeException(OracleErrorTranslator.translate(ex));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<String> obtenerCedulaPorUserId(Long userId) {
        return clienteRepository.findByUserId(userId).map(Cliente::getCedula);
    }

    private ClienteResponseDTO toResponseDTO(Cliente cliente) {
        return ClienteResponseDTO.builder()
                .cedula(cliente.getCedula())
                .nombre(cliente.getNombre())
                .apellido(cliente.getApellido())
                .email(cliente.getEmail())
                .telefono(cliente.getTelefono())
                .direccion(cliente.getDireccion())
                .estado(cliente.getActivo())
                .userId(cliente.getUser().getId())
                .build();
    }
}
