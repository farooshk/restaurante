package com.retailsoft.dto;

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
public class ComandaDTO {

    private Long pedidoId;
    private LocalDateTime fechaHora;
    private String mesa;
    private String mesero;
    private List<ItemComandaDTO> items = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemComandaDTO {
        private String categoria;
        private String productoNombre;
        private Integer cantidad;
        private String observaciones;
        private List<String> ingredientesAdicionales = new ArrayList<>();
        private List<String> ingredientesEliminados = new ArrayList<>();
    }
}
