package com.retailsoft.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaDTO {

    private Long id;
    private String nombre;
    private String urlImagen;
    private int cantidadProductos;
}
