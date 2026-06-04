package com.farmaciaproyecto.service;

import java.util.List;
import java.util.Optional;

import com.farmaciaproyecto.dto.request.ClienteRequestDTO;
import com.farmaciaproyecto.dto.response.ClienteResponseDTO;

public interface ClienteService {

    List<ClienteResponseDTO> listarTodosLosClientes();

    ClienteResponseDTO obtenerClientePorCedula(String cedula);

    ClienteResponseDTO obtenerClientePorUserId(Long userId);

    ClienteResponseDTO actualizarClientePorCedula(String cedula, ClienteRequestDTO request);

    Optional<String> obtenerCedulaPorUserId(Long userId);
}