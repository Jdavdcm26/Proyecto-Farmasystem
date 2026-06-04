package com.farmaciaproyecto.service;

import com.farmaciaproyecto.dto.request.RegistroClienteRequestDTO;
import com.farmaciaproyecto.dto.request.UserLoginDTO;
import com.farmaciaproyecto.dto.request.UserRequestDTO;
import com.farmaciaproyecto.dto.response.UserResponseDTO;

public interface UserService {

    UserResponseDTO registrar(RegistroClienteRequestDTO requestDTO);

    UserResponseDTO login(UserLoginDTO loginDTO);

    UserResponseDTO obtenerMiPerfil(Long userId);

    UserResponseDTO actualizarMiPerfil(Long userId, UserRequestDTO requestDTO);

    void eliminarMiCuenta(Long userId);
}
