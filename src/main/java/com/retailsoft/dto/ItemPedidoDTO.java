package com.retailsoft.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedidoDTO {

    private Long id;
    private Long productoId;
    private String productoNombre;
    private Integer cantidad;
    private Integer precioUnitario;
    private Integer subtotal;
    private String observaciones;
    private List<IngredienteDTO> ingredientesAdicionales = new ArrayList<>();
    private List<IngredienteDTO> ingredientesEliminados = new ArrayList<>();
}
