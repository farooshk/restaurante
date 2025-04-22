package com.retailsoft.dto;

import com.retailsoft.entity.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {

    private Long id;
    private String username;
    private String nombre;
    private Usuario.TipoUsuario tipoUsuario;
    private boolean activo;
}
