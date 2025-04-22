package com.retailsoft.dto;

import com.retailsoft.entity.Pedido;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO {

    private Long id;
    private LocalDateTime fechaHora;
    private String mesa;
    private String usuarioNombre;
    private List<ItemPedidoDTO> items = new ArrayList<>();
    private Integer total;
    private Pedido.EstadoPedido estado;
    private boolean comandaImpresa;
    private boolean anulado;
    private String motivoAnulacion;
}
