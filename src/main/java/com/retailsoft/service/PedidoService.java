package com.retailsoft.service;

import com.retailsoft.dto.ComandaDTO;
import com.retailsoft.dto.PedidoDTO;
import com.retailsoft.dto.ResumenVentasDTO;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PedidoService {

    List<PedidoDTO> listarPedidosDelDia();
    Optional<PedidoDTO> buscarPorId(Long id);
    PedidoDTO crearPedido(PedidoDTO pedidoDTO);
    PedidoDTO actualizarPedido(PedidoDTO pedidoDTO);
    void anularPedido(Long id, String motivo);
    ComandaDTO generarComanda(Long pedidoId);
    boolean marcarComandaImpresa(Long pedidoId);
    ResumenVentasDTO obtenerResumenVentas(LocalDate fecha);
}
