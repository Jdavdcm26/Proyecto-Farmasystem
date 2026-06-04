package com.farmaciaproyecto.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmaciaproyecto.dto.request.CompraRequestDTO;
import com.farmaciaproyecto.dto.response.DetalleVentaResponseDTO;
import com.farmaciaproyecto.dto.response.VentaResponseDTO;
import com.farmaciaproyecto.model.Cliente;
import com.farmaciaproyecto.model.DetalleVenta;
import com.farmaciaproyecto.model.EstadoVenta;
import com.farmaciaproyecto.model.Lote;
import com.farmaciaproyecto.model.Producto;
import com.farmaciaproyecto.model.User;
import com.farmaciaproyecto.model.Venta;
import com.farmaciaproyecto.repository.ClienteRepository;
import com.farmaciaproyecto.repository.EstadoVentaRepository;
import com.farmaciaproyecto.repository.ProductoRepository;
import com.farmaciaproyecto.repository.UserRepository;
import com.farmaciaproyecto.repository.VentaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final UserRepository userRepository;
    private final EstadoVentaRepository estadoVentaRepository;

    private static final String ESTADO_COMPLETADA = "COMPLETADA";
    private static final String ESTADO_ANULADA = "ANULADA";
    private static final BigDecimal CIEN = new BigDecimal("100");

    @Override
    public VentaResponseDTO realizarCompra(String clienteCedula, CompraRequestDTO request) {

        Cliente cliente = clienteRepository.findByCedula(clienteCedula)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        User admin = userRepository.findByRol("ADMIN")
                .orElseThrow(() -> new RuntimeException("Administrador no configurado"));

        String numeroVenta = "VTA-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        EstadoVenta estadoCompletada = estadoVentaRepository.findByNombre(ESTADO_COMPLETADA)
                .orElseThrow(() -> new RuntimeException("Estado COMPLETADA no configurado"));

        Venta venta = new Venta();
        venta.setNumeroVenta(numeroVenta);
        venta.setFechaVenta(LocalDateTime.now());
        venta.setEstado(estadoCompletada);
        venta.setCliente(cliente);
        venta.setUser(admin);

        List<DetalleVenta> detalles = request.getDetalleVentas().stream()
                .map(d -> {
                    Producto producto = productoRepository.findById(d.getProductoId())
                            .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + d.getProductoId()));

                    if (!Boolean.TRUE.equals(producto.getActivo())) {
                        throw new RuntimeException("El producto no está disponible: " + producto.getNombre());
                    }

                    Lote lote = seleccionarLoteFifo(producto, d.getCantidad());

                    lote.setStock(lote.getStock() - d.getCantidad());
                    producto.setStock(producto.getStock() - d.getCantidad());

                    BigDecimal subtotal = lote.getPrecioVenta()
                            .multiply(BigDecimal.valueOf(d.getCantidad()));

                    DetalleVenta detalle = new DetalleVenta();
                    detalle.setVenta(venta);
                    detalle.setProducto(producto);
                    detalle.setLote(lote);
                    detalle.setCantidad(d.getCantidad());
                    detalle.setPrecioUnitario(lote.getPrecioVenta());
                    detalle.setSubtotal(subtotal);

                    return detalle;
                })
                .collect(java.util.stream.Collectors.toList());

        BigDecimal descuento = request.getDescuento() != null
                ? request.getDescuento()
                : BigDecimal.ZERO;

        BigDecimal parcial = detalles.stream()
                .map(DetalleVenta::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal factorDescuento = BigDecimal.ONE.subtract(
                descuento.divide(CIEN, 4, RoundingMode.HALF_UP));

        BigDecimal subtotal = parcial.multiply(factorDescuento)
                .setScale(2, RoundingMode.HALF_UP);

        // IVA por línea: aplica el % del producto sobre la línea ya con descuento.
        BigDecimal ivaMonto = detalles.stream()
                .map(d -> d.getSubtotal()
                        .multiply(factorDescuento)
                        .multiply(d.getProducto().getIva())
                        .divide(CIEN, 4, RoundingMode.HALF_UP))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal total = subtotal.add(ivaMonto).setScale(2, RoundingMode.HALF_UP);

        venta.setParcial(parcial.setScale(2, RoundingMode.HALF_UP));
        venta.setDescuento(descuento);
        venta.setSubtotal(subtotal);
        venta.setIvaMonto(ivaMonto);
        venta.setTotal(total);
        venta.setDetalleVentas(detalles);

        try {
            return toResponseDTO(ventaRepository.save(venta));
        } catch (org.springframework.dao.DataAccessException ex) {
            throw new RuntimeException(OracleErrorTranslator.translate(ex));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaResponseDTO> listarTodasLasVentas() {
        return ventaRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaResponseDTO> listarVentasPorCliente(String clienteCedula) {
        return ventaRepository.findByClienteCedula(clienteCedula)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public VentaResponseDTO obtenerVentaPorId(Long ventaId) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        return toResponseDTO(venta);
    }

    @Override
    public void cancelarVenta(Long ventaId) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        if (venta.getEstado() != null && ESTADO_ANULADA.equals(venta.getEstado().getNombre())) {
            throw new RuntimeException("La venta ya está anulada");
        }

        EstadoVenta estadoAnulada = estadoVentaRepository.findByNombre(ESTADO_ANULADA)
                .orElseThrow(() -> new RuntimeException("Estado ANULADA no configurado"));

        venta.getDetalleVentas().forEach(d -> {
            Lote lote = d.getLote();
            if (lote != null) {
                lote.setStock(lote.getStock() + d.getCantidad());
            }
            Producto p = d.getProducto();
            p.setStock(p.getStock() + d.getCantidad());
        });

        venta.setEstado(estadoAnulada);
        try {
            ventaRepository.save(venta);
        } catch (org.springframework.dao.DataAccessException ex) {
            throw new RuntimeException(OracleErrorTranslator.translate(ex));
        }
    }

    /**
     * Selecciona el lote activo con la fecha de vencimiento más cercana que
     * tenga stock suficiente para cubrir la cantidad solicitada (FIFO por
     * caducidad). Lanza RuntimeException si ningún lote del producto puede
     * cubrir la cantidad solicitada en una sola entrega.
     */
    private Lote seleccionarLoteFifo(Producto producto, int cantidad) {
        if (producto.getLotes() == null || producto.getLotes().isEmpty()) {
            throw new RuntimeException("El producto no tiene lotes registrados: " + producto.getNombre());
        }
        return producto.getLotes().stream()
                .filter(l -> Boolean.TRUE.equals(l.getActivo()))
                .filter(l -> l.getStock() != null && l.getStock() >= cantidad)
                .min(Comparator.comparing(Lote::getFechaVencimiento))
                .orElseThrow(() -> new RuntimeException(
                        "Stock insuficiente en lotes disponibles para: " + producto.getNombre()));
    }

    private VentaResponseDTO toResponseDTO(Venta venta) {
        return VentaResponseDTO.builder()
                .id(venta.getId())
                .numeroVenta(venta.getNumeroVenta())
                .fechaVenta(venta.getFechaVenta())
                .parcial(venta.getParcial())
                .descuento(venta.getDescuento())
                .subtotal(venta.getSubtotal())
                .ivaMonto(venta.getIvaMonto())
                .total(venta.getTotal())
                .estadoId(venta.getEstado() != null ? venta.getEstado().getId() : null)
                .estado(venta.getEstado() != null ? venta.getEstado().getNombre() : null)
                .clienteCedula(venta.getCliente().getCedula())
                .clienteNombre(venta.getCliente().getNombre() + " " + venta.getCliente().getApellido())
                .usuarioId(venta.getUser().getId())
                .usuarioNombre(venta.getUser().getName())
                .detalleVentas(
                        venta.getDetalleVentas().stream().map(d ->
                                DetalleVentaResponseDTO.builder()
                                        .id(d.getId())
                                        .productoId(d.getProducto().getId())
                                        .productoNombre(d.getProducto().getNombre())
                                        .loteId(d.getLote() != null ? d.getLote().getId() : null)
                                        .numeroLote(d.getLote() != null ? d.getLote().getNumeroLote() : null)
                                        .cantidad(d.getCantidad())
                                        .precioUnitario(d.getPrecioUnitario())
                                        .subtotal(d.getSubtotal())
                                        .build()
                        ).toList()
                )
                .build();
    }
}
