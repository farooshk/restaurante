package com.retailsoft.service.impl;

import com.retailsoft.dto.UsuarioDTO;
import com.retailsoft.entity.Usuario;
import com.retailsoft.mapper.UsuarioMapper;
import com.retailsoft.repository.UsuarioRepository;
import com.retailsoft.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              @Lazy PasswordEncoder passwordEncoder, UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioMapper = usuarioMapper;
    }

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
    @Transactional(readOnly = true)
    public Optional<UsuarioDTO> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username).map(this::convertirADTO);
    }

    @Override
    @Transactional
    public UsuarioDTO guardar(UsuarioDTO usuarioDTO, String password) {
        Usuario usuario = convertirAEntidad(usuarioDTO);
        if (password != null && !password.isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(password));
        }
        usuario = usuarioRepository.save(usuario);
        return convertirADTO(usuario);
    }

    @Override
    @Transactional
    public void inactivar(Long id) {
        usuarioRepository.findById(id).ifPresent(usuario -> {
            // Evitar eliminar al usuario superadministrador
            if (usuario.getUsername().equalsIgnoreCase("sistemas")) {
                throw new IllegalStateException("No se puede eliminar al usuario superadministrador.");
            }
            usuario.setActivo(false);
            usuarioRepository.save(usuario);
        });
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        usuarioRepository.findById(id).ifPresent(usuario -> {
            // Evitar eliminar al usuario superadministrador
            if (usuario.getUsername().equalsIgnoreCase("sistemas")) {
                throw new IllegalStateException("No se puede eliminar al usuario superadministrador.");
            }
            usuarioRepository.delete(usuario);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }

    @Override
    public List<UsuarioDTO> listarUsuariosQueTomanPedidos() {
        return usuarioRepository.findByTipoUsuarioIn(List.of(Usuario.TipoUsuario.MESERO, Usuario.TipoUsuario.ADMINISTRADOR))
                .stream()
                .map(usuarioMapper::toDTO)
                .toList();
    }

    private UsuarioDTO convertirADTO(Usuario usuario) {
        return UsuarioDTO.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .nombre(usuario.getNombre())
                .tipoUsuario(usuario.getTipoUsuario())
                .activo(usuario.isActivo())
                .build();
    }

    private Usuario convertirAEntidad(UsuarioDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setId(dto.getId());
        usuario.setUsername(dto.getUsername());
        usuario.setNombre(dto.getNombre());
        usuario.setTipoUsuario(dto.getTipoUsuario());
        usuario.setActivo(dto.isActivo());
        return usuario;
    }
}
