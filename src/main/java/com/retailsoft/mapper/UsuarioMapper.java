package com.retailsoft.mapper;

import com.retailsoft.dto.UsuarioDTO;
import com.retailsoft.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioDTO toDTO(Usuario usuario) {
        if (usuario == null) return null;

        return UsuarioDTO.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .nombre(usuario.getNombre())
                .tipoUsuario(usuario.getTipoUsuario())
                .activo(usuario.isActivo())
                .build();
    }

    public Usuario toEntity(UsuarioDTO dto) {
        if (dto == null) return null;

        Usuario usuario = new Usuario();
        usuario.setId(dto.getId());
        usuario.setUsername(dto.getUsername());
        usuario.setNombre(dto.getNombre());
        usuario.setTipoUsuario(dto.getTipoUsuario());
        usuario.setActivo(dto.isActivo());
        return usuario;
    }
}
