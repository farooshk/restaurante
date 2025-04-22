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
public class ProductoDTO {

    private Long id;
    private String nombre;
    private Integer precio;
    private String descripcion;
    private Long categoriaId;
    private String categoriaNombre;
    private boolean activo;
    private List<IngredienteDTO> ingredientesBase = new ArrayList<>();
}
