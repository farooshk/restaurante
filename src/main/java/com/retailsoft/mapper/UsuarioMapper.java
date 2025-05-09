package com.retailsoft.mapper;

import com.retailsoft.dto.RolDTO;
import com.retailsoft.dto.UsuarioDTO;
import com.retailsoft.entity.Usuario;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UsuarioMapper {

    public UsuarioDTO toDTO(Usuario usuario) {
        if (usuario == null) return null;

        return UsuarioDTO.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .email(usuario.getEmail())
                .activo(usuario.isActivo())
                // No incluir contraseña por seguridad
                .password("")
                // Si quieres incluir roles, debes mapearlos también
                .roles(usuario.getRoles() != null ?
                        usuario.getRoles().stream()
                                .map(rol -> new RolDTO(rol.getId(), rol.getNombre()))
                                .collect(Collectors.toList()) :
                        null)
                .build();
    }

    public Usuario toEntity(UsuarioDTO dto) {
        if (dto == null) return null;

        Usuario usuario = new Usuario();
        usuario.setId(dto.getId());
        usuario.setUsername(dto.getUsername());
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setEmail(dto.getEmail());

        // Si el DTO tiene password y no está vacío, asignarlo
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            usuario.setPassword(dto.getPassword());
        }

        usuario.setActivo(dto.isActivo());

        // Si quieres mapear tipoUsuario, necesitas determinarlo de alguna forma
        // Por ejemplo, basándote en los roles
        if (dto.getRoles() != null && !dto.getRoles().isEmpty()) {
            boolean esAdmin = dto.getRoles().stream()
                    .anyMatch(rol -> "ROLE_ADMIN".equals(rol.getNombre()));
            usuario.setTipoUsuario(esAdmin ? Usuario.TipoUsuario.ADMINISTRADOR : Usuario.TipoUsuario.MESERO);
        }

        return usuario;
    }
}
