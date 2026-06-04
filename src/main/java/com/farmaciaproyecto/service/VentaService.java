package com.farmaciaproyecto.service;

import java.util.List;

import com.farmaciaproyecto.dto.request.CompraRequestDTO;
import com.farmaciaproyecto.dto.response.VentaResponseDTO;

public interface VentaService {

    VentaResponseDTO realizarCompra(String clienteCedula, CompraRequestDTO request);

    List<VentaResponseDTO> listarTodasLasVentas();

    List<VentaResponseDTO> listarVentasPorCliente(String clienteCedula);

    VentaResponseDTO obtenerVentaPorId(Long ventaId);

    void cancelarVenta(Long ventaId);
}
