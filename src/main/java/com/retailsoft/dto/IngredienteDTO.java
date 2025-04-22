package com.retailsoft.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredienteDTO {

    private Long id;
    private String nombre;
    private Integer precioPorcion;
    private boolean esAdicional;
}
