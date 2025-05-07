package com.retailsoft.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumenVentasDTO {

    private LocalDate fecha;
    private int totalPedidos;
    private Integer ventasTotal;
    private List<PedidoDTO> pedidos = new ArrayList<>();
}
