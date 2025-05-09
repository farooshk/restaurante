package com.retailsoft.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredienteDTO {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    private String descripcion;

    @NotNull(message = "El precio por porción es obligatorio")
    @Min(value = 0, message = "El precio debe ser mayor o igual a 0")
    private Integer precioPorcion;

    private boolean disponible = true;
    private boolean esAdicional = false;
    private Integer cantidadProductos = 0;
}
