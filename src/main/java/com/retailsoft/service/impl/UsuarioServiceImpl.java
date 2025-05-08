package com.retailsoft.service.impl;

import com.retailsoft.dto.UsuarioDTO;
import com.retailsoft.entity.Usuario;
import com.retailsoft.repository.PedidoRepository;
import com.retailsoft.repository.UsuarioRepository;
import com.retailsoft.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UsuarioDTO> buscarPorId(Long id) {
        return usuarioRepository.findById(id).map(this::convertirADTO);
    }

    @Override
    public UsuarioDTO guardar(UsuarioDTO usuarioDTO, String password) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean tienePedidos(Long id) {
        return pedidoRepository.existsByUsuarioId(id);
    }

    @Override
    public Optional<UsuarioDTO> buscarPorUsername(String username) {
        return Optional.empty();
    }

    @Override
    public void inactivar(Long id) {

    }

    @Override
    public boolean existePorUsername(String username) {
        return false;
    }

    @Override
    @Transactional
    public UsuarioDTO guardar(UsuarioDTO usuarioDTO) {
        Usuario usuario;

        // Si es actualización, obtener la entidad existente
        if (usuarioDTO.getId() != null) {
            usuario = usuarioRepository.findById(usuarioDTO.getId())
                    .orElse(new Usuario());

            // Si no se proporciona una contraseña, mantener la existente
            if (usuarioDTO.getPassword() == null || usuarioDTO.getPassword().isEmpty()) {
                usuarioDTO.setPassword(usuario.getPassword());
            } else {
                // Si se proporciona una nueva contraseña, encriptarla
                usuarioDTO.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
            }
        } else {
            usuario = new Usuario();
            // Encriptar contraseña para nuevos usuarios
            usuarioDTO.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
        }

        // Actualizar propiedades básicas
        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setApellido(usuarioDTO.getApellido());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setUsername(usuarioDTO.getUsername());
        usuario.setPassword(usuarioDTO.getPassword());
        usuario.setActivo(usuarioDTO.isActivo());

        // Actualizar roles
        if (usuarioDTO.getRoles() != null) {
            Set<Rol> roles = usuarioDTO.getRoles().stream()
                    .map(dto -> {
                        Rol rol = new Rol();
                        rol.setId(dto.getId());
                        return rol;
                    })
                    .collect(Collectors.toSet());
            usuario.setRoles(roles);
        }

        // Guardar y convertir a DTO
        usuario = usuarioRepository.save(usuario);

        return convertirADTO(usuario);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    public List<UsuarioDTO> listarUsuariosQueTomanPedidos() {
        return List.of();
    }

    private UsuarioDTO convertirADTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setEmail(usuario.getEmail());
        dto.setUsername(usuario.getUsername());
        dto.setPassword(""); // No enviar la contraseña en el DTO
        dto.setActivo(usuario.isActivo());

        // Convertir roles
        if (usuario.getRoles() != null) {
            List<RolDTO> rolesDTO = usuario.getRoles().stream()
                    .map(rol -> {
                        RolDTO rolDTO = new RolDTO();
                        rolDTO.setId(rol.getId());
                        rolDTO.setNombre(rol.getNombre());
                        return rolDTO;
                    })
                    .collect(Collectors.toList());
            dto.setRoles(rolesDTO);
        }

        return dto;
    }
}
